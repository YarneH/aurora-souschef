package SouschefProcessor.Task.IngredientDetector;

import java.util.ArrayList;

import SouschefProcessor.Recipe.IngredientUnitAmount;
import SouschefProcessor.Recipe.Recipe;
import SouschefProcessor.Recipe.RecipeInProgress;
import SouschefProcessor.Recipe.Step;
import SouschefProcessor.Task.Task;

/**
 * Detects the ingredients in the list of ingredients
 */
public class IngredientDetectorStep implements Task {

     private Step step;

    public void doTask(RecipeInProgress recipe){
        //TODO
        return;
    }

    public  ArrayList<IngredientUnitAmount> detectIngredients(Step step){
        //TODO generate functionality

        //dummy
        ArrayList list = new ArrayList<IngredientUnitAmount>();
        if(step.getDescription().contains("sauce")){
            list.add(new IngredientUnitAmount("sauce", "gram", 500));
        }
        else{
            list.add(new IngredientUnitAmount("spaghetti", "gram", 500));
        }
        return list;
    }
}
