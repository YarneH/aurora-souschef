package com.aurora.souschef.SouschefProcessor.Task;

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

    @BeforeClass
    public static void initialize(){
        detector = new IngredientDetectorList();
        String text = "500 gram spaghetti \n 500 gram sauce";
        String originalText = "irrelevant";
        recipe = new RecipeInProgress(originalText);
        recipe.setIngredientsString(text);
    }

    @Test
    public void IngredientDetector_detectIngredients_setHasCorrectSize(){
        detector.doTask(recipe,threadPoolExecutor );
        assert(recipe.getIngredients().size() == 2);
    }

    @Test
    public void IngredientDetector_detectIngredients_setHasCorrectElements(){
        detector.doTask(recipe,threadPoolExecutor );
        IngredientUnitAmount spaghettiIngredient = new IngredientUnitAmount("spaghetti", "gram", 500);
        IngredientUnitAmount sauceIngredient = new IngredientUnitAmount("sauce", "gram", 500);
        boolean spaghetti = recipe.getIngredients().contains(spaghettiIngredient);
        boolean sauce = recipe.getIngredients().contains(sauceIngredient);
        assert(spaghetti);
        assert(sauce);
    }


}
