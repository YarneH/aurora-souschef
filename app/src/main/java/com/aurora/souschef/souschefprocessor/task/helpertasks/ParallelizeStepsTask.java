package com.aurora.souschef.souschefprocessor.task.helpertasks;

import com.aurora.souschef.souschefprocessor.task.RecipeInProgress;
import com.aurora.souschef.recipe.RecipeStep;
import com.aurora.souschef.souschefprocessor.task.ingredientdetector.DetectIngredientsInStepTask;
import com.aurora.souschef.souschefprocessor.task.ProcessingTask;
import com.aurora.souschef.souschefprocessor.task.timerdetector.DetectTimersInStepTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * A task that detects timers in mRecipeSteps
 */
public class ParallelizeStepsTask extends ProcessingTask {
    ThreadPoolExecutor mThreadPoolExecutor;
    ParallellizeableTaskNames [] mParallellizeableTaskNames; // Maybe update this to classes, so that taskClasses are given and can be detected through reflection


    public ParallelizeStepsTask(RecipeInProgress recipeInProgress, ThreadPoolExecutor threadPoolExecutor, ParallellizeableTaskNames[] parallellizeableTaskNames) {
        super(recipeInProgress);
        this.mThreadPoolExecutor = threadPoolExecutor;
        this.mParallellizeableTaskNames = parallellizeableTaskNames; // should this be deep copied?
    }

    /**
     * Launches parallel threads for each type of task submitted and for each step
     */
    public void doTask() {//TODO fallback if no mRecipeSteps present
        List<RecipeStep> recipeSteps = mRecipeInProgress.getRecipeSteps();
        ArrayList<StepTaskThread> threads = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(recipeSteps.size() * mParallellizeableTaskNames.length); //for every step and for every parallelizeable task

        // TODO: it is possible to immediately pass the recipeStep to the Detect...InStepTasks.
        // In order to do this, these Detect...InStepTasks should not inherit ProcessingTask, but
        // inherit from something like StepProcessingTask (which has a RecipeStep instead of a RecipeInProgress)
        // that cannot be added directly in the pipeline,
        // but only through ParallelizeStepTask (or a wrapper task)
        for (int i = 0; i < recipeSteps.size(); i++){
            for (ParallellizeableTaskNames taskName : mParallellizeableTaskNames) {
                StepTaskThread thread = createStepTaskThread(latch, i, taskName);
                mThreadPoolExecutor.execute(thread);
            }
        }
        waitForThreads(latch);
    }

    private StepTaskThread createStepTaskThread(CountDownLatch latch, int stepIndex, ParallellizeableTaskNames taskName){
        StepTaskThread stepTaskThread = null;

        if (taskName.equals(ParallellizeableTaskNames.INGR)) {
            stepTaskThread = new StepTaskThread(new DetectIngredientsInStepTask(this.mRecipeInProgress, stepIndex), latch);
        }
        else if (taskName.equals(ParallellizeableTaskNames.TIMER)) {
            stepTaskThread = new StepTaskThread(new DetectTimersInStepTask(this.mRecipeInProgress, stepIndex), latch);
        }
        // TODO Is it necessary to add the thread to threads array? Did not seem to happen in original code
        return stepTaskThread;
    }

    private void waitForThreads(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * A thread that does the detecting of timer of a recipeStep
     */
    private class StepTaskThread extends Thread {

        private ProcessingTask task;
        private CountDownLatch latch;

        public StepTaskThread(ProcessingTask task, CountDownLatch latch) {
            this.task = task;
            this.latch = latch;
        }

        /**
         * executes the task in the thread
         */
        public void run() {
            task.doTask();
            latch.countDown();
        }
    }


}