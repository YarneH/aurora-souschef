package com.aurora.souschef.souschefprocessor.facade;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.aurora.souschef.recipe.Recipe;
import com.aurora.souschef.souschefprocessor.task.RecipeInProgress;
import com.aurora.souschef.souschefprocessor.task.helpertasks.ParallelizeStepsTask;
import com.aurora.souschef.souschefprocessor.task.helpertasks.ParallellizeableTaskNames;
import com.aurora.souschef.souschefprocessor.task.ingredientdetector.DetectIngredientsInListTask;
import com.aurora.souschef.souschefprocessor.task.ProcessingTask;
import com.aurora.souschef.souschefprocessor.task.sectiondivider.DetectNumberOfPeopleTask;
import com.aurora.souschef.souschefprocessor.task.sectiondivider.SplitStepsTask;
import com.aurora.souschef.souschefprocessor.task.sectiondivider.SplitToMainSectionsTask;

/**
 * Implements the processing by applying the filters. This implements the order of the pipeline as
 * described in the architecture.
 */
public class Delegator {

    private ThreadPoolExecutor mThreadPoolExecutor; //TODO Maybe all threadpool stuff can be moved to ParallelizeSteps

    /**
     * Creates the ThreadPoolExecutor for the processing of the text, this is device-dependent
     */
    private void setUpThreadPool() {
        /*
         * Gets the number of available cores
         * (not always the same as the maximum number of cores)
         */
        int NUMBER_OF_CORES =
                Runtime.getRuntime().availableProcessors();
        // A queue of Runnables
        final BlockingQueue<Runnable> decodeWorkQueue;
        // Instantiates the queue of Runnables as a LinkedBlockingQueue
        decodeWorkQueue = new LinkedBlockingQueue<Runnable>();
        // Sets the amount of time an idle thread waits before terminating
        final int KEEP_ALIVE_TIME = 1;
        // Sets the Time Unit to seconds
        final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
        // Creates a thread pool manager
        mThreadPoolExecutor = new ThreadPoolExecutor(
                NUMBER_OF_CORES,       // Initial pool size
                NUMBER_OF_CORES,       // Max pool size
                KEEP_ALIVE_TIME,
                KEEP_ALIVE_TIME_UNIT,
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
        if (mThreadPoolExecutor == null) {
            setUpThreadPool();
        }
        RecipeInProgress recipeInProgress = new RecipeInProgress(text);
        ArrayList<ProcessingTask> pipeline = setUpPipeline(recipeInProgress);
        if (pipeline != null) {
            for (ProcessingTask task : pipeline) {
                task.doTask();
            }
        }


        return recipeInProgress.convertToRecipe();
    }



    /**
     * The function creates all the tasks that could be used for the processing. If new tasks are added to the
     * codebase they should be created here as well.
     */
    public ArrayList<ProcessingTask> setUpPipeline(RecipeInProgress recipeInProgress) {
        ArrayList<ProcessingTask> pipeline = new ArrayList<>();
        pipeline.add(new DetectNumberOfPeopleTask(recipeInProgress));
        pipeline.add(new SplitToMainSectionsTask(recipeInProgress));
        pipeline.add(new SplitStepsTask(recipeInProgress));
        pipeline.add(new DetectIngredientsInListTask(recipeInProgress));
        ParallellizeableTaskNames[] taskNames = {ParallellizeableTaskNames.INGR, ParallellizeableTaskNames.TIMER};
        pipeline.add(new ParallelizeStepsTask(recipeInProgress, this.mThreadPoolExecutor, taskNames));
        return pipeline;
    }


    public ThreadPoolExecutor getThreadPoolExecutor() {
        if (mThreadPoolExecutor == null) {
            setUpThreadPool();
        }
        return mThreadPoolExecutor;
    }


}

