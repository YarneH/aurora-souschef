package com.aurora.souschef.SouschefProcessor.Task;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import SouschefProcessor.Recipe.RecipeInProgress;
import SouschefProcessor.Recipe.RecipeStep;
import SouschefProcessor.Task.IngredientDetector.DetectIngredientsInStepTask;

public class DetectIngredientsInRecipeStepTaskTest {

    private static DetectIngredientsInStepTask detector0;
    private static DetectIngredientsInStepTask detector1;
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
    /*
    @Test
    public void IngredientDetectorList_doTask_setHasCorrectSize(){
        detector.doTask(recipe,threadPoolExecutor );
        assert(recipe.getIngredients().size() == 2);
    }

    @Test
    public void IngredientDetectorList_doTask_setHasCorrectElements(){
        detector.doTask(recipe,threadPoolExecutor );
        Ingredient spaghettiIngredient = new Ingredient("spaghetti", "gram", 500);
        Ingredient sauceIngredient = new Ingredient("sauce", "gram", 500);
        boolean spaghetti = recipe.getIngredients().contains(spaghettiIngredient);
        boolean sauce = recipe.getIngredients().contains(sauceIngredient);
        assert(spaghetti);
        assert(sauce);
    }

    public void IngredientDetectorList_doTask_ifNoIngredientsSetEmptyList(){
        recipe.setIngredientsString("");
        detector.doTask(recipe,threadPoolExecutor);
        assert(recipe.getIngredients() != null && recipe.getIngredients().size() == 0);
    }*/


}
