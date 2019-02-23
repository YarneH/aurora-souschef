package SouschefProcessor.Facade;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;

import SouschefProcessor.Recipe.RecipeInProgress;
import SouschefProcessor.Task.ProcessingTask;

/**
 * A class in which a processingTask does its work
 */
public class ProcssingTaskThread extends Thread {

    private RecipeInProgress recipeInProgress;
    private ProcessingTask processingTask;
    private ThreadPoolExecutor threadPoolExecutor;
    private CountDownLatch latch;

    public ProcssingTaskThread(RecipeInProgress recipeInProgress, ProcessingTask processingTask, ThreadPoolExecutor threadPoolExecutor, CountDownLatch latch) {
        this.recipeInProgress = recipeInProgress;
        this.processingTask = processingTask;
        this.threadPoolExecutor = threadPoolExecutor;
        this.latch = latch;
    }

    @Override
    public void run() {
        processingTask.doTask(recipeInProgress, threadPoolExecutor);
        latch.countDown();

    }
}
