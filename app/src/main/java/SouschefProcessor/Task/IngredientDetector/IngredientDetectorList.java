package SouschefProcessor.Task.IngredientDetector;

import java.util.ArrayList;

import java.util.HashSet;


import SouschefProcessor.Recipe.IngredientUnitAmount;

import SouschefProcessor.Recipe.RecipeInProgress;
import SouschefProcessor.Recipe.Step;
import SouschefProcessor.Task.Task;

/**
 * Detects the ingredients in the list of ingredients
 */
public class IngredientDetectorList implements Task {

    private String ingredientList;



    public IngredientDetectorList(String ingredientList) {
        this.ingredientList = ingredientList;
    }


    public void doTask(RecipeInProgress recipe){
        HashSet<IngredientUnitAmount> set = detectIngredients(ingredientList);
        recipe.setIngredients(set);
    }


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
