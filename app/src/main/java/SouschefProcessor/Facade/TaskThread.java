package SouschefProcessor.Facade;

import java.util.concurrent.CountDownLatch;
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
    private CountDownLatch latch;

    public TaskThread(RecipeInProgress recipe, Task task, ThreadPoolExecutor threadPool, CountDownLatch latch) {
        this.recipe = recipe;
        this.task = task;
        this.threadPool = threadPool;
        this.latch = latch;
    }

    @Override
    public void run() {
        task.doTask(recipe, threadPool);
        latch.countDown();

    }
}
