package com.aurora.souschef.SouschefProcessor.Task;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import com.aurora.souschef.SouchefProcessor.Recipe.Ingredient;
import com.aurora.souschef.SouchefProcessor.Recipe.RecipeInProgress;
import com.aurora.souschef.SouchefProcessor.Task.IngredientDetector.DetectIngredientsInListTask;

public class DetectIngredientsInListTaskTest {

    private static RecipeInProgress recipe;
    private static DetectIngredientsInListTask detector;
    private static ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(20);
    private static String originalText;
    private static String ingredientList;

    @BeforeClass
    public static void initialize() {
        ingredientList = "500 gram spaghetti \n 500 gram sauce";
        originalText = "irrelevant";
        recipe = new RecipeInProgress(originalText);
        recipe.setIngredientsString(ingredientList);
        detector = new DetectIngredientsInListTask(recipe);
    }

    @After
    public void wipeRecipe() {
        recipe.setIngredients(null);
        recipe.setIngredientsString(ingredientList);
    }


    @Test
    public void DetectIngredientsInList_doTask_setHasBeenSet() {
        detector.doTask();
        assert (recipe.getIngredients() != null);
    }

    @Test
    public void DetectIngredientsInList_doTask_setHasCorrectSize() {
        detector.doTask();
        System.out.println(recipe.getIngredients());
        assert (recipe.getIngredients().size() == 2);
    }

    @Test
    public void DetectIngredientsInList_doTask_setHasCorrectElements() {
        detector.doTask();
        Ingredient spaghettiIngredient = new Ingredient("spaghetti", "gram", 500);
        Ingredient sauceIngredient = new Ingredient("sauce", "gram", 500);
        boolean spaghetti = recipe.getIngredients().contains(spaghettiIngredient);
        boolean sauce = recipe.getIngredients().contains(sauceIngredient);
        assert (spaghetti);
        assert (sauce);
    }

    @Test
    public void DetectIngredientsInList_doTask_ifNoIngredientsSetEmptyList() {
        recipe.setIngredientsString("");
        detector.doTask();
        assert (recipe.getIngredients() != null && recipe.getIngredients().size() == 0);

    }


}
