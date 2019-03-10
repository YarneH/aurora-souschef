package com.aurora.souschef.souschefprocessor.task.ingredientdetector;

import com.aurora.souschef.recipe.Ingredient;
import com.aurora.souschef.recipe.RecipeStep;
import com.aurora.souschef.souschefprocessor.task.AbstractProcessingTask;
import com.aurora.souschef.souschefprocessor.task.RecipeInProgress;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Detects the mIngredients in the list of mIngredients
 */
public class DetectIngredientsInStepTask extends AbstractProcessingTask {
    // fields for the dummy code
    private static final int AMOUNT = 500;
    private int mStepIndex;

    public DetectIngredientsInStepTask(RecipeInProgress recipeInProgress, int stepIndex) {
        super(recipeInProgress);
        if (stepIndex < 0) {
            throw new IllegalArgumentException("Negative stepIndex passed");
        }
        if (stepIndex >= recipeInProgress.getRecipeSteps().size()) {
            throw new IllegalArgumentException("stepIndex passed too large, stepIndex: "
                    + stepIndex + " ,size of list: " + recipeInProgress.getRecipeSteps().size());
        }
        this.mStepIndex = stepIndex;
    }

    /**
     * Detects the mIngredients for each recipeStep
     */
    public void doTask() {
        RecipeStep recipeStep = mRecipeInProgress.getRecipeSteps().get(mStepIndex);
        List<Ingredient> ingredientListRecipe = mRecipeInProgress.getIngredients();
        Set<Ingredient> iuaSet = detectIngredients(recipeStep, ingredientListRecipe);
        recipeStep.setIngredients(iuaSet);
    }

    /**
     * Detects the set of mIngredients in a recipeStep. It also checks if this corresponds with the mIngredients of the
     * recipe.
     *
     * @param recipeStep           The recipeStep on which to detect the mIngredients
     * @param ingredientListRecipe The set of mIngredients contained in the recipe of which the recipeStep is a part
     * @return A set of Ingredient objects that represent the mIngredients contained in the recipeStep
     */
    private Set<Ingredient> detectIngredients(RecipeStep recipeStep, List<Ingredient> ingredientListRecipe) {
        // TODO generate functionality

        // dummy
        Set<Ingredient> set = new HashSet<>();
        if (ingredientListRecipe != null) {

            if (recipeStep.getDescription().contains("sauce")) {
                set.add(new Ingredient("sauce", "gram", AMOUNT, recipeStep.getDescription()));
            } else {
                set.add(new Ingredient("spaghetti", "gram", AMOUNT, recipeStep.getDescription()));
            }
        }
        return set;
    }


}
