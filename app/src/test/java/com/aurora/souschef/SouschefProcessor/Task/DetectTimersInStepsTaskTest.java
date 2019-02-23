package com.aurora.souschef.SouschefProcessor.Task;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import SouschefProcessor.Recipe.RecipeInProgress;
import SouschefProcessor.Recipe.Step;
import SouschefProcessor.Task.TimerDetector.DetectTimersInStepsTask;

public class DetectTimersInStepsTaskTest {

    private static DetectTimersInStepsTask detector;
    private static RecipeInProgress recipe;
    private static ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(20);
    private static ArrayList<Step> steps;

    @BeforeClass
    public static void initialize() {
        detector = new DetectTimersInStepsTask();
        steps = new ArrayList<>();
        Step s1 = new Step("Put 500 gram sauce in the microwave for 3 minutes");
        Step s2 = new Step("Put 500 gram spaghetti in boiling water 9 minutes");
        steps.add(s1);
        steps.add(s2);
        String originalText = "irrelevant";
        recipe = new RecipeInProgress(originalText);
        recipe.setSteps(steps);

    }

    @After
    public void wipeRecipeSteps() {
        for (Step s : steps) {
            s.unsetTimer();
        }
    }


    @Test
    public void TimerDetectorStep_doTask_timerHasBeenSetForAllSteps() {
        detector.doTask(recipe, threadPoolExecutor);
        for (Step s : recipe.getSteps()) {
            assert (s.isTimerDetected());
            assert (s.getTimer() != null);
        }
    }
}
