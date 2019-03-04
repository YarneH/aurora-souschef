package com.aurora.souschef.souschefprocessor.task.sectiondivider;

import com.aurora.souschef.souschefprocessor.task.RecipeInProgress;
import com.aurora.souschef.recipe.RecipeStep;
import com.aurora.souschef.souschefprocessor.task.AbstractProcessingTask;

import java.util.ArrayList;
import java.util.List;

/**
 * A AbstractProcessingTask that splits the string representing the mRecipeSteps into RecipeStep objects
 */
public class SplitStepsTask extends AbstractProcessingTask {

    public SplitStepsTask(RecipeInProgress recipeInProgress) {
        super(recipeInProgress);
    }


    /**
     * This will split the stepsString in the RecipeInProgress Object into mRecipeSteps and modifies the
     * recipe object so that the mRecipeSteps are set
     */
    public void doTask() {
        List<RecipeStep> recipeStepList = divideIntoSteps(this.mRecipeInProgress.getStepsString());
        this.mRecipeInProgress.setRecipeSteps(recipeStepList);
    }

    /**
     * This function splits the text, describing the mRecipeSteps, into different mRecipeSteps of the recipe
     *
     * @return A list of all mRecipeSteps in order
     */
    private List<RecipeStep> divideIntoSteps(String steps) {
        //TODO generate functionality to split attribute stepsText

        // dummy code
        String[] array = steps.split("\n");
        List<RecipeStep> list = new ArrayList<>();
        for(String step : array){
            list.add(new RecipeStep(step));
        }
        return list;
    }

}
