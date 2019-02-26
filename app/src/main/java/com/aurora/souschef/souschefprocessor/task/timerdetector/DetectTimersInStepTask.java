package com.aurora.souschef.souschefprocessor.task.timerdetector;

import java.util.ArrayList;
import java.util.List;

import com.aurora.souschef.souschefprocessor.recipe.RecipeInProgress;
import com.aurora.souschef.souschefprocessor.recipe.RecipeStep;
import com.aurora.souschef.souschefprocessor.recipe.RecipeTimer;
import com.aurora.souschef.souschefprocessor.task.ProcessingTask;

/**
 * A task that detects timers in mRecipeSteps
 */
public class DetectTimersInStepTask extends ProcessingTask {
    int mStepIndex;

    public DetectTimersInStepTask(RecipeInProgress recipeInProgress, int stepIndex) {
        super(recipeInProgress);
        if(stepIndex < 0){
            throw new IllegalArgumentException("Negative stepIndex passed");
        }
        if (stepIndex >= recipeInProgress.getRecipeSteps().size()){
            throw new IllegalArgumentException("stepIndex passed too large, stepIndex: "+stepIndex +" ,size of list: "+recipeInProgress.getRecipeSteps().size());
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
                list.add(new RecipeTimer(9 * 60));
            } else {
                list.add(new RecipeTimer(3 * 60, 3 * 60));
            }

        } catch (IllegalArgumentException tvie) {
            //TODO do something meaningful
        }
        return list;
    }
}
