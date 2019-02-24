package com.aurora.souschef.SouchefProcessor.Task;

import com.aurora.souschef.SouchefProcessor.Recipe.RecipeInProgress;

/**
 * An interface that has to be implemented by all tasks that do work on a recipe
 */
public abstract class ProcessingTask {
    /**
     * A task to be done on a mRecipeInProgress
     * @param mRecipeInProgress The mRecipeInProgress on which to do the task
     */

    protected RecipeInProgress mRecipeInProgress;

    public ProcessingTask(RecipeInProgress recipeInProgress){
        this.mRecipeInProgress = recipeInProgress;
    }

    public abstract void doTask();

}
