package SouschefProcessor.Task.HelperTasks;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;

import SouschefProcessor.Recipe.RecipeInProgress;
import SouschefProcessor.Recipe.RecipeStep;
import SouschefProcessor.Task.IngredientDetector.DetectIngredientsInStepTask;
import SouschefProcessor.Task.ProcessingTask;
import SouschefProcessor.Task.TimerDetector.DetectTimersInStepTask;

/**
 * A task that detects timers in recipeSteps
 */
public class ParallelizeStepsTask extends ProcessingTask {
    ThreadPoolExecutor threadPoolExecutor;
    String[] taskNames; // Maybe update this to classes, so that taskClasses are given and can be detected through reflection

    public ParallelizeStepsTask(RecipeInProgress recipeInProgress, ThreadPoolExecutor threadPoolExecutor, String[] taskNames) {
        super(recipeInProgress);
        this.threadPoolExecutor = threadPoolExecutor;
        this.taskNames = taskNames; // should this be deep copied?
    }

    /**
     * Launches parallel threads for each type of task submitted and for each step
     */
    public void doTask() {//TODO fallback if no recipeSteps present
        ArrayList<RecipeStep> recipeSteps = recipeInProgress.getRecipeSteps();
        ArrayList<StepTaskThread> threads = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(recipeSteps.size());

        // TODO: it is possible to immediately pass the recipeStep to the Detect...InStepTasks.
        // In order to do this, these Detect...InStepTasks should not inherit ProcessingTask, but
        // inherit from something like StepProcessingTask (which has a RecipeStep instead of a RecipeInProgress)
        // that cannot be added directly in the pipeline,
        // but only through ParallelizeStepTask (or a wrapper task)
        for (int i = 0; i < recipeSteps.size(); i++){
            for (String taskName : taskNames) {
                StepTaskThread thread = createStepTaskThread(latch, i, taskName);
                threadPoolExecutor.execute(thread);
            }
        }
        waitForThreads(latch);
    }

    private StepTaskThread createStepTaskThread(CountDownLatch latch, int stepIndex, String taskName){
        StepTaskThread thread = null;
        // TODO enum or reflection
        if (taskName.equals("INGR")) {
            thread = new StepTaskThread(new DetectIngredientsInStepTask(this.recipeInProgress, stepIndex), latch);
        }
        else if (taskName.equals("TIMER")) {
            thread = new StepTaskThread(new DetectTimersInStepTask(this.recipeInProgress, stepIndex), latch);
        }
        // TODO Is it necessary to add the thread to threads array? Did not seem to happen in original code
        return thread;
    }

    private void waitForThreads(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * A thread that does the detecting of timer of a recipeStep
     */
    private class StepTaskThread extends Thread {

        private ProcessingTask task;
        private CountDownLatch latch;

        public StepTaskThread(ProcessingTask task, CountDownLatch latch) {
            this.task = task;
            this.latch = latch;
        }

        /**
         * executes the task in the thread
         */
        public void run() {
            task.doTask();
            latch.countDown();
        }
    }


}