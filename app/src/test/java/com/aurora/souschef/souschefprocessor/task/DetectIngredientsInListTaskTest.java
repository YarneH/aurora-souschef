package com.aurora.souschef.SouschefProcessor.task;

import com.aurora.souschef.recipe.Ingredient;
import com.aurora.souschef.souschefprocessor.task.RecipeInProgress;
import com.aurora.souschef.souschefprocessor.task.ingredientdetector.DetectIngredientsInListTask;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class DetectIngredientsInListTaskTest {

    private static RecipeInProgress recipe;
    private static DetectIngredientsInListTask detector;
    private static ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(20);
    private static String originalText;
    private static String ingredientList;

    @BeforeClass
    public static void initialize() {
        ingredientList = "500 ounces spaghetti \n500 ounces sauce \n1 1/2 pounds minced meat\n 1 clove garlic\n twenty basil leaves";
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
        assert (recipe.getIngredients().size() == 5);
    }

    @Test
    public void DetectIngredientsInList_doTask_setHasCorrectElements() {
        detector.doTask();
        Ingredient spaghettiIngredient = new Ingredient("spaghetti", "ounces", 500);
        Ingredient sauceIngredient = new Ingredient("sauce", "ounces", 500);
        Ingredient meatIngredient = new Ingredient("minced meat", "pounds", 1.5);
        Ingredient garlicIngredient = new Ingredient("garlic", "clove", 1.0);
        Ingredient basilIngredient = new Ingredient("basil leaves", "", 20.0);
        boolean spaghetti = recipe.getIngredients().contains(spaghettiIngredient);
        boolean sauce = recipe.getIngredients().contains(sauceIngredient);
        boolean meat = recipe.getIngredients().contains(meatIngredient);
        boolean garlic = recipe.getIngredients().contains(garlicIngredient);
        boolean basil = recipe.getIngredients().contains(basilIngredient);
        assert (spaghetti);
        assert (sauce);
        assert (meat);
        assert(garlic);
        assert(basil);
    }

    @Test
    public void DetectIngredientsInList_doTask_ifNoIngredientsSetEmptyList() {
        recipe.setIngredientsString("");
        detector.doTask();
        assert (recipe.getIngredients() != null && recipe.getIngredients().size() == 0);

    }

    @Test
    public void DetectIngredientsInList_doTask_randomInputStringDoesNotThrowError(){
        boolean thrown = false;

        recipe.setIngredientsString("20 pound xxx \n jqkfdksqfjkd// \n 45055 450 47d dkjq4 kdj  4 dqfd/n \n kjfqkf 450 ounce lfqj \n 20 pound xxx\"");
        try{
            detector.doTask();
        }catch(Exception e){
            thrown = true;
        }
        assert(!thrown);



    }


}
