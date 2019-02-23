package SouschefProcessor.Task.TimerDetector;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;

import SouschefProcessor.Recipe.RecipeInProgress;
import SouschefProcessor.Recipe.Step;
import SouschefProcessor.Recipe.Timer;
import SouschefProcessor.Task.ProcessingTask;

/**
 * A task that detects timers in steps
 */
public class DetectTimersInStepsTask implements ProcessingTask {

    public DetectTimersInStepsTask() {

    }

    /**
     * Detects the Timer in all the steps
     *
     * @param recipeInProgress The recipe containing the steps
     * @@param threadPool The threadpool on which to execute threads within this task
     */
    public void doTask(RecipeInProgress recipeInProgress, ThreadPoolExecutor threadPoolExecutor) {
        //TODO fallback if no steps present
        ArrayList<Step> steps = recipeInProgress.getSteps();
        ArrayList<DetectTimersInStepThread> threads = new ArrayList<>();

        CountDownLatch latch = new CountDownLatch(steps.size());

        for (Step s : steps) {
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
     * Detects the timer in a step
     *
     * @param step The step in which to detect a timer
     * @return A timer detected in the step
     */
    public Timer detectTimer(Step step) {

        //dummy
        if (step.getDescription().contains("9 minutes")) {
            return new Timer(9 * 60);
        } else {
            return new Timer(3 * 60, 3 * 60);
        }
    }

    /**
     * A thread that does the detecting of timer of a step
     */
    private class DetectTimersInStepThread extends Thread {

        private Step step;
        private CountDownLatch latch;

        public DetectTimersInStepThread(Step step, CountDownLatch latch) {
            this.step = step;
            this.latch = latch;
        }

        /**
         * Detects the timer and sets the timer field in the step
         */
        public void run() {
            Timer timer = detectTimer(step);
            step.setTimer(timer);
            latch.countDown();
        }
    }
}
