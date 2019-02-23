package SouschefProcessor.Task.IngredientDetector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;

import SouschefProcessor.Recipe.Ingredient;
import SouschefProcessor.Recipe.RecipeInProgress;
import SouschefProcessor.Recipe.Step;
import SouschefProcessor.Task.ProcessingTask;

/**
 * Detects the ingredients in the list of ingredients
 */
public class DetectIngredientsInStepsTask implements ProcessingTask {


    /**
     * Detects the ingredients for each step
     *
     * @param recipeInProgress     The recipe, for which ingredients have to be detected in each step
     * @param threadPoolExecutor The threadpool on which to execute threads within this task
     */
    public void doTask(RecipeInProgress recipeInProgress, ThreadPoolExecutor threadPoolExecutor) {
        //TODO, fallback if no steps present
        ArrayList<Step> steps = recipeInProgress.getSteps();
        HashSet<Ingredient> ingredientSetRecipe = recipeInProgress.getIngredients();

        CountDownLatch latch = new CountDownLatch(steps.size());

        for (Step s : steps) {
            DetectIngredientsInStepThread thread = new DetectIngredientsInStepThread(s, ingredientSetRecipe, latch);
            threadPoolExecutor.execute(thread);
        }
        waitForThreads(latch);

    }

    private void waitForThreads(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Detects the set of ingredients in a step. It also checks if this corresponds with the ingredients of the
     * recipe.
     *
     * @param step                The step on which to detect the ingredients
     * @param ingredientSetRecipe The set of ingredients contained in the recipe of which the step is a part
     * @return A set of Ingredient objects that represent the ingredients contained in the step
     */
    public HashSet<Ingredient> detectIngredients(Step step, HashSet<Ingredient> ingredientSetRecipe) {
        //TODO generate functionality

        //dummy
        HashSet<Ingredient> set = new HashSet<>();
        if (step.getDescription().contains("sauce")) {
            set.add(new Ingredient("sauce", "gram", 500));
        } else {
            set.add(new Ingredient("spaghetti", "gram", 500));
        }
        return set;
    }


    /**
     * A thread that does the detecting of ingredients of a step
     */
    private class DetectIngredientsInStepThread extends Thread {

        private Step step;
        private HashSet<Ingredient> ingredientSetRecipe;
        private CountDownLatch latch;


        public DetectIngredientsInStepThread(Step step, HashSet<Ingredient> ingredientSetRecipe, CountDownLatch latch) {
            this.step = step;
            this.ingredientSetRecipe = ingredientSetRecipe;
            this.latch = latch;
        }

        /**
         * Detects the ingredients in the step and sets the ingredientUnitAmountSet field of the step
         */
        public void run() {
            HashSet<Ingredient> iuaSet = detectIngredients(step, ingredientSetRecipe);
            step.setIngredientUnitAmountSet(iuaSet);
            latch.countDown();

        }
    }
}
