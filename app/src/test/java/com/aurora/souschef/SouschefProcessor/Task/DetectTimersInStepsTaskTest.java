package com.aurora.souschef.SouschefProcessor.Task;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import SouschefProcessor.Recipe.RecipeInProgress;
import SouschefProcessor.Recipe.RecipeStep;
import SouschefProcessor.Task.TimerDetector.DetectTimersInStepsTask;

public class DetectTimersInStepsTaskTest {

    private static DetectTimersInStepsTask detector;
    private static RecipeInProgress recipe;
    private static ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(20);
    private static ArrayList<RecipeStep> recipeSteps;

    @BeforeClass
    public static void initialize() {
        detector = new DetectTimersInStepsTask();
        recipeSteps = new ArrayList<>();
        RecipeStep s1 = new RecipeStep("Put 500 gram sauce in the microwave for 3 minutes");
        RecipeStep s2 = new RecipeStep("Put 500 gram spaghetti in boiling water 9 minutes");
        recipeSteps.add(s1);
        recipeSteps.add(s2);
        String originalText = "irrelevant";
        recipe = new RecipeInProgress(originalText);
        recipe.setRecipeSteps(recipeSteps);

    }

    @After
    public void wipeRecipeSteps() {
        for (RecipeStep s : recipeSteps) {
            s.unsetTimer();
        }
    }


    @Test
    public void TimerDetectorStep_doTask_timersHaveBeenSetForAllSteps() {
        detector.doTask(recipe, threadPoolExecutor);
        for (RecipeStep s : recipe.getRecipeSteps()) {
            assert (s.isTimerDetected());
            assert (s.getRecipeTimers() != null);
        }
    }
}
