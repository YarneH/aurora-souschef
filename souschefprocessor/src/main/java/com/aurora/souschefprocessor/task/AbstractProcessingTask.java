package com.aurora.souschefprocessor.task;

/**
 * An interface that has to be implemented by all tasks that do work on a recipe
 */
public abstract class AbstractProcessingTask {
    /**
     * A task to be done on a mRecipeInProgress
     *
     * @param mRecipeInProgress The mRecipeInProgress on which to do the task
     */

    protected RecipeInProgress mRecipeInProgress;


    public AbstractProcessingTask(RecipeInProgress recipeInProgress) {
        if(recipeInProgress == null){
            throw new IllegalArgumentException("The recipeInProgress cannot be null");
        }
        this.mRecipeInProgress = recipeInProgress;
    }

    public abstract void doTask();



}
