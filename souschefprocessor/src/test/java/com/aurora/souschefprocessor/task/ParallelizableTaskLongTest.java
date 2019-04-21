package com.aurora.souschefprocessor.task;

import com.aurora.souschefprocessor.facade.Delegator;
import com.aurora.souschefprocessor.recipe.RecipeStep;
import com.aurora.souschefprocessor.task.helpertasks.ParallelizeStepsTask;
import com.aurora.souschefprocessor.task.helpertasks.StepTaskNames;
import com.aurora.souschefprocessor.task.timerdetector.DetectTimersInStepTask;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.aurora.souschefprocessor.task.helpertasks.StepTaskNames.INGR;
import static com.aurora.souschefprocessor.task.helpertasks.StepTaskNames.TIMER;

public class ParallelizableTaskLongTest {

    private static ThreadPoolExecutor mThreadPoolExecutor;
    private static List<RecipeStep> recipeSteps = new ArrayList<>();
    private static StepTaskNames[] onlyTimerName = {TIMER};
    private static StepTaskNames[] onlyIngrName = {INGR};
    private static StepTaskNames[] both = {TIMER, INGR};
    private static ParallelizeStepsTask onlyTimerstask;
    private static ParallelizeStepsTask onlyIngrtask;
    private static ParallelizeStepsTask bothtasks;

    @BeforeClass
    public static void initialize() {
        RecipeInProgress rip = new RecipeInProgress(null);
        DetectTimersInStepTask.initializeAnnotationPipeline();
        recipeSteps.add(new RecipeStep("Put 500 gram sauce in the microwave for 3 minutes")); //0 minutes
        recipeSteps.add(new RecipeStep("Heat the oil in a saucepan and gently fry the onion until softened, about 4-5 minutes.")); //1 upperbound and lowerbound with dash //"Put 500 gram spaghetti in boiling water 7 to 9 minutes")); //1 (upperbound and lowerbound different)
        recipeSteps.add(new RecipeStep("Put in the oven for 30 minutes and let rest for 20 minutes.")); //2 (two timers)
        recipeSteps.add(new RecipeStep("Grate cheese for 30 seconds")); //3 (seconds)
        recipeSteps.add(new RecipeStep("Wait for 4 hours")); //4 (hours)
        recipeSteps.add(new RecipeStep("Let cool down for an hour and a half.")); //5 (verbose hour)
        recipeSteps.add(new RecipeStep("Put the lasagna in the oven for 1h"));//6 (symbol hour)
        recipeSteps.add(new RecipeStep("Put 500 gram spaghetti in boiling water 7 to 9 minutes")); //7 (upperbound and lowerbound different)))
        rip.setRecipeSteps(recipeSteps);
        setUpThreadPool();
        onlyTimerstask = new ParallelizeStepsTask(rip, mThreadPoolExecutor, onlyTimerName);
        onlyIngrtask = new ParallelizeStepsTask(rip, mThreadPoolExecutor, onlyIngrName);
        bothtasks = new ParallelizeStepsTask(rip, mThreadPoolExecutor, both);
    }

    private static void setUpThreadPool() {
        /*
         * Gets the number of available cores
         * (not always the same as the maximum number of cores)
         */
        int numberOfCores =
                Runtime.getRuntime().availableProcessors();
        // A queue of Runnables
        final BlockingQueue<Runnable> decodeWorkQueue;
        // Instantiates the queue of Runnables as a LinkedBlockingQueue
        decodeWorkQueue = new LinkedBlockingQueue<>();
        // Sets the amount of time an idle thread waits before terminating
        final int KEEP_ALIVE_TIME = 1;
        // Sets the Time Unit to seconds
        final TimeUnit keepAliveTimeUnit = TimeUnit.SECONDS;
        // Creates a thread pool manager
        mThreadPoolExecutor = new ThreadPoolExecutor(
                // Initial pool size
                numberOfCores,
                // Max pool size
                numberOfCores,
                KEEP_ALIVE_TIME,
                keepAliveTimeUnit,
                decodeWorkQueue);
    }

    @After
    public void wipeRecipe() {
        for (RecipeStep step : recipeSteps) {
            step.setIngredients(null);
            step.setRecipeTimers(null);
            step.setIngredientDetectionDone(false);
            step.setTimerDetectionDone(false);
        }
    }

    @Test
    public void ParrallelizableStepTask_doTask_TimersDetectedForAllSteps() {
        // The parallel tasks set the timers for all steps

        // Arrange
        onlyTimerstask.doTask();

        // Assert
        for (RecipeStep step : recipeSteps) {
            assert (step.isTimerDetectionDone());
            // for each of these steps a timer can be detected so assert non null value
            assert (step.getRecipeTimers() != null);
        }
    }


}
