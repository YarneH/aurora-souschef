package com.aurora.souschefprocessor.task;

import com.aurora.souschefprocessor.facade.RecipeDetectionException;

/**
 * An abstract class that has to be implemented by all tasks that do work on a {@link RecipeInProgress}
 * object.
 */
public abstract class AbstractProcessingTask {
    /**
     * The {@link RecipeInProgress} to do the task on
     */
    protected RecipeInProgress mRecipeInProgress;

    public AbstractProcessingTask(RecipeInProgress recipeInProgress) {
        if (recipeInProgress == null) {
            throw new IllegalArgumentException("The recipeInProgress cannot be null");
        }
        this.mRecipeInProgress = recipeInProgress;
    }

    /**
     * Do the task on the {@link #mRecipeInProgress} attribute
     *
     * @throws RecipeDetectionException an indication that this step could not be executed. The input received from
     *                                  Aurora does probably not represent a recipe.
     */
    public abstract void doTask() throws RecipeDetectionException;
}
