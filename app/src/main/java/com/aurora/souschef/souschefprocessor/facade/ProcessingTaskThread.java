package com.aurora.souschef.souschefprocessor.facade;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;

import com.aurora.souschef.souschefprocessor.recipe.RecipeInProgress;
import com.aurora.souschef.souschefprocessor.task.ProcessingTask;

/**
 * A class in which a processingTask does its work
 */
public class ProcessingTaskThread extends Thread {

    private RecipeInProgress recipeInProgress;
    private ProcessingTask processingTask;
    private ThreadPoolExecutor threadPoolExecutor;
    private CountDownLatch latch;

    public ProcessingTaskThread(RecipeInProgress recipeInProgress, ProcessingTask processingTask, ThreadPoolExecutor threadPoolExecutor, CountDownLatch latch) {
        this.recipeInProgress = recipeInProgress;
        this.processingTask = processingTask;
        this.threadPoolExecutor = threadPoolExecutor;
        this.latch = latch;
    }

    @Override
    public void run() {
        processingTask.doTask();
        latch.countDown();

    }
}
