package SouschefProcessor.Facade;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import SouschefProcessor.Recipe.Recipe;
import SouschefProcessor.Recipe.RecipeInProgress;
import SouschefProcessor.Task.IngredientDetector.DetectIngredientsInListTask;
import SouschefProcessor.Task.IngredientDetector.DetectIngredientsInStepsTask;
import SouschefProcessor.Task.ProcessingTask;
import SouschefProcessor.Task.SectionDivider.DetectNumberOfPeopleTask;
import SouschefProcessor.Task.SectionDivider.SplitStepsTask;
import SouschefProcessor.Task.SectionDivider.SplitToMainSectionsTask;
import SouschefProcessor.Task.TimerDetector.DetectTimersInStepsTask;

/**
 * Implements the processing by applying the filters. This implements the order of the pipeline as
 * described in the architecture.
 */
public class Delegator {

    private ThreadPoolExecutor threadPoolExecutor;
    private DetectNumberOfPeopleTask detectNumberOfPeopleTask;
    private SplitToMainSectionsTask splitToMainSectionsTask;
    private SplitStepsTask splitStepsTask;
    private DetectIngredientsInStepsTask detectIngredientsInStepsTask;
    private DetectIngredientsInListTask detectIngredientsInListTask;
    private DetectTimersInStepsTask detectTimersInStepsTask;
    private boolean tasksHaveBeenSetUp = false;

    /**
     * Creates the ThreadPoolExecutor for the processing of the text, this is device-dependent
     */
    private void setUpThreadPool() {
        /*
         * Gets the number of available cores
         * (not always the same as the maximum number of cores)
         */
        int NUMBER_OF_CORES =
                Runtime.getRuntime().availableProcessors();
        // A queue of Runnables
        final BlockingQueue<Runnable> decodeWorkQueue;
        // Instantiates the queue of Runnables as a LinkedBlockingQueue
        decodeWorkQueue = new LinkedBlockingQueue<Runnable>();
        // Sets the amount of time an idle thread waits before terminating
        final int KEEP_ALIVE_TIME = 1;
        // Sets the Time Unit to seconds
        final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
        // Creates a thread pool manager
        threadPoolExecutor = new ThreadPoolExecutor(
                NUMBER_OF_CORES,       // Initial pool size
                NUMBER_OF_CORES,       // Max pool size
                KEEP_ALIVE_TIME,
                KEEP_ALIVE_TIME_UNIT,
                decodeWorkQueue);
    }

    /**
     * This is the core function of the delegator, where the text is processed by applying the filters
     * This function should be able to at run time decide to do certain filters or not (graceful degradation)
     *
     * @param text The text to be processed in to a recipe Object
     * @return A recipe object that was constructed from the text
     */
    public Recipe processText(String text) {
        //TODO implement this function so that at runtime it is decided which tasks should be performed
        if (threadPoolExecutor == null) {
            setUpThreadPool();
        }
        if (!tasksHaveBeenSetUp) {
            setUpPipeline();
        }

        RecipeInProgress recipeInProgress = new RecipeInProgress(text);

        //detect number of people
        doTask(recipeInProgress, detectNumberOfPeopleTask, threadPoolExecutor);
        //divide into sections
        doTask(recipeInProgress, splitToMainSectionsTask, threadPoolExecutor);

        //detect ingredients in list
        CountDownLatch detectIngredientsInListLatch = new CountDownLatch(1);
        ProcssingTaskThread detectIngredientsInListThread = doTaskInThread(recipeInProgress, detectIngredientsInListTask, threadPoolExecutor, detectIngredientsInListLatch);

        //split into recipeSteps
        CountDownLatch splitStepsLatch = new CountDownLatch(1);
        ProcssingTaskThread splitStepsThread = doTaskInThread(recipeInProgress, splitStepsTask, threadPoolExecutor, splitStepsLatch);

        waitForLatch(splitStepsLatch); //later recipeSteps depend on splitting in recipeSteps

        //detectTimers in recipeSteps
        CountDownLatch finishLatch = new CountDownLatch(2); //timerdetector and ingredient in step detector
        ProcssingTaskThread detectTimersInStepsThread = doTaskInThread(recipeInProgress, detectTimersInStepsTask, threadPoolExecutor, finishLatch);

        waitForLatch(detectIngredientsInListLatch); //later recipeSteps depend on ingredientlist

        //detect ingredients in recipeSteps

        waitForLatch(finishLatch);

        return recipeInProgress.convertToRecipe();
    }

    private void waitForLatch(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException ie) {
            //TODO, maybe let a higher stage handle this
        }
    }

    /**
     * The function creates all the tasks that could be used for the processing. If new tasks are added to the c
     * codebase they should be created here as well.
     */
    public void setUpPipeline() {
        detectNumberOfPeopleTask = new DetectNumberOfPeopleTask();
        splitToMainSectionsTask = new SplitToMainSectionsTask();
        splitStepsTask = new SplitStepsTask();
        detectIngredientsInListTask = new DetectIngredientsInListTask();
        detectIngredientsInStepsTask = new DetectIngredientsInStepsTask();
        detectTimersInStepsTask = new DetectTimersInStepsTask();
        tasksHaveBeenSetUp = true;
    }

    /**
     * This performs a processingTask on the recipe.
     *
     * @param rip            The recipe on which to do the processingTask
     * @param processingTask The processingTask to be performed
     */
    public void doTask(RecipeInProgress rip, ProcessingTask processingTask, ThreadPoolExecutor threadPool) {
        processingTask.doTask(rip, threadPool);
    }

    /**
     * This performs a processingTask on a recipe in a seperate thread.
     *
     * @param rip            The recipe on which to do the processingTask
     * @param processingTask The processingTask to be performed
     * @return The thread in which the processingTask is being performed.
     */
    public ProcssingTaskThread doTaskInThread(RecipeInProgress rip, ProcessingTask processingTask, ThreadPoolExecutor threadPool, CountDownLatch latch) {
        ProcssingTaskThread t = new ProcssingTaskThread(rip, processingTask, threadPool, latch);
        threadPool.execute(t);
        return t;
    }

    public ThreadPoolExecutor getThreadPoolExecutor() {
        if (threadPoolExecutor == null) {
            setUpThreadPool();
        }
        return threadPoolExecutor;
    }



}

