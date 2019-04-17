package com.aurora.souschefprocessor.task.helpertasks;

import com.aurora.souschefprocessor.facade.RecipeDetectionException;
import com.aurora.souschefprocessor.recipe.RecipeStep;
import com.aurora.souschefprocessor.task.AbstractProcessingTask;
import com.aurora.souschefprocessor.task.RecipeInProgress;
import com.aurora.souschefprocessor.task.ingredientdetector.DetectIngredientsInStepTask;
import com.aurora.souschefprocessor.task.timerdetector.DetectTimersInStepTask;

import java.util.List;

/**
 * A wrapper class for doing the steptasks but not in parallel as opposed to {@link ParallelizeStepsTask}
 */
public class NonParallelizeStepTask extends AbstractProcessingTask {
    /**
     * The names of the tasks that will be done by this task on steps {@link StepTaskNames}
     */
    private StepTaskNames[] mStepTaskNames;

    /**
     * Constructs the NonParrallelizeStepTaks
     *
     * @param recipeInProgress The recipe on which to do the task
     * @param stepTaskNames    The names of the tasks to do on the steps
     */
    public NonParallelizeStepTask(RecipeInProgress recipeInProgress,
                                  StepTaskNames[] stepTaskNames) {
        super(recipeInProgress);
        this.mStepTaskNames = stepTaskNames;
    }

    /**
     * Does the tasks sequentially for each step
     */
    public void doTask() {
        List<RecipeStep> recipeSteps = mRecipeInProgress.getRecipeSteps();

        if (recipeSteps.isEmpty()) {
            throw new RecipeDetectionException("No steps were detected in this recipe. This is probably not" +
                    "a recipe!");
        }

        for (int i = 0; i < recipeSteps.size(); i++) {
            for (StepTaskNames taskName : mStepTaskNames) {
                doTask(i, taskName);
            }
        }
    }

    /**
     * Does the task on the step with index of the recipe. This is used to do all tasks on all steps
     * in the {@link #doTask()} method
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
