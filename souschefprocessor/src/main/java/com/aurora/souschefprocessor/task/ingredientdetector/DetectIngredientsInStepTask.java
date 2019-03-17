package com.aurora.souschefprocessor.task.ingredientdetector;

import com.aurora.souschefprocessor.recipe.Ingredient;
import com.aurora.souschefprocessor.recipe.ListIngredient;
import com.aurora.souschefprocessor.recipe.Position;
import com.aurora.souschefprocessor.recipe.RecipeStep;
import com.aurora.souschefprocessor.task.AbstractProcessingTask;
import com.aurora.souschefprocessor.task.RecipeInProgress;

import java.util.HashMap;
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
        List<ListIngredient> ingredientListRecipe = mRecipeInProgress.getIngredients();
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
    private Set<Ingredient> detectIngredients(RecipeStep recipeStep, List<ListIngredient> ingredientListRecipe) {
        // TODO generate functionality

        // dummy
        Set<Ingredient> set = new HashSet<>();
        if (ingredientListRecipe != null) {
            HashMap<Ingredient.PositionKey, Position> map = new HashMap<>();
            Position pos = new Position(0, 1);
            for (Ingredient.PositionKey key : Ingredient.PositionKey.values()) {
                map.put(key, pos);
            }

            if (recipeStep.getDescription().contains("sauce")) {
                set.add(new Ingredient("sauce", "gram", AMOUNT, map));
            } else {
                set.add(new Ingredient("spaghetti", "gram", AMOUNT, map));
            }
        }
        return set;
    }


}