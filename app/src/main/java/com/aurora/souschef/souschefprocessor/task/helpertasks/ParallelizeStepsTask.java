package com.aurora.souschef.souschefprocessor.task.helpertasks;

import android.util.Log;

import com.aurora.souschef.recipe.RecipeStep;
import com.aurora.souschef.souschefprocessor.task.AbstractProcessingTask;
import com.aurora.souschef.souschefprocessor.task.RecipeInProgress;
import com.aurora.souschef.souschefprocessor.task.ingredientdetector.DetectIngredientsInStepTask;
import com.aurora.souschef.souschefprocessor.task.timerdetector.DetectTimersInStepTask;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;

import static android.content.ContentValues.TAG;

/**
 * A task that detects timers in mRecipeSteps
 */
public class ParallelizeStepsTask extends AbstractProcessingTask {
    private ThreadPoolExecutor mThreadPoolExecutor;
    // Maybe update this to classes, so that taskClasses are given and can be detected through reflection
    private ParallellizeableTaskNames[] mParallellizeableTaskNames;


    public ParallelizeStepsTask(RecipeInProgress recipeInProgress,
                                ThreadPoolExecutor threadPoolExecutor,
                                ParallellizeableTaskNames[] parallellizeableTaskNames) {
        super(recipeInProgress);
        this.mThreadPoolExecutor = threadPoolExecutor;
        // should this be deep copied?
        this.mParallellizeableTaskNames = parallellizeableTaskNames;
    }

    /**
     * Launches parallel threads for each type of task submitted and for each step
     */
    public void doTask() {
        //TODO fallback if no mRecipeSteps present
        List<RecipeStep> recipeSteps = mRecipeInProgress.getRecipeSteps();
        //for every step and for every parallelizeable task
        CountDownLatch latch = new CountDownLatch(recipeSteps.size() * mParallellizeableTaskNames.length);
        // TODO: it is possible to immediately pass the recipeStep to the Detect...InStepTasks.
        // In order to do this, these Detect...InStepTasks should not inherit AbstractProcessingTask, but
        // inherit from something like StepProcessingTask (which has a RecipeStep instead of a RecipeInProgress)
        // that cannot be added directly in the pipeline,
        // but only through ParallelizeStepTask (or a wrapper task)
        for (int i = 0; i < recipeSteps.size(); i++) {
            for (ParallellizeableTaskNames taskName : mParallellizeableTaskNames) {
                StepTaskThread thread = createStepTaskThread(latch, i, taskName);
                mThreadPoolExecutor.execute(thread);
            }
        }
        waitForThreads(latch);
    }

    private StepTaskThread createStepTaskThread(CountDownLatch latch, int stepIndex,
                                                ParallellizeableTaskNames taskName) {
        StepTaskThread stepTaskThread = null;

        if (taskName.equals(ParallellizeableTaskNames.INGR)) {
            //Ingredient
            stepTaskThread = new StepTaskThread(new DetectIngredientsInStepTask(
                    this.mRecipeInProgress, stepIndex), latch);
        } else  {
            //Timer
            stepTaskThread = new StepTaskThread(new DetectTimersInStepTask(
                    this.mRecipeInProgress, stepIndex), latch);
        }

        // TODO Is it necessary to add the thread to threads array? Did not seem to happen in original code
        return stepTaskThread;
    }

    private void waitForThreads(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            Log.e(TAG, "waitForThreads: ", e);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * A thread that does the detecting of timer of a recipeStep
     */
    private class StepTaskThread implements Runnable {

        private AbstractProcessingTask task;
        private CountDownLatch latch;

        public StepTaskThread(AbstractProcessingTask task, CountDownLatch latch) {
            this.task = task;
            this.latch = latch;
        }

        /**
         * executes the task in the thread
         */
        @Override
        public void run() {
            task.doTask();
            latch.countDown();
        }
    }


}
