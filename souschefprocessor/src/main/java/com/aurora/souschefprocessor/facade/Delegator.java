package com.aurora.souschefprocessor.facade;

import android.util.Log;

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
import edu.stanford.nlp.pipeline.AnnotationPipeline;
import edu.stanford.nlp.pipeline.Annotator;
import edu.stanford.nlp.pipeline.TokenizerAnnotator;
import edu.stanford.nlp.pipeline.WordsToSentencesAnnotator;

/**
 * Implements the processing by applying the filters. This implements the order of the pipeline as
 * described in the architecture.
 */
public class Delegator {

    private static final double HALF = 0.5;
    //TODO Maybe all threadpool stuff can be moved to ParallelizeSteps
    private static ThreadPoolExecutor sThreadPoolExecutor;
    private CRFClassifier<CoreLabel> mIngredientClassifier;
    private boolean mParallelize;

    private static boolean startedCreatingPipelines = false;
    private static final Object LOCK = new Object();

    Delegator(CRFClassifier<CoreLabel> ingredientClassifier, boolean parallelize) {
        mIngredientClassifier = ingredientClassifier;
        mParallelize = parallelize;

    }

    static{
        createAnnotationPipelines();
    }

    static void createAnnotationPipelines() {
        synchronized (LOCK){

            if(startedCreatingPipelines){
                // creating already started or finished -> do not start again
                return;
            }
            // ensure no other thread starts creating pipelines
            startedCreatingPipelines = true;
            LOCK.notifyAll();
        }
        if(sThreadPoolExecutor == null){
            setUpThreadPool();
        }
        List<Annotator> annotators = new ArrayList<>();
        annotators.add(new TokenizerAnnotator(false));
        Delegator.incrementProgressAnnotationPipelines();
        Log.d("COMMON:", "0");
        annotators.add(new WordsToSentencesAnnotator(false));
        Delegator.incrementProgressAnnotationPipelines();
        Log.d("COMMON", "2");

        DetectTimersInStepTask.initializeAnnotationPipeline(annotators);

        DetectIngredientsInStepTask.initializeAnnotationPipeline(annotators);


    }

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
    public List<AbstractProcessingTask> setUpPipeline(RecipeInProgress recipeInProgress) {
        ArrayList<AbstractProcessingTask> pipeline = new ArrayList<>();
        pipeline.add(new DetectNumberOfPeopleTask(recipeInProgress));
        pipeline.add(new SplitToMainSectionsTask(recipeInProgress));
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


    public static ThreadPoolExecutor getThreadPoolExecutor() {
        if ( sThreadPoolExecutor == null) {
            setUpThreadPool();
        }
        return sThreadPoolExecutor;
    }


}

