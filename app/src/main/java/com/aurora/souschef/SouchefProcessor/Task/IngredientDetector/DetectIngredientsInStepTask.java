package com.aurora.souschef.SouchefProcessor.Task.IngredientDetector;

import com.aurora.souschef.SouchefProcessor.Recipe.Ingredient;
import com.aurora.souschef.SouchefProcessor.Recipe.RecipeInProgress;
import com.aurora.souschef.SouchefProcessor.Recipe.RecipeStep;
import com.aurora.souschef.SouchefProcessor.Task.ProcessingTask;

import java.util.HashSet;
import java.util.Set;

/**
 * Detects the mIngredients in the list of mIngredients
 */
public class DetectIngredientsInStepTask extends ProcessingTask {
    int mStepIndex;

    public DetectIngredientsInStepTask(RecipeInProgress recipeInProgress, int stepIndex) {
        super(recipeInProgress);
        if (stepIndex < 0) {
            throw new IllegalArgumentException("Negative stepIndex passed");
        }
        if (stepIndex >= recipeInProgress.getRecipeSteps().size()) {
            throw new IllegalArgumentException("stepIndex passed too large, stepIndex: " + stepIndex + " ,size of list: " + recipeInProgress.getRecipeSteps().size());
        }
        this.mStepIndex = stepIndex;
    }

    /**
     * Detects the mIngredients for each recipeStep
     */
    public void doTask() {
        RecipeStep recipeStep = mRecipeInProgress.getRecipeSteps().get(mStepIndex);
        Set<Ingredient> ingredientSetRecipe = mRecipeInProgress.getIngredients();
        Set<Ingredient> iuaSet = detectIngredients(recipeStep, ingredientSetRecipe);
        recipeStep.setIngredients(iuaSet);
    }

    /**
     * Detects the set of mIngredients in a recipeStep. It also checks if this corresponds with the mIngredients of the
     * recipe.
     *
     * @param recipeStep          The recipeStep on which to detect the mIngredients
     * @param ingredientSetRecipe The set of mIngredients contained in the recipe of which the recipeStep is a part
     * @return A set of Ingredient objects that represent the mIngredients contained in the recipeStep
     */
    private Set<Ingredient> detectIngredients(RecipeStep recipeStep, Set<Ingredient> ingredientSetRecipe) {
        //TODO generate functionality

        //dummy
        Set<Ingredient> set = new HashSet<>();
        if (recipeStep.getDescription().contains("sauce")) {
            set.add(new Ingredient("sauce", "gram", 500));
        } else {
            set.add(new Ingredient("spaghetti", "gram", 500));
        }
        return set;
    }


}
