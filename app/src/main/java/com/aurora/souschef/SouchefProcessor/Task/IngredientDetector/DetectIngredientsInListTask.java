package com.aurora.souschef.SouchefProcessor.Task.IngredientDetector;

import java.util.HashSet;
import java.util.Set;

import com.aurora.souschef.SouchefProcessor.Recipe.Ingredient;
import com.aurora.souschef.SouchefProcessor.Recipe.RecipeInProgress;
import com.aurora.souschef.SouchefProcessor.Task.ProcessingTask;

/**
 * Detects the mIngredients in the list of mIngredients
 */
public class DetectIngredientsInListTask extends ProcessingTask {

    public DetectIngredientsInListTask(RecipeInProgress recipeInProgress){
        super(recipeInProgress);
    }

    /**
     * Detects the mIngredients presented in the ingredientsString and sets the mIngredients field
     * in the recipe to this set of mIngredients.
     */
    public void doTask() {
        //TODO fallback if no mIngredients can be detected
        Set<Ingredient> set = detectIngredients(this.mRecipeInProgress.getIngredientsString());
        this.mRecipeInProgress.setIngredients(set);
    }

    /**
     * Detetcs ingredients in a string representing an ingredient list, makes corresponding
     * Ingredient Objects and returns a set of these
     *
     * @param ingredientList The string representing the ingredientList
     * @return A set of Ingredient Objects detected in the string
     */
    private Set<Ingredient> detectIngredients(String ingredientList) {
        //TODO generate functionality

        //dummy
        if (ingredientList == null || ingredientList.equals("")) {
            return new HashSet<Ingredient>();
        }
        Set<Ingredient> returnSet = new HashSet<Ingredient>();
        String[] list = ingredientList.split("\n");

        for (String ingredient : list) {
            if (ingredient != null) {
                if (ingredient.charAt(0) == ' ') {
                    ingredient = ingredient.substring(1);
                }
                String[] ingredientUnitAmount = ingredient.split(" ");


                try {
                    Ingredient ing = null;

                    if (ingredientUnitAmount.length == 2) {
                        ing = new Ingredient(ingredientUnitAmount[1], "", Double.valueOf(ingredientUnitAmount[0]));
                    } else if (ingredientUnitAmount.length == 3) {
                        ing = new Ingredient(ingredientUnitAmount[2], ingredientUnitAmount[1], Double.valueOf(ingredientUnitAmount[0]));
                    }
                    if (ing != null){
                        returnSet.add(ing);
                    }

                } catch (NumberFormatException nfe) {
                    //TODO have appropriate catch if ingredient does not contain a number that is parseable to double
                }
            }
        }
        return returnSet;
    }


}
