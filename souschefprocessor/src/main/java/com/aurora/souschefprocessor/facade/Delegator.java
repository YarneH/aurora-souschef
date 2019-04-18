package com.aurora.souschefprocessor.facade;


import android.util.Log;

import com.aurora.auroralib.ExtractedText;
import com.aurora.souschefprocessor.recipe.Recipe;
import com.aurora.souschefprocessor.task.AbstractProcessingTask;
import com.aurora.souschefprocessor.task.RecipeInProgress;
import com.aurora.souschefprocessor.task.helpertasks.NonParallelizeStepTask;
import com.aurora.souschefprocessor.task.helpertasks.ParallelizeStepsTask;
import com.aurora.souschefprocessor.task.helpertasks.StepTaskNames;
import com.aurora.souschefprocessor.task.ingredientdetector.DetectIngredientsInListTask;
import com.aurora.souschefprocessor.task.ingredientdetector.DetectIngredientsInStepTask;
import com.aurora.souschefprocessor.task.sectiondivider.DetectNumberOfPeopleTask;
import com.aurora.souschefprocessor.task.sectiondivider.SplitStepsTask;
import com.aurora.souschefprocessor.task.sectiondivider.SplitToMainSectionsTask;
import com.aurora.souschefprocessor.task.timerdetector.DetectTimersInStepTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotator;
import edu.stanford.nlp.pipeline.POSTaggerAnnotator;
import edu.stanford.nlp.pipeline.TokenizerAnnotator;
import edu.stanford.nlp.pipeline.WordsToSentencesAnnotator;

/**
 * Implements the processing by applying the filters. This implements the order of the pipeline as
 * described in the architecture.
 */
public class Delegator {

    /**
     * A constant describing 1/2
     */
    private static final double HALF = 0.5;
    /**
     * An object that serves as a lock to ensure that the pipelines are only created once
     */
    private static final Object LOCK = new Object();
    /**
     * A boolean that indicates if the pipelines have been created (or the creation has started)
     */
    private static boolean sStartedCreatingPipelines = false;

    //TODO Maybe all threadpool stuff can be moved to ParallelizeSteps


    /**
     * A threadPoolExecutor to execute steps in parallel
     */
    private static ThreadPoolExecutor sThreadPoolExecutor;

    /**
     * A list of basic annotators needed for every step that has a pipeline (tokenizer, wordstosentence
     * and POS)
     */
    private static List<Annotator> basicAnnotators = new ArrayList<>();

    /*
     * Makes sure that the {@link #createAnnotationPipelines()} method is always called if a delegator is
     * used, to ensure that the pipelines have been created
     */
    static {
        createAnnotationPipelines();
    }

    /**
     * The classifier to classify ingredients
     */
    private CRFClassifier<CoreLabel> mIngredientClassifier;
    /**
     * A boolean that indicates whether the processing should be parallelized
     */
    private boolean mParallelize;

    /**
     * Creating the delegator
     *
     * @param ingredientClassifier the classifier to classify the ingredients
     * @param parallelize          boolean to indicate wheter to parallelize or not
     */
    Delegator(CRFClassifier<CoreLabel> ingredientClassifier, boolean parallelize) {

        mIngredientClassifier = ingredientClassifier;
        mParallelize = parallelize;

    }

    /**
     * Creates the annotation pipelines for the {@link DetectIngredientsInStepTask} and
     * {@link DetectTimersInStepTask} if the creation has not started yet
     */
    static void createAnnotationPipelines() {
        synchronized (LOCK) {

            if (sStartedCreatingPipelines) {
                // creating already started or finished -> do not start again
                return;
            }
            // ensure no other thread starts creating pipelines
            sStartedCreatingPipelines = true;
            LOCK.notifyAll();
        }
        Thread t = new Thread(() -> {
            createBasicAnnotators();
            DetectTimersInStepTask.initializeAnnotationPipeline(basicAnnotators);
            DetectIngredientsInStepTask.initializeAnnotationPipeline(basicAnnotators);
        });
        t.start();
    }

    /**
     * Creates the basicannotators (tokenizer, words to sentence and POS)
     *
     * @return the list of basicAnnotators
     */
    private static List<Annotator> createBasicAnnotators() {
        synchronized (basicAnnotators) {
            if (basicAnnotators.isEmpty()) {

                basicAnnotators.add(new TokenizerAnnotator(false, "en"));
                incrementProgressAnnotationPipelines();
                basicAnnotators.add(new WordsToSentencesAnnotator(false));
                incrementProgressAnnotationPipelines();
                basicAnnotators.add(new POSTaggerAnnotator(false));
                incrementProgressAnnotationPipelines();

            }
            basicAnnotators.notifyAll();
        }
        return basicAnnotators;
    }

    /**
     * Increments the {@link Communicator#mProgressAnnotationPipelines} value of the communicator
     */
    public static void incrementProgressAnnotationPipelines() {
        Communicator.incrementProgressAnnotationPipelines();

    }

    /**
     * Creates the ThreadPoolExecutor for the processing of the text, this is device-dependent
     */
    private static void setUpThreadPool() {
        /*
         * Gets the number of available cores
         * (not always the same as the maximum number of cores)
         * the processing is faster if this only half of the available cores to limit context
         * switching
         */
        int numberOfCores = (int)
                (Runtime.getRuntime().availableProcessors() * HALF);
        // A queue of Runnables
        final BlockingQueue<Runnable> decodeWorkQueue;
        // Instantiates the queue of Runnables as a LinkedBlockingQueue
        decodeWorkQueue = new LinkedBlockingQueue<>();
        // Sets the amount of time an idle thread waits before terminating
        final int KEEP_ALIVE_TIME = 1;
        // Sets the Time Unit to seconds
        final TimeUnit keepAliveTimeUnit = TimeUnit.SECONDS;
        // Creates a thread pool manager
        sThreadPoolExecutor = new ThreadPoolExecutor(
                // Initial pool size
                numberOfCores,
                // Max pool size
                numberOfCores,
                KEEP_ALIVE_TIME,
                keepAliveTimeUnit,
                decodeWorkQueue);
    }


    /**
     * This is the core function of the delegator, where the text is processed by applying the filters
     * This function should be able to at run time decide to do certain filters or not (graceful degradation)
     *
     * @param text The text to be processed in to a recipe Object
     * @return A Recipe object that was constructed from the text
     */
    public Recipe processText(ExtractedText text) {
        //TODO implement this function so that at runtime it is decided which tasks should be performed
        if (sThreadPoolExecutor == null) {
            setUpThreadPool();
        }
        RecipeInProgress recipeInProgress = new RecipeInProgress(text);
        List<AbstractProcessingTask> pipeline = setUpPipeline(recipeInProgress);
        if (pipeline != null) {
            for (AbstractProcessingTask task : pipeline) {
                task.doTask();
                Log.d("DELEGATOR", task.getClass().toString());
            }
        }


        return recipeInProgress.convertToRecipe();
    }

    /**
     * This is the core function of the delegator, where the text is processed by applying the filters
     * This function should be able to at run time decide to do certain filters or not (graceful degradation)
     *
     * @param text The text to be processed in to a recipe Object
     * @return A Recipe object that was constructed from the text
     */
    public Recipe processText(String text) {
        //TODO implement this function so that at runtime it is decided which tasks should be performed
        if (sThreadPoolExecutor == null) {
            setUpThreadPool();
        }
        RecipeInProgress recipeInProgress = new RecipeInProgress(text);
        List<AbstractProcessingTask> pipeline = setUpPipeline(recipeInProgress);
        if (pipeline != null) {
            for (AbstractProcessingTask task : pipeline) {
                task.doTask();
            }
        }


        return recipeInProgress.convertToRecipe();
    }

    /**
     * The function creates all the tasks that could be used for the processing. If new tasks are added to the
     * codebase they should be created here as well.
     */
    private List<AbstractProcessingTask> setUpPipeline(RecipeInProgress recipeInProgress) {
        List<AbstractProcessingTask> pipeline = new ArrayList<>();
        pipeline.add(new SplitToMainSectionsTask(recipeInProgress, createBasicAnnotators()));
        pipeline.add(new DetectNumberOfPeopleTask(recipeInProgress));
        pipeline.add(new SplitStepsTask(recipeInProgress));
        pipeline.add(new DetectIngredientsInListTask(recipeInProgress, mIngredientClassifier));
        StepTaskNames[] taskNames = {StepTaskNames.INGR, StepTaskNames.TIMER};
        if (mParallelize) {
            pipeline.add(new ParallelizeStepsTask(recipeInProgress, sThreadPoolExecutor, taskNames));
        } else {
            pipeline.add(new NonParallelizeStepTask(recipeInProgress, taskNames));
        }
        return pipeline;
    }
}

