package com.aurora.souschef.SouschefProcessor.Task;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import SouschefProcessor.Recipe.RecipeInProgress;
import SouschefProcessor.Recipe.RecipeStep;
import SouschefProcessor.Task.IngredientDetector.DetectIngredientsInStepsTask;

public class DetectIngredientsInRecipeStepTaskTest {

    private static DetectIngredientsInStepsTask detector;
    private static RecipeInProgress recipe;
    private static ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(20);
    private static ArrayList<RecipeStep> recipeSteps;

    @BeforeClass
    public static void initialize() {
        detector = new DetectIngredientsInStepsTask();
        recipeSteps = new ArrayList<>();
        RecipeStep s1 = new RecipeStep("Put 500 gram sauce in the microwave");
        RecipeStep s2 = new RecipeStep("Put 500 gram spaghetti in boiling water");
        recipeSteps.add(s1);
        recipeSteps.add(s2);
        String originalText = "irrelevant";
        recipe = new RecipeInProgress(originalText);
        recipe.setRecipeSteps(recipeSteps);

    }

    @After
    public void wipeRecipeSteps() {
        for (RecipeStep s : recipeSteps) {
            s.setIngredients(null);
        }
    }


    @Test
    public void IngredientDetectorStep_doTask_setHasBeenSetForAllSteps() {
        detector.doTask(recipe, threadPoolExecutor);
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
