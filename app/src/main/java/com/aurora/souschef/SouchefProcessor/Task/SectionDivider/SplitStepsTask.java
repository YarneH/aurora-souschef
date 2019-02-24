package com.aurora.souschef.SouchefProcessor.Task.SectionDivider;

import java.util.ArrayList;

import com.aurora.souschef.SouchefProcessor.Recipe.RecipeInProgress;
import com.aurora.souschef.SouchefProcessor.Recipe.RecipeStep;
import com.aurora.souschef.SouchefProcessor.Task.ProcessingTask;

/**
 * A ProcessingTask that splits the string representing the mRecipeSteps into RecipeStep objects
 */
public class SplitStepsTask extends ProcessingTask {

    public SplitStepsTask(RecipeInProgress recipeInProgress){
        super(recipeInProgress);
    }


    /**
     * This will split the stepsString in the RecipeInProgress Object into mRecipeSteps and modifies the
     * recipe object so that the mRecipeSteps are set
     */
    public void doTask() {
        ArrayList<RecipeStep> recipeStepList = divideIntoSteps(this.mRecipeInProgress.getStepsString());
        this.mRecipeInProgress.setRecipeSteps(recipeStepList);
    }

    /**
     * This function splits the text, describing the mRecipeSteps, into different mRecipeSteps of the recipe
     *
     * @return A list of all mRecipeSteps in order
     */
    private ArrayList<RecipeStep> divideIntoSteps(String steps) {
        //TODO generate functionality to split attribute stepsText

        //dummy code
        ArrayList<RecipeStep> list = new ArrayList<>();
        RecipeStep s1 = new RecipeStep("Put 500 gram spaghetti in boiling water for 9 minutes.");
        RecipeStep s2 = new RecipeStep("Put the sauce in the Microwave for 3 minutes");
        list.add(s1);
        list.add(s2);
        return list;
    }

}
