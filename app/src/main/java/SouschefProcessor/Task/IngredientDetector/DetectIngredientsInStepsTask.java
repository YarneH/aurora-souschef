package SouschefProcessor.Task.IngredientDetector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;

import SouschefProcessor.Recipe.Ingredient;
import SouschefProcessor.Recipe.RecipeInProgress;
import SouschefProcessor.Recipe.RecipeStep;
import SouschefProcessor.Task.ProcessingTask;

/**
 * Detects the ingredients in the list of ingredients
 */
public class DetectIngredientsInStepsTask implements ProcessingTask {


    /**
     * Detects the ingredients for each recipeStep
     *
     * @param recipeInProgress     The recipe, for which ingredients have to be detected in each recipeStep
     * @param threadPoolExecutor The threadpool on which to execute threads within this task
     */
    public void doTask(RecipeInProgress recipeInProgress, ThreadPoolExecutor threadPoolExecutor) {
        //TODO, fallback if no recipeSteps present
        ArrayList<RecipeStep> recipeSteps = recipeInProgress.getRecipeSteps();
        HashSet<Ingredient> ingredientSetRecipe = recipeInProgress.getIngredients();

        CountDownLatch latch = new CountDownLatch(recipeSteps.size());

        for (RecipeStep s : recipeSteps) {
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
     * Detects the set of ingredients in a recipeStep. It also checks if this corresponds with the ingredients of the
     * recipe.
     *
     * @param recipeStep                The recipeStep on which to detect the ingredients
     * @param ingredientSetRecipe The set of ingredients contained in the recipe of which the recipeStep is a part
     * @return A set of Ingredient objects that represent the ingredients contained in the recipeStep
     */
    public HashSet<Ingredient> detectIngredients(RecipeStep recipeStep, HashSet<Ingredient> ingredientSetRecipe) {
        //TODO generate functionality

        //dummy
        HashSet<Ingredient> set = new HashSet<>();
        if (recipeStep.getDescription().contains("sauce")) {
            set.add(new Ingredient("sauce", "gram", 500));
        } else {
            set.add(new Ingredient("spaghetti", "gram", 500));
        }
        return set;
    }


    /**
     * A thread that does the detecting of ingredients of a recipeStep
     */
    private class DetectIngredientsInStepThread extends Thread {

        private RecipeStep recipeStep;
        private HashSet<Ingredient> ingredientSetRecipe;
        private CountDownLatch latch;


        public DetectIngredientsInStepThread(RecipeStep recipeStep, HashSet<Ingredient> ingredientSetRecipe, CountDownLatch latch) {
            this.recipeStep = recipeStep;
            this.ingredientSetRecipe = ingredientSetRecipe;
            this.latch = latch;
        }

        /**
         * Detects the ingredients in the recipeStep and sets the ingredientUnitAmountSet field of the recipeStep
         */
        public void run() {
            HashSet<Ingredient> iuaSet = detectIngredients(recipeStep, ingredientSetRecipe);
            recipeStep.setIngredients(iuaSet);
            latch.countDown();

        }
    }
}
