package com.aurora.souschef.SouchefProcessor.Task.IngredientDetector;

import java.util.HashSet;

import com.aurora.souschef.SouchefProcessor.Recipe.Ingredient;
import com.aurora.souschef.SouchefProcessor.Recipe.RecipeInProgress;
import com.aurora.souschef.SouchefProcessor.Recipe.RecipeStep;
import com.aurora.souschef.SouchefProcessor.Task.ProcessingTask;

/**
 * Detects the mIngredients in the list of mIngredients
 */
public class DetectIngredientsInStepTask extends ProcessingTask {
    int mStepIndex;

    public DetectIngredientsInStepTask(RecipeInProgress recipeInProgress, int stepIndex) {
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
     * Detects the mIngredients for each recipeStep
    */
    public void doTask() {
        RecipeStep recipeStep = mRecipeInProgress.getRecipeSteps().get(mStepIndex);
        HashSet<Ingredient> ingredientSetRecipe = mRecipeInProgress.getIngredients();
        HashSet<Ingredient> iuaSet = detectIngredients(recipeStep, ingredientSetRecipe);
        recipeStep.setIngredients(iuaSet);
    }

    /**
     * Detects the set of mIngredients in a recipeStep. It also checks if this corresponds with the mIngredients of the
     * recipe.
     *
     * @param recipeStep                The recipeStep on which to detect the mIngredients
     * @param ingredientSetRecipe The set of mIngredients contained in the recipe of which the recipeStep is a part
     * @return A set of Ingredient objects that represent the mIngredients contained in the recipeStep
     */
    private HashSet<Ingredient> detectIngredients(RecipeStep recipeStep, HashSet<Ingredient> ingredientSetRecipe) {
        //TODO generate functionality

        //dummy
        HashSet<Ingredient> set = new HashSet<>();
        if (recipeStep.getDescription().contains("sauce")) {
            set.add(new Ingredient("sauce", "gram", 500));
        } else {
            set.add(new Ingredient("spaghetti", "gram", 500));
        }
        return set;
    }


}
