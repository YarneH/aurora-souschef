package com.aurora.souschef.SouschefProcessor.task;

import android.content.Context;

import com.aurora.souschef.recipe.Ingredient;
import com.aurora.souschef.souschefprocessor.task.RecipeInProgress;
import com.aurora.souschef.souschefprocessor.task.ingredientdetector.DetectIngredientsInListTask;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import androidx.test.InstrumentationRegistry;

public class DetectIngredientsInListTaskTest {

    private static RecipeInProgress recipe;
    private static DetectIngredientsInListTask detector;
    private static ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(20);
    private static String originalText;
    private static String ingredientList;

    @BeforeClass
    public static void initialize() {
        ingredientList = "500 ounces spaghetti \n 500 ounces sauce";
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
        Ingredient spaghettiIngredient = new Ingredient("spaghetti", "ounces", 500);
        Ingredient sauceIngredient = new Ingredient("sauce", "ounces", 500);
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
