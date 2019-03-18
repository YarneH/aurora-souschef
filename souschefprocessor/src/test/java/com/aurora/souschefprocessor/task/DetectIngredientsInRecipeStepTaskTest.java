package com.aurora.souschefprocessor.task;

import com.aurora.souschefprocessor.recipe.Ingredient;
import com.aurora.souschefprocessor.recipe.ListIngredient;
import com.aurora.souschefprocessor.recipe.Position;
import com.aurora.souschefprocessor.recipe.RecipeStep;
import com.aurora.souschefprocessor.task.RecipeInProgress;
import com.aurora.souschefprocessor.task.ingredientdetector.DetectIngredientsInStepTask;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
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
    private static HashMap<Ingredient.PositionKey, Position> irrelevantPositions;

    @BeforeClass
    public static void initialize() {
        // Initialize recipe in progress
        String originalText = "irrelevant";
        recipe = new RecipeInProgress(originalText);

        // Initialize positions with dummy values
        irrelevantPositions = new HashMap<>();
        Position pos = new Position(0, 1);
        for (Ingredient.PositionKey key : Ingredient.PositionKey.values()) {
            irrelevantPositions.put(key, pos);
        }

        // Initialize ingredient list with dummy values
        List<ListIngredient> set = new ArrayList<>();
        set.add(new ListIngredient("spaghetti", "gram",500, "irrelevant", irrelevantPositions));
        set.add(new ListIngredient("sauce", "gram", 500, "irrelevant", irrelevantPositions));
        recipe.setIngredients(set);

        // Initialize recipe steps with dummy values
        recipeSteps = new ArrayList<>();
        RecipeStep s1 = new RecipeStep("Put 500 gram sauce in the microwave");
        RecipeStep s2 = new RecipeStep("Put 500 gram spaghetti in boiling water");
        recipeSteps.add(s1);
        recipeSteps.add(s2);
        int stepIndex0 = 0;
        int stepIndex1 = 1;
        recipe.setRecipeSteps(recipeSteps);

        // Initialize detectors
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
        Ingredient sauceIngredient = new Ingredient("sauce", "gram", 500,  irrelevantPositions);
        Ingredient spaghettiIngredient = new Ingredient("spaghetti", "gram", 500,  irrelevantPositions);
        // For now, only names can be detected
        // boolean spaghetti = recipe.getRecipeSteps().get(0).getIngredients().contains(sauceIngredient);
        // boolean sauce = recipe.getRecipeSteps().get(1).getIngredients().contains(spaghettiIngredient);
        boolean spaghetti = false;
        boolean sauce = false;
        for(Ingredient ingr : recipe.getRecipeSteps().get(0).getIngredients()){
            if(ingr.getName().equals(sauceIngredient.getName())){
                sauce = true;
            }
        }
        for(Ingredient ingr : recipe.getRecipeSteps().get(1).getIngredients()){
            if(ingr.getName().equals(spaghettiIngredient.getName())){
                spaghetti = true;
            }
        }
        assert (spaghetti);
        assert (sauce);
    }


}
