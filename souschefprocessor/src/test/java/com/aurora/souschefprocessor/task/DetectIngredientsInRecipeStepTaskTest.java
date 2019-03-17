package com.aurora.souschefprocessor.task;

import com.aurora.souschefprocessor.recipe.Ingredient;
import com.aurora.souschefprocessor.recipe.RecipeStep;
import com.aurora.souschefprocessor.task.RecipeInProgress;
import com.aurora.souschefprocessor.task.ingredientdetector.DetectIngredientsInListTask;
import com.aurora.souschefprocessor.task.ingredientdetector.DetectIngredientsInStepTask;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class DetectIngredientsInRecipeStepTaskTest {

    private static DetectIngredientsInStepTask detector0;
    private static DetectIngredientsInStepTask detector1;
    private static DetectIngredientsInStepTask detector2;
    private static DetectIngredientsInStepTask detector3;
    private static RecipeInProgress recipe;
    private static ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(20);
    private static ArrayList<RecipeStep> recipeSteps;

    @BeforeClass
    public static void initialize() {
        recipeSteps = new ArrayList<>();
        RecipeStep s1 = new RecipeStep("Put 500 gram sauce in the microwave");
        RecipeStep s2 = new RecipeStep("Put 500 gram spaghetti in boiling water");
        recipeSteps.add(s1);
        recipeSteps.add(s2);
        int stepIndex0 = 0;
        int stepIndex1 = 1;
        String originalText = "irrelevant";
        recipe = new RecipeInProgress(originalText);
        recipe.setRecipeSteps(recipeSteps);
        List<Ingredient> set = new ArrayList<>();
        set.add(new Ingredient("spaghetti", "gram", 500, "irrelevant"));
        set.add(new Ingredient("sauce", "gram", 500, "irrelevant"));
        recipe.setIngredients(set);

        detector0 = new DetectIngredientsInStepTask(recipe, stepIndex0);
        detector1 = new DetectIngredientsInStepTask(recipe, stepIndex1);
    }

    @After
    public void wipeRecipeSteps() {
        for (RecipeStep s : recipeSteps) {
            s.setIngredients(null);
        }
    }

    @Test
    public void IngredientDetectorStep_doTask_setHasBeenSetForAllSteps() {
        detector0.doTask();
        detector1.doTask();

        for (RecipeStep s : recipe.getRecipeSteps()) {
            assert (s.isIngredientDetected());
            assert (s.getIngredients() != null);
        }
    }

    @Test
    public void DetectIngredientsInStep_doTask_stepsHaveCorrectElements() {

        detector0.doTask();
        detector1.doTask();
        Ingredient spaghettiIngredient = new Ingredient("spaghetti", "gram", 500, "irrelevant");
        Ingredient sauceIngredient = new Ingredient("sauce", "gram", 500, "irrelevant");
        boolean spaghetti = recipe.getRecipeSteps().get(0).getIngredients().contains(sauceIngredient);
        boolean sauce = recipe.getRecipeSteps().get(1).getIngredients().contains(spaghettiIngredient);
        assert (spaghetti);
        assert (sauce);
    }


}
