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
public class DetectIngredientsInStepTask extends ProcessingTask {
    int stepIndex;

    public DetectIngredientsInStepTask(RecipeInProgress recipeInProgress, int stepIndex) {
        super(recipeInProgress);
        this.stepIndex = stepIndex;
    }

    /**
     * Detects the ingredients for each recipeStep
    */
    public void doTask() {
        RecipeStep recipeStep = recipeInProgress.getRecipeSteps().get(stepIndex);
        HashSet<Ingredient> ingredientSetRecipe = recipeInProgress.getIngredients();
        HashSet<Ingredient> iuaSet = detectIngredients(recipeStep, ingredientSetRecipe);
        recipeStep.setIngredients(iuaSet);
    }

    /**
     * Detects the set of ingredients in a recipeStep. It also checks if this corresponds with the ingredients of the
     * recipe.
     *
     * @param recipeStep                The recipeStep on which to detect the ingredients
     * @param ingredientSetRecipe The set of ingredients contained in the recipe of which the recipeStep is a part
     * @return A set of Ingredient objects that represent the ingredients contained in the recipeStep
     */
    private HashSet<Ingredient> detectIngredients(RecipeStep recipeStep, HashSet<Ingredient> ingredientSetRecipe) {
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


}
