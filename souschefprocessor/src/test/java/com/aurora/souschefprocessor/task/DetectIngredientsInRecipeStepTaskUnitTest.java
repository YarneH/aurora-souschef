package com.aurora.souschefprocessor.task;

import com.aurora.souschefprocessor.recipe.Ingredient;
import com.aurora.souschefprocessor.recipe.ListIngredient;
import com.aurora.souschefprocessor.recipe.Position;
import com.aurora.souschefprocessor.recipe.RecipeStep;
import com.aurora.souschefprocessor.task.ingredientdetector.DetectIngredientsInStepTask;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DetectIngredientsInRecipeStepTaskUnitTest {

    private static DetectIngredientsInStepTask detector0;
    private static DetectIngredientsInStepTask detector1;
    private static RecipeInProgress recipe;
    private static ArrayList<RecipeStep> recipeSteps;
    private static HashMap<Ingredient.PositionKey, Position> irrelevantPositions = new HashMap<>();

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
        List<ListIngredient> set = new ArrayList<>();
        recipe.setIngredients(set);

        detector0 = new DetectIngredientsInStepTask(recipe, stepIndex0);
        detector1 = new DetectIngredientsInStepTask(recipe, stepIndex1);

        Position pos = new Position(0, 1);
        for (Ingredient.PositionKey key : Ingredient.PositionKey.values()) {
            irrelevantPositions.put(key, pos);
        }
    }

    @After
    public void wipeRecipeSteps() {
        for (RecipeStep s : recipeSteps) {
            s.setIngredients(null);
        }
    }


    @Test
    public void IngredientDetectorStep_doTask_setHasBeenSetForAllSteps() {
        /**
         * After doing the task the ingredients field cannot be null
         */
        // Act
        detector0.doTask();
        detector1.doTask();
        // Assert
        for (RecipeStep s : recipe.getRecipeSteps()) {
            assert (s.isIngredientDetected());
            assert (s.getIngredients() != null);
        }
    }

    @Test
    public void DetectIngredientsInStep_doTask_stepsHaveCorrectElements() {
        /**
         * After doing the task, the correct ingredients are detected
         */
        // Arrange
        Ingredient spaghettiIngredient = new Ingredient("spaghetti", "gram", 500, irrelevantPositions);
        Ingredient sauceIngredient = new Ingredient("sauce", "gram", 500, irrelevantPositions);
        // Act
        detector0.doTask();
        detector1.doTask();
        // Assert
        boolean spaghetti = recipe.getRecipeSteps().get(0).getIngredients().contains(sauceIngredient);
        boolean sauce = recipe.getRecipeSteps().get(1).getIngredients().contains(spaghettiIngredient);
        assert (spaghetti);
        assert (sauce);
    }


}
