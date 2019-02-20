package SouschefProcessor.IngredientDetector;

import java.util.ArrayList;
import java.util.HashMap;

import SouschefProcessor.StepSplitter.Step;

public class IngredientDetector {

    private ArrayList<IngredientUnitAmount> ingredientList;
    private HashMap<Step, ArrayList<IngredientUnitAmount>> ingredientsPerStep;


    private IngredientDetector(ArrayList<IngredientUnitAmount> ingredientList, HashMap<Step, ArrayList<IngredientUnitAmount>> ingredientsPerStep) {
        this.ingredientList = ingredientList;
        this.ingredientsPerStep = ingredientsPerStep;
    }



    public ArrayList<IngredientUnitAmount> getIngredientList() {
        return ingredientList;
    }

    public HashMap<Step, ArrayList<IngredientUnitAmount>> getIngredientsPerStep() {
       return ingredientsPerStep;
    }


    public static IngredientDetector createIngredientDetector(String ingredients, ArrayList<Step> steps){
        //detect ingredients in list
        ArrayList<IngredientUnitAmount> list = detectIngredients(ingredients);

        //detect ingredients for steps
        HashMap<Step, ArrayList<IngredientUnitAmount>> map = new HashMap<>();

        for(Step s : steps){
            ArrayList<IngredientUnitAmount> stepIngredients = detectIngredients(s);
            map.put(s,stepIngredients);
        }

        return new IngredientDetector(list,map);
    }
    private static  ArrayList<IngredientUnitAmount> detectIngredients(String ingredientList){
        //TODO generate functionality

        //dummy
        ArrayList returnList = new ArrayList<IngredientUnitAmount>();
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

                returnList.add(ing);
            }
            catch(NumberFormatException nfe){
                //TODO have appropriate catch if ingredient does not contain a number that is parseable to double
            }
        }
        return returnList;
    }

    private static ArrayList<IngredientUnitAmount> detectIngredients(Step step){
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
