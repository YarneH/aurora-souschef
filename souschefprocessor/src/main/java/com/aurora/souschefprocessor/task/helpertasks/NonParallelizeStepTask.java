package com.aurora.souschefprocessor.task.helpertasks;

import com.aurora.souschefprocessor.recipe.RecipeStep;
import com.aurora.souschefprocessor.task.AbstractProcessingTask;
import com.aurora.souschefprocessor.task.RecipeInProgress;
import com.aurora.souschefprocessor.task.ingredientdetector.DetectIngredientsInStepTask;
import com.aurora.souschefprocessor.task.timerdetector.DetectTimersInStepTask;

import java.util.List;

/**
 * A wrapper class for doing the steptasks but not in parallel
 */
public class NonParallelizeStepTask extends AbstractProcessingTask {

    private StepTaskNames[] mStepTaskNames;

    public NonParallelizeStepTask(RecipeInProgress recipeInProgress,
                                  StepTaskNames[] stepTaskNames) {
        super(recipeInProgress);
        this.mStepTaskNames = stepTaskNames;
    }

    /**
     * Does the tasks sequentially for each step
     */
    public void doTask() {
        //TODO fallback if no mRecipeSteps present
        List<RecipeStep> recipeSteps = mRecipeInProgress.getRecipeSteps();

        for (int i = 0; i < recipeSteps.size(); i++) {
            for (StepTaskNames taskName : mStepTaskNames) {
                doTask(i, taskName);
            }
        }

    }

    /**
     * Does the task on the step with index of the recipe
     *
     * @param stepIndex The index of the step on which to do the processing
     * @param taskname  The name of the task to do on the step
     */
    private void doTask(int stepIndex, StepTaskNames taskname) {
        if (taskname.equals(StepTaskNames.TIMER)) {
            new DetectTimersInStepTask(
                    this.mRecipeInProgress, stepIndex).doTask();
        } else {
            new DetectIngredientsInStepTask(
                    this.mRecipeInProgress, stepIndex).doTask();
        }
    }
}
