package com.aurora.souschef.SouschefProcessor.task;

import com.aurora.souschef.recipe.RecipeStep;
import com.aurora.souschef.recipe.RecipeTimer;
import com.aurora.souschef.souschefprocessor.task.RecipeInProgress;
import com.aurora.souschef.souschefprocessor.task.timerdetector.DetectTimersInStepTask;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class DetectTimersInStepTaskTest {

    private static List<DetectTimersInStepTask> detectors = new ArrayList<>();
    private static RecipeInProgress recipe;
    private static ArrayList<RecipeStep> recipeSteps;

    @BeforeClass
    public static void initialize() {
        recipeSteps = new ArrayList<>();
        recipeSteps.add(new RecipeStep("Put 500 gram sauce in the microwave for 3 minutes")); //0 minutes
        recipeSteps.add(new RecipeStep("Put 500 gram spaghetti in boiling water 7 to 9 minutes")); //1 (upperbound and lowerbound different)
        recipeSteps.add(new RecipeStep("Put in the oven for 30 minutes and let rest for 20 minutes.")); //2 (two timers)
        recipeSteps.add(new RecipeStep("Grate cheese for 30 seconds")); //3 (seconds)
        recipeSteps.add(new RecipeStep("Wait for 4 hours")); //4 (hours)
        recipeSteps.add(new RecipeStep("Let cool down for an hour and a half.")); //5 (verbose hour)
        recipeSteps.add(new RecipeStep("Put the lasagna in the oven for 1h"));//6 (symbol hour)

        int stepIndex0 = 0;
        int stepIndex1 = 1;
        String originalText = "irrelevant";
        recipe = new RecipeInProgress(originalText);
        recipe.setRecipeSteps(recipeSteps);
        for (int stepIndex = 0; stepIndex < recipeSteps.size(); stepIndex++) {
            detectors.add(new DetectTimersInStepTask(recipe, stepIndex));
        }

    }

    @After
    public void wipeRecipeSteps() {
        for (RecipeStep s : recipeSteps) {
            s.unsetTimer();
        }
    }


    @Test
    public void DetectTimersInStep_doTask_timersHaveBeenSetForAllSteps() {
        for (DetectTimersInStepTask detector : detectors) {
            detector.doTask();
        }
        for (RecipeStep s : recipe.getRecipeSteps()) {
            assert (s.isTimerDetected());
            assert (s.getRecipeTimers() != null);
        }
    }

    @Test
    public void DetectTimersInStep_doTask_detectMinuteTimer() {
        int stepIndex = 0; //index zero has minutes
        DetectTimersInStepTask detector = detectors.get(stepIndex); //index zero has minutes
        detector.doTask();
        //assert detection
        assert (recipeSteps.get(stepIndex).getRecipeTimers().size() > 0);
        //assert correct detection
        RecipeTimer timer = new RecipeTimer(3 * 60);
        assert (timer.equals(recipe.getRecipeSteps().get(stepIndex).getRecipeTimers().get(0)));

    }

    @Test
    public void DetectTimersInStep_doTask_detectHoursTimer() {
        int stepIndex = 4; //index four has hours
        DetectTimersInStepTask detector = detectors.get(stepIndex);
        detector.doTask();
        RecipeTimer timer = new RecipeTimer(4 * 60 * 60);
        assert (timer.equals(recipe.getRecipeSteps().get(stepIndex).getRecipeTimers().get(0)));
    }

    @Test
    public void DetectTimersInStep_doTask_detectSecondsTimer() {
        int stepIndex = 3; //index three has seconds
        DetectTimersInStepTask detector = detectors.get(stepIndex);
        detector.doTask();
        //assert detection
        assert (recipeSteps.get(stepIndex).getRecipeTimers().size() > 0);
        //assert correct detection
        RecipeTimer timer = new RecipeTimer(30);
        assert (timer.equals(recipe.getRecipeSteps().get(stepIndex).getRecipeTimers().get(0)));
    }

    @Test
    public void DetectTimersInStep_doTask_detectMultipleTimers() {
        int stepIndex = 2; //index two has multiple timers
        DetectTimersInStepTask detector = detectors.get(stepIndex);
        detector.doTask();
        //assert detection
        assert (recipeSteps.get(stepIndex).getRecipeTimers().size() > 0);
        //assert correct detection
        RecipeTimer timer1 = new RecipeTimer(30 * 60);
        RecipeTimer timer2 = new RecipeTimer(20 * 60);
        assert (recipeSteps.get(stepIndex).getRecipeTimers().contains(timer1));
        assert (recipeSteps.get(stepIndex).getRecipeTimers().contains(timer2));
    }

    @Test
    public void DetectTimersInStep_doTask_verboseHoursTimers() {
        int stepIndex = 5; //index five has verbose hours
        DetectTimersInStepTask detector = detectors.get(stepIndex);
        detector.doTask();
        //assert detection
        assert (recipeSteps.get(stepIndex).getRecipeTimers().size() > 0);
        //assert correct detection
        RecipeTimer timer = new RecipeTimer((int) (60 * 60 * 1.5));
        assert (timer.equals(recipe.getRecipeSteps().get(stepIndex).getRecipeTimers().get(0)));
    }

    @Test
    public void DetectTimersInStep_doTask_hourSymbolTimers() {
        int stepIndex = 6; //index six has symbol hours
        DetectTimersInStepTask detector = detectors.get(stepIndex);
        detector.doTask();
        //assert detection
        assert (recipeSteps.get(stepIndex).getRecipeTimers().size() > 0);
        //assert correct detection
        RecipeTimer timer = new RecipeTimer(60 * 60);
        assert (timer.equals(recipe.getRecipeSteps().get(stepIndex).getRecipeTimers().get(0)));
    }

    @Test
    public void DetectTimersInStep_doTask_upperBoundAndLowerBoundNotEqual() {
        int stepIndex = 1; //index 1 has upper and lower bound
        DetectTimersInStepTask detector = detectors.get(stepIndex);
        detector.doTask();
        //assert detection
        assert (recipeSteps.get(stepIndex).getRecipeTimers().size() > 0);
        //assert correct detection
        RecipeTimer timer = new RecipeTimer(9 * 60, 7 * 60);
        assert (timer.equals(recipe.getRecipeSteps().get(stepIndex).getRecipeTimers().get(0)));
    }

}
