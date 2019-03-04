package com.aurora.souschef.souschefprocessor.task.ingredientdetector;

import com.aurora.souschef.recipe.Ingredient;
import com.aurora.souschef.souschefprocessor.task.AbstractProcessingTask;
import com.aurora.souschef.souschefprocessor.task.RecipeInProgress;

import java.util.HashSet;
import java.util.Set;

/**
 * Detects the mIngredients in the list of mIngredients
 */
public class DetectIngredientsInListTask extends AbstractProcessingTask {

    // this is for the dummy detect code
    private static final  int INGREDIENT_WITH_UNIT_SIZE = 3;
    private static final  int INGREDIENT_WITHOUT_UNIT_SIZE = 2;
    private static final  int INGREDIENT_PLACE = 2;
    private static final  int UNIT_PLACE = 1;
    private static final  int AMOUNT_PLACE = 0;

    public DetectIngredientsInListTask(RecipeInProgress recipeInProgress) {
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
        // TODO generate functionality

        // dummy
        if (ingredientList == null || ("").equals(ingredientList)) {
            return new HashSet<>();
        }
        Set<Ingredient> returnSet = new HashSet<>();
        String[] list = ingredientList.split("\n");

        for (String ingredient : list) {
            if (ingredient != null) {
                if (ingredient.charAt(0) == ' ') {
                    ingredient = ingredient.substring(1);
                }
                String[] words = ingredient.split(" ");

                try {
                    Ingredient ing = (detectIngredient(words));
                    if (ing != null) {
                        returnSet.add(ing);
                    }
                } catch (NumberFormatException nfe) {
                    //TODO have appropriate catch if ingredient does not contain a number that is parseable to double
                }
            }
        }
        return returnSet;
    }

    private Ingredient detectIngredient(String[] line) {
        Ingredient ing = null;


        if (line.length == INGREDIENT_WITHOUT_UNIT_SIZE) {
            ing = new Ingredient(line[1], "", Double.valueOf(line[0]));
        }
        if (line.length == INGREDIENT_WITH_UNIT_SIZE) {

            ing = new Ingredient(line[INGREDIENT_PLACE], line[UNIT_PLACE], Double.valueOf(line[AMOUNT_PLACE]));
        }

        return ing;

    }
}
