package com.aurora.souschefprocessor.task;

/**
 * An interface that has to be implemented by all tasks that do work on a recipe
 */
public abstract class AbstractProcessingTask {


    protected RecipeInProgress mRecipeInProgress;


    /**
     * A task to be done on a mRecipeInProgress
     *
     * @param recipeInProgress The mRecipeInProgress on which to do the task
     */
    public AbstractProcessingTask(RecipeInProgress recipeInProgress) {
        if(recipeInProgress == null){
            throw new IllegalArgumentException("The recipeInProgress cannot be null");
        }
        this.mRecipeInProgress = recipeInProgress;
    }

    /**
     * Do the task on the {@link #mRecipeInProgress} attribute
     */
    public abstract void doTask();



}
