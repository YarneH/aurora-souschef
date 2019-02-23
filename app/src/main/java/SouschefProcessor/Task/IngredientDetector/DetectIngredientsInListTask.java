package SouschefProcessor.Task.IngredientDetector;

import java.util.HashSet;
import java.util.concurrent.ThreadPoolExecutor;

import SouschefProcessor.Recipe.Ingredient;
import SouschefProcessor.Recipe.RecipeInProgress;
import SouschefProcessor.Task.ProcessingTask;

/**
 * Detects the ingredients in the list of ingredients
 */
public class DetectIngredientsInListTask extends ProcessingTask {

    public DetectIngredientsInListTask(RecipeInProgress recipeInProgress){
        super(recipeInProgress);
    }

    /**
     * Detects the ingredients presented in the ingredientsString and sets the ingredients field
     * in the recipe to this set of ingredients.
     */
    public void doTask() {
        //TODO fallback if no ingredients can be detected
        HashSet<Ingredient> set = detectIngredients(this.recipeInProgress.getIngredientsString());
        this.recipeInProgress.setIngredients(set);
    }

    /**
     * Detetcs ingredients in a string representing an ingredient list, makes corresponding
     * Ingredient Objects and returns a set of these
     *
     * @param ingredientList
     * @return A set of Ingredient Objects detected in the string
     */
    public HashSet<Ingredient> detectIngredients(String ingredientList) {
        //TODO generate functionality

        //dummy
        if (ingredientList == null || ingredientList.equals("")) {
            return new HashSet<Ingredient>();
        }
        HashSet returnSet = new HashSet<Ingredient>();
        String[] list = ingredientList.split("\n");

        for (String ingredient : list) {

            if (ingredient != null && ingredient.charAt(0) == ' ') {
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

                returnSet.add(ing);
            } catch (NumberFormatException nfe) {
                //TODO have appropriate catch if ingredient does not contain a number that is parseable to double
            }
        }
        return returnSet;
    }


}
