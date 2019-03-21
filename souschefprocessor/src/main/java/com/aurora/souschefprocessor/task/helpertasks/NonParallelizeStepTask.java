package com.aurora.souschefprocessor.task.helpertasks;

import com.aurora.souschefprocessor.recipe.RecipeStep;
import com.aurora.souschefprocessor.task.AbstractProcessingTask;
import com.aurora.souschefprocessor.task.RecipeInProgress;
import com.aurora.souschefprocessor.task.ingredientdetector.DetectIngredientsInStepTask;
import com.aurora.souschefprocessor.task.timerdetector.DetectTimersInStepTask;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class NonParallelizeStepTask extends AbstractProcessingTask {

    private ParallellizeableTaskNames[] mParallellizeableTaskNames;

    public NonParallelizeStepTask(RecipeInProgress recipeInProgress,
                                  ParallellizeableTaskNames[] parallellizeableTaskNames) {
        super(recipeInProgress);


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
                doTask(i, taskName);
            }
        }

    }

    public void doTask(int stepIndex, ParallellizeableTaskNames taskname) {
        if (taskname.equals(ParallellizeableTaskNames.TIMER)) {
            new DetectTimersInStepTask(
                    this.mRecipeInProgress, stepIndex).doTask();
        } else {
            new DetectIngredientsInStepTask(
                    this.mRecipeInProgress, stepIndex).doTask();
        }
    }
}
