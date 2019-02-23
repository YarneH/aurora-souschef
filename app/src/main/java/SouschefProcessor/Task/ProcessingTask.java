package SouschefProcessor.Task;

import java.util.concurrent.ThreadPoolExecutor;

import SouschefProcessor.Recipe.RecipeInProgress;

/**
 * An interface that has to be implemented by all tasks that do work on a recipe
 */
public abstract class ProcessingTask {
    /**
     * A task to be done on a recipeInProgress
     * @param recipeInProgress The recipeInProgress on which to do the task
     * @param threadPoolExecutor A threadpool to use if threads are created in this task
     */

    protected RecipeInProgress recipeInProgress;

    public ProcessingTask(RecipeInProgress recipeInProgress){
        this.recipeInProgress = recipeInProgress;
    }

    public abstract void doTask();

}
