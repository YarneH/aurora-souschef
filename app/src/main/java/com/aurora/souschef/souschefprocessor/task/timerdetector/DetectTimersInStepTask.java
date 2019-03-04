package com.aurora.souschef.souschefprocessor.task.timerdetector;

import android.util.Log;

import com.aurora.souschef.recipe.RecipeStep;
import com.aurora.souschef.recipe.RecipeTimer;
import com.aurora.souschef.souschefprocessor.task.AbstractProcessingTask;
import com.aurora.souschef.souschefprocessor.task.RecipeInProgress;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * A task that detects timers in mRecipeSteps
 */
public class DetectTimersInStepTask extends AbstractProcessingTask {
    private int mStepIndex;

    //these are for the dummy detect code
    private static final int SECONDS_IN_MINUTE = 60;
    private static final int STEP_MINUTES = 9;
    private static final int UP = 4;
    private static final int LOW = 3;

    public DetectTimersInStepTask(RecipeInProgress recipeInProgress, int stepIndex) {
        super(recipeInProgress);
        if (stepIndex < 0) {
            throw new IllegalArgumentException("Negative stepIndex passed");
        }
        if (stepIndex >= recipeInProgress.getRecipeSteps().size()) {
            throw new IllegalArgumentException("stepIndex passed too large, stepIndex: " + stepIndex
                    + " ,size of list: " + recipeInProgress.getRecipeSteps().size());
        }
        this.mStepIndex = stepIndex;
    }

    /**
     * Detects the RecipeTimer in all the mRecipeSteps
     */
    public void doTask() {
        RecipeStep recipeStep = mRecipeInProgress.getRecipeSteps().get(mStepIndex);
        List<RecipeTimer> recipeTimers = detectTimer(recipeStep);
        recipeStep.setRecipeTimers(recipeTimers);
    }


    /**
     * Detects the timer in a recipeStep
     *
     * @param recipeStep The recipeStep in which to detect a timer
     * @return A timer detected in the recipeStep
     */
    private List<RecipeTimer> detectTimer(RecipeStep recipeStep) {

        //dummy
        List<RecipeTimer> list = new ArrayList<>();
        try {

            if (recipeStep.getDescription().contains("9 minutes")) {


                list.add(new RecipeTimer(STEP_MINUTES * SECONDS_IN_MINUTE));
            } else {

                list.add(new RecipeTimer(UP * SECONDS_IN_MINUTE, LOW * SECONDS_IN_MINUTE));
            }

        } catch (IllegalArgumentException iae) {
            Log.e(TAG, "detectTimer: ", iae);
            //TODO do something meaningful
        }
        return list;
    }
}
