package com.aurora.souschef.SouschefProcessor.Task;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import SouschefProcessor.Recipe.IngredientUnitAmount;
import SouschefProcessor.Recipe.RecipeInProgress;
import SouschefProcessor.Task.IngredientDetector.IngredientDetectorList;

public class IngredientDetectorListTest {

    private static IngredientDetectorList detector;
    private static RecipeInProgress recipe;
    private static ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(20);
    private static String originalText;
    private static String ingredientList;

    @BeforeClass
    public static void initialize() {
        detector = new IngredientDetectorList();
        ingredientList = "500 gram spaghetti \n 500 gram sauce";
        originalText = "irrelevant";
        recipe = new RecipeInProgress(originalText);
        recipe.setIngredientsString(ingredientList);
    }

    @After
    public void wipeRecipe() {
        recipe.setIngredients(null);
    }


    @Test
    public void IngredientDetectorList_doTask_setHasBeenSet() {
        detector.doTask(recipe, threadPoolExecutor);
        assert (recipe.getIngredients() != null);
    }

    @Test
    public void IngredientDetectorList_doTask_setHasCorrectSize() {
        detector.doTask(recipe, threadPoolExecutor);
        assert (recipe.getIngredients().size() == 2);
    }

    @Test
    public void IngredientDetectorList_doTask_setHasCorrectElements() {
        detector.doTask(recipe, threadPoolExecutor);
        IngredientUnitAmount spaghettiIngredient = new IngredientUnitAmount("spaghetti", "gram", 500);
        IngredientUnitAmount sauceIngredient = new IngredientUnitAmount("sauce", "gram", 500);
        boolean spaghetti = recipe.getIngredients().contains(spaghettiIngredient);
        boolean sauce = recipe.getIngredients().contains(sauceIngredient);
        assert (spaghetti);
        assert (sauce);
    }

    public void IngredientDetectorList_doTask_ifNoIngredientsSetEmptyList() {
        recipe.setIngredientsString("");
        detector.doTask(recipe, threadPoolExecutor);
        assert (recipe.getIngredients() != null && recipe.getIngredients().size() == 0);
    }


}
