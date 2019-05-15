package com.aurora.souschefprocessor.task.helpertasks;

import android.util.Log;

import com.aurora.souschefprocessor.facade.RecipeDetectionException;
import com.aurora.souschefprocessor.recipe.RecipeStep;
import com.aurora.souschefprocessor.task.AbstractProcessingTask;
import com.aurora.souschefprocessor.task.RecipeInProgress;
import com.aurora.souschefprocessor.task.RecipeStepInProgress;
import com.aurora.souschefprocessor.task.ingredientdetector.DetectIngredientsInStepTask;
import com.aurora.souschefprocessor.task.timerdetector.DetectTimersInStepTask;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;

import static android.content.ContentValues.TAG;

/**
 * A wrapper class for doing the steptasks in parallel as opposed to {@link NonParallelizeStepTask}
 */
public class ParallelizeStepsTask extends AbstractProcessingTask {
    /**
     * A {@link ThreadPoolExecutor} for executing the tasks in different threads
     */
    private ThreadPoolExecutor mThreadPoolExecutor;

    /**
     * The names of the tasks that will be done by this task on steps {@link StepTaskNames}
     */
    private StepTaskNames[] mStepTaskNames;

    /**
     * Constructs the ParallelizeStepsTask
     *
     * @param recipeInProgress   The recipe on which to do the task
     * @param stepTaskNames      The names of the tasks to do on the steps
     * @param threadPoolExecutor The threadpoolexecutor for executing the tasks in different threads
     */
    public ParallelizeStepsTask(RecipeInProgress recipeInProgress,
                                ThreadPoolExecutor threadPoolExecutor,
                                StepTaskNames[] stepTaskNames) {
        super(recipeInProgress);
        this.mThreadPoolExecutor = threadPoolExecutor;
        this.mStepTaskNames = stepTaskNames;
    }

    /**
     * Launches parallel threads for each type of task in {@link #mStepTaskNames} submitted and for
     * each {@link RecipeStep} in {@link #mRecipeInProgress}. If no steps are detected this throws a
     * RecipeDetectionException
     *
     * @throws RecipeDetectionException Is thrown when no steps are detected, most probably this is not a recipe.
     */
    public void doTask() throws RecipeDetectionException {

        List<RecipeStepInProgress> recipeSteps = mRecipeInProgress.getStepsInProgress();
        if (recipeSteps.isEmpty()) {
            throw new RecipeDetectionException("No steps were detected in this recipe. This is probably not" +
                    "a recipe!");
        }

        // Make a latch that counts for every step and for every parallelizeable task
        CountDownLatch latch = new CountDownLatch(recipeSteps.size() * mStepTaskNames.length);

        for (int i = 0; i < recipeSteps.size(); i++) {
            for (StepTaskNames taskName : mStepTaskNames) {
                // Create a thread to execute this task on this step
                StepTaskThread thread = createStepTaskThread(latch, i, taskName);
                mThreadPoolExecutor.execute(thread);
            }
        }
        // wait unitl all threads have finished
        waitForThreads(latch);

        if(mRecipeDetectionException != null){
            // something went wrong in at least one of the threads
            throw mRecipeDetectionException;
        }
    }

    private RecipeDetectionException mRecipeDetectionException = null;

    /**
     * Creates a StepTaskThread for a step and task
     *
     * @param latch     The latch to count down after finishing the thread
     * @param stepIndex the index of the step of this thread
     * @param taskName  the name of the task of this thread
     * @return A thread that executes the task on the step with index stepindex of the recipe
     */
    private StepTaskThread createStepTaskThread(CountDownLatch latch, int stepIndex,
                                                StepTaskNames taskName) {
        StepTaskThread stepTaskThread;

        if (taskName.equals(StepTaskNames.INGREDIENT)) {
            // Ingredient
            stepTaskThread = new StepTaskThread(new DetectIngredientsInStepTask(
                    this.mRecipeInProgress, stepIndex), latch);

        } else {
            // Timer
            stepTaskThread = new StepTaskThread(new DetectTimersInStepTask(
                    this.mRecipeInProgress, stepIndex), latch);
        }

        return stepTaskThread;
    }

    /**
     * Waits untill all the threads of this latch are finished
     *
     * @param latch The latch to wait on
     */
    private void waitForThreads(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            Log.e(TAG, "waitForThreads: ", e);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * A thread that executes a task on a step
     */
    private class StepTaskThread implements Runnable {

        /**
         * The task to be executed
         */
        private AbstractProcessingTask task;

        /**
         * The latch to count down after finishing the task
         */
        private CountDownLatch latch;

        StepTaskThread(AbstractProcessingTask task, CountDownLatch latch) {
            this.task = task;
            this.latch = latch;

        }

        /**
         * executes the task in the thread and counts down the latch
         */
        @Override
        public void run() {
            try {
                task.doTask();
            } catch (RecipeDetectionException rde) {
                // set the exception to this one to let the caller know something went wrong
                mRecipeDetectionException = rde;
            }
            latch.countDown();
        }
    }
}
