package SouschefProcessor.Task.IngredientDetector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ThreadPoolExecutor;

import SouschefProcessor.Recipe.IngredientUnitAmount;
import SouschefProcessor.Recipe.RecipeInProgress;
import SouschefProcessor.Recipe.Step;
import SouschefProcessor.Task.Task;

/**
 * Detects the ingredients in the list of ingredients
 */
public class IngredientDetectorStep implements Task {

    /**
     * Detects the ingredients for each step
     * @param recipe The recipe, for which ingredients have to be detected in each step
     * @param threadPool The threadpool on which to execute threads within this task
     */
    public void doTask(RecipeInProgress recipe, ThreadPoolExecutor threadPool){
        //TODO, fallback if no steps present
        ArrayList<Step> steps = recipe.getSteps();
        HashSet<IngredientUnitAmount> ingredientSetRecipe = recipe.getIngredients();

        ArrayList<IngredientDetectorStepThread> threads = new ArrayList<>();

        for(Step s : steps){
           IngredientDetectorStepThread thread = new IngredientDetectorStepThread(s,ingredientSetRecipe);
           threadPool.execute(thread);
           threads.add(thread);
        }

        for(Thread t : threads){
            try {
               t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Detects the set of ingredients in a step. It also checks if this corresponds with the ingredients of the
     * recipe.
     * @param step The step on which to detect the ingredients
     * @param ingredientSetRecipe The set of ingredients contained in the recipe of which the step is a part
     * @return A set of IngredientUnitAmount objects that represent the ingredients contained in the step
     */
    public HashSet<IngredientUnitAmount> detectIngredients(Step step, HashSet<IngredientUnitAmount> ingredientSetRecipe){
        //TODO generate functionality

        //dummy
        HashSet<IngredientUnitAmount> set = new HashSet<>();
        if(step.getDescription().contains("sauce")){
            set.add(new IngredientUnitAmount("sauce", "gram", 500));
        }
        else{
            set.add(new IngredientUnitAmount("spaghetti", "gram", 500));
        }
        return set;
    }

    /**
     * A thread that does the detecting of ingredients of a step
     */
    private class IngredientDetectorStepThread extends Thread{

        private Step step;
        private HashSet<IngredientUnitAmount> ingredientSetRecipe;

        public IngredientDetectorStepThread(Step step, HashSet<IngredientUnitAmount> ingredientSetRecipe ){
            this.step = step;
            this.ingredientSetRecipe = ingredientSetRecipe;
        }

        /**
         * Detects the ingredients in the step and sets the ingredientUnitAmountSet field of the step
         */
        public void run(){
            HashSet<IngredientUnitAmount> iuaSet = detectIngredients(step, ingredientSetRecipe);
            step.setIngredientUnitAmountSet(iuaSet);
        }
    }
}
