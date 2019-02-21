package SouschefProcessor.Task;

import java.util.concurrent.ThreadPoolExecutor;

import SouschefProcessor.Recipe.RecipeInProgress;

/**
 * An interface that has to be implemented by all tasks that do work on a recipe
 */
public interface Task {
    /**
     * A task to be done on a recipe
     * @param recipe The recipe on which to do the task
     * @param threadPool A threadpool to use if threads are created in this task
     */
     void doTask(RecipeInProgress recipe, ThreadPoolExecutor threadPool);

}
