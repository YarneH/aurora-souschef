package SouschefProcessor.Facade;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import SouschefProcessor.Recipe.Recipe;
import SouschefProcessor.Recipe.RecipeInProgress;
import SouschefProcessor.Task.HelperTasks.ParallelizeStepsTask;
import SouschefProcessor.Task.IngredientDetector.DetectIngredientsInListTask;
import SouschefProcessor.Task.ProcessingTask;
import SouschefProcessor.Task.SectionDivider.DetectNumberOfPeopleTask;
import SouschefProcessor.Task.SectionDivider.SplitStepsTask;
import SouschefProcessor.Task.SectionDivider.SplitToMainSectionsTask;

/**
 * Implements the processing by applying the filters. This implements the order of the pipeline as
 * described in the architecture.
 */
public class Delegator {

    private ThreadPoolExecutor threadPoolExecutor; //TODO Maybe all threadpool stuff can be moved to ParallelizeSteps

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
     * @return A Recipe object that was constructed from the text
     */
    public Recipe processText(String text) {
        //TODO implement this function so that at runtime it is decided which tasks should be performed
        if (threadPoolExecutor == null) {
            setUpThreadPool();
        }
        RecipeInProgress recipeInProgress = new RecipeInProgress(text);
        ArrayList<ProcessingTask> pipeline = setUpPipeline(recipeInProgress);
        if (pipeline != null) {
            for (ProcessingTask task : pipeline) {
                task.doTask();
            }
        }


        return recipeInProgress.convertToRecipe();
    }



    /**
     * The function creates all the tasks that could be used for the processing. If new tasks are added to the c
     * codebase they should be created here as well.
     */
    public ArrayList<ProcessingTask> setUpPipeline(RecipeInProgress recipeInProgress) {
        ArrayList<ProcessingTask> pipeline = new ArrayList<>();
        pipeline.add(new DetectNumberOfPeopleTask(recipeInProgress));
        pipeline.add(new SplitToMainSectionsTask(recipeInProgress));
        pipeline.add(new SplitStepsTask(recipeInProgress));
        pipeline.add(new DetectIngredientsInListTask(recipeInProgress));
        String[] taskNames = {"INGR", "TIMER"};
        pipeline.add(new ParallelizeStepsTask(recipeInProgress, this.threadPoolExecutor, taskNames));
        return pipeline;
    }


    public ThreadPoolExecutor getThreadPoolExecutor() {
        if (threadPoolExecutor == null) {
            setUpThreadPool();
        }
        return threadPoolExecutor;
    }


}

