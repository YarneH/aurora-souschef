package SouschefProcessor.Facade;

import java.util.concurrent.ThreadPoolExecutor;

import SouschefProcessor.Recipe.RecipeInProgress;
import SouschefProcessor.Task.Task;

/**
 * A class in which a task does its work
 */
public class TaskThread extends Thread {

    private RecipeInProgress recipe;
    private Task task;
    private ThreadPoolExecutor threadPool;

    public TaskThread(RecipeInProgress recipe, Task task, ThreadPoolExecutor threadPool){
        this.recipe = recipe;
        this.task = task;
        this.threadPool = threadPool;
    }
    @Override
    public void run(){
        task.doTask(recipe, threadPool);

    }
}
