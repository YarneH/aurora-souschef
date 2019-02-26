package com.aurora.souschef.souschefprocessor.task;

import com.aurora.souschef.souschefprocessor.recipe.RecipeInProgress;
import com.aurora.souschef.souschefprocessor.recipe.RecipeStep;
import com.aurora.souschef.souschefprocessor.task.timerdetector.DetectTimersInStepTask;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class DetectTimersInStepTaskTest {

    private static DetectTimersInStepTask detector0;
    private static DetectTimersInStepTask detector1;
    private static RecipeInProgress recipe;
    private static ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(20);
    private static ArrayList<RecipeStep> recipeSteps;

    @BeforeClass
    public static void initialize() {
        recipeSteps = new ArrayList<>();
        RecipeStep s1 = new RecipeStep("Put 500 gram sauce in the microwave for 3 minutes");
        RecipeStep s2 = new RecipeStep("Put 500 gram spaghetti in boiling water 9 minutes");
        recipeSteps.add(s1);
        recipeSteps.add(s2);
        int stepIndex0 = 0;
        int stepIndex1 = 1;
        String originalText = "irrelevant";
        recipe = new RecipeInProgress(originalText);
        recipe.setRecipeSteps(recipeSteps);
        detector0 = new DetectTimersInStepTask(recipe, stepIndex0);
        detector1 = new DetectTimersInStepTask(recipe, stepIndex1);



    }

    @After
    public void wipeRecipeSteps() {
        for (RecipeStep s : recipeSteps) {
            s.unsetTimer();
        }
    }


    @Test
    public void TimerDetectorStep_doTask_timersHaveBeenSetForAllSteps() {
        detector0.doTask();
        detector1.doTask();
        for (RecipeStep s : recipe.getRecipeSteps()) {
            assert (s.isTimerDetected());
            assert (s.getRecipeTimers() != null);
        }
    }
}
