package com.aurora.souschef.SouschefProcessor.Task;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import SouschefProcessor.Recipe.RecipeInProgress;
import SouschefProcessor.Recipe.Step;
import SouschefProcessor.Task.IngredientDetector.IngredientDetectorStep;

public class IngredientDetectorStepTest {

    private static IngredientDetectorStep detector;
    private static RecipeInProgress recipe;
    private static ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(20);
    private static ArrayList<Step> steps;

    @BeforeClass
    public static void initialize() {
        detector = new IngredientDetectorStep();
        steps = new ArrayList<>();
        Step s1 = new Step("Put 500 gram sauce in the microwave");
        Step s2 = new Step("Put 500 gram spaghetti in boiling water");
        steps.add(s1);
        steps.add(s2);
        String originalText = "irrelevant";
        recipe = new RecipeInProgress(originalText);
        recipe.setSteps(steps);

    }

    @After
    public void wipeRecipeSteps() {
        for (Step s : steps) {
            s.setIngredientUnitAmountSet(null);
        }
    }


    @Test
    public void IngredientDetectorStep_doTask_setHasBeenSetForAllSteps() {
        detector.doTask(recipe, threadPoolExecutor);
        for (Step s : recipe.getSteps()) {
            assert (s.isIngredientDetected());
            assert (s.getIngredientUnitAmountSet() != null);
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
        IngredientUnitAmount spaghettiIngredient = new IngredientUnitAmount("spaghetti", "gram", 500);
        IngredientUnitAmount sauceIngredient = new IngredientUnitAmount("sauce", "gram", 500);
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
