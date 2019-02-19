package SouschefProcessor.IngredientDetector;

import java.util.ArrayList;

import SouschefProcessor.StepSplitter.Step;

final class IngredientDetector {


    public ArrayList<IngredientUnitAmount> detectIngredients(String ingredientList){
        //TODO generate functionality

        //dummy
        ArrayList returnList = new ArrayList<IngredientUnitAmount>();
        String[] list = ingredientList.split("\n");

        for(String ingredient: list){
            String[] ingredientUnitAmount  = ingredient.split(" ");


            try {
                IngredientUnitAmount ing = null;

                if (ingredientUnitAmount.length == 2) {
                    ing = new IngredientUnitAmount(ingredientUnitAmount[1], "", Double.valueOf(ingredientUnitAmount[0]));
                } else if (ingredientUnitAmount.length == 3) {
                    ing = new IngredientUnitAmount(ingredientUnitAmount[2], ingredientUnitAmount[1], Double.valueOf(ingredientUnitAmount[0]));
                }

                returnList.add(ing);
            }
            catch(NumberFormatException nfe){
                //TODO have appropriate catch if ingredient does not contain a number that is parseable to double
            }
        }
        return returnList;
    }

    public ArrayList<IngredientUnitAmount> detectIngredients(Step step){
        //TODO generate functionality

        //dummy
        ArrayList list = new ArrayList<IngredientUnitAmount>();
        if(step.getDescription().contains("sauce")){
            list.add(new IngredientUnitAmount("spaghetti sauce", "gram", 500));
        }
        else{
            list.add(new IngredientUnitAmount("spaghetti", "gram", 500));
        }
        return list;
    }
}
