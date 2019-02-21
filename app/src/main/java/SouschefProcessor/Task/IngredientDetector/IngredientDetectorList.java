package SouschefProcessor.Task.IngredientDetector;

import java.util.HashSet;
import java.util.concurrent.ThreadPoolExecutor;

import SouschefProcessor.Recipe.IngredientUnitAmount;
import SouschefProcessor.Recipe.RecipeInProgress;
import SouschefProcessor.Task.Task;

/**
 * Detects the ingredients in the list of ingredients
 */
public class IngredientDetectorList implements Task {

    /**
     * Detects the ingredients presented in the ingredientsString and sets the ingredients field
     * in the recipe to this set of ingredients.
     * @param recipe The recipe on which to detect the ingredients and to modify
     * @param threadPool The threadpool on which to execute threads within this task
     */
    public void doTask(RecipeInProgress recipe, ThreadPoolExecutor threadPool){
        //TODO fallback if no ingredients can be detected
        HashSet<IngredientUnitAmount> set = detectIngredients(recipe.getIngredientsString());
        recipe.setIngredients(set);
    }

    /**
     * Detetcs ingredients in a string representing an ingredient list, makes corresponding
     * IngredientUnitAmount Objects and returns a set of these
     * @param ingredientList
     * @return A set of IngredientUnitAmount Objects detected in the string
     */
    private HashSet<IngredientUnitAmount> detectIngredients(String ingredientList){
        //TODO generate functionality

        //dummy
        if(ingredientList.equals("")){
            return new HashSet<IngredientUnitAmount>();
        }
        HashSet returnSet = new HashSet<IngredientUnitAmount>();
        String[] list = ingredientList.split("\n");

        for(String ingredient: list){
            if (ingredient.charAt(0) == ' '){
                ingredient = ingredient.substring(1);
            }
            String[] ingredientUnitAmount  = ingredient.split(" ");


            try {
                IngredientUnitAmount ing = null;

                if (ingredientUnitAmount.length == 2) {
                    ing = new IngredientUnitAmount(ingredientUnitAmount[1], "", Double.valueOf(ingredientUnitAmount[0]));
                } else if (ingredientUnitAmount.length == 3) {
                    ing = new IngredientUnitAmount(ingredientUnitAmount[2], ingredientUnitAmount[1], Double.valueOf(ingredientUnitAmount[0]));
                }

                returnSet.add(ing);
            }
            catch(NumberFormatException nfe){
                //TODO have appropriate catch if ingredient does not contain a number that is parseable to double
            }
        }
        return returnSet;
    }


}
