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
public class DetectTimersInStepTask extends ProcessingTask {
    int stepIndex;

    public DetectTimersInStepTask(RecipeInProgress recipeInProgress, int stepIndex) {
        super(recipeInProgress);
        this.stepIndex = stepIndex;
    }

    /**
     * Detects the RecipeTimer in all the recipeSteps
     */
    public void doTask() {
        RecipeStep recipeStep = recipeInProgress.getRecipeSteps().get(stepIndex);
        ArrayList<RecipeTimer> recipeTimers = detectTimer(recipeStep);
        recipeStep.setRecipeTimers(recipeTimers);
    }


    /**
     * Detects the timer in a recipeStep
     *
     * @param recipeStep The recipeStep in which to detect a timer
     * @return A timer detected in the recipeStep
     */
    private ArrayList<RecipeTimer> detectTimer(RecipeStep recipeStep) {

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
}
