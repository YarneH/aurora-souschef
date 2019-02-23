package SouschefProcessor.Task.TimerDetector;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;

import SouschefProcessor.Recipe.RecipeInProgress;
import SouschefProcessor.Recipe.RecipeStep;
import SouschefProcessor.Recipe.RecipeTimer;
import SouschefProcessor.Task.ProcessingTask;

/**
 * A task that detects timers in recipeSteps
 */
public class DetectTimersInStepsTask implements ProcessingTask {

    public DetectTimersInStepsTask() {

    }

    /**
     * Detects the RecipeTimer in all the recipeSteps
     *
     * @param recipeInProgress The recipe containing the recipeSteps
     * @@param threadPool The threadpool on which to execute threads within this task
     */
    public void doTask(RecipeInProgress recipeInProgress, ThreadPoolExecutor threadPoolExecutor) {
        //TODO fallback if no recipeSteps present
        ArrayList<RecipeStep> recipeSteps = recipeInProgress.getRecipeSteps();
        ArrayList<DetectTimersInStepThread> threads = new ArrayList<>();

        CountDownLatch latch = new CountDownLatch(recipeSteps.size());

        for (RecipeStep s : recipeSteps) {
            DetectTimersInStepThread thread = new DetectTimersInStepThread(s, latch);
            threadPoolExecutor.execute(thread);
        }
        waitForThreads(latch);

    }

    private void waitForThreads(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Detects the timer in a recipeStep
     *
     * @param recipeStep The recipeStep in which to detect a timer
     * @return A timer detected in the recipeStep
     */
    public ArrayList<RecipeTimer> detectTimer(RecipeStep recipeStep) {

        //dummy
        ArrayList<RecipeTimer> list = new ArrayList<>();
        try {

            if (recipeStep.getDescription().contains("9 minutes")) {
                list.add(new RecipeTimer(9 * 60));
            } else {
                list.add(new RecipeTimer(3 * 60, 3 * 60));
            }

        } catch (RecipeTimer.TimerValueInvalidException tvie) {
            //TODO do something meaningful
        }
        return list;
    }

    /**
     * A thread that does the detecting of timer of a recipeStep
     */
    private class DetectTimersInStepThread extends Thread {

        private RecipeStep recipeStep;
        private CountDownLatch latch;

        public DetectTimersInStepThread(RecipeStep recipeStep, CountDownLatch latch) {
            this.recipeStep = recipeStep;
            this.latch = latch;
        }

        /**
         * Detects the timer and sets the timer field in the recipeStep
         */
        public void run() {
            ArrayList<RecipeTimer> recipeTimers = detectTimer(recipeStep);
            recipeStep.setRecipeTimers(recipeTimers);
            latch.countDown();
        }
    }
}
