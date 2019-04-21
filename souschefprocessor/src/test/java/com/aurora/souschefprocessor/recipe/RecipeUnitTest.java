package com.aurora.souschefprocessor.recipe;

import com.aurora.souschefprocessor.task.RecipeInProgress;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecipeUnitTest {

    private static Position irrelevantPosition = new Position(0, 1);
    private static HashMap<Ingredient.PositionKeysForIngredients, Position> irrelevantPositions = new HashMap<>();
    private static RecipeInProgress rip;


    @BeforeClass
    public static void initialize() {

        for (Ingredient.PositionKeysForIngredients key : Ingredient.PositionKeysForIngredients.values()) {
            irrelevantPositions.put(key, irrelevantPosition);
        }
        rip = new RecipeInProgress("");
        RecipeStep step1 = new RecipeStep("Let the pasta boil for 10 minutes");
        RecipeTimer timer1 = new RecipeTimer(10 * 60, irrelevantPosition);

        step1.add(timer1);
        Ingredient ing1 = new Ingredient("pasta", "", 1, irrelevantPositions);
        step1.add(ing1);
        RecipeStep step2 = new RecipeStep("Put 500 g sauce in the microwave for 3 to 5 minutes");
        RecipeTimer timer2 = new RecipeTimer(3 * 60, 5 * 60, irrelevantPosition);
        step2.add(timer2);
        Ingredient ing2 = new Ingredient("sauce", "g", 500, irrelevantPositions);
        step2.add(ing2);

        List<RecipeStep> steps = new ArrayList<>();
        steps.add(step1);
        steps.add(step2);

        ListIngredient LI1 = new ListIngredient("pasta", "g", 500, "500 gram pasta", irrelevantPositions);
        ListIngredient LI2 = new ListIngredient("sauce", "g", 500, "500 gram pasta", irrelevantPositions);
        List<ListIngredient> ingredients = new ArrayList<>();
        ingredients.add(LI1);
        ingredients.add(LI2);
        rip.setIngredients(ingredients);
        rip.setDescription("This is a very simple recipe");

    }

    @Test
    public void Recipe_toJSON_afterConversionRecipeIsEqual() {
        // Arrange
        Recipe recipe = rip.convertToRecipe();

        // Act
        String json = recipe.toJSON();
        Recipe recipeAfterConversion =  Recipe.fromJson(json, Recipe.class);

        // Assert
        assert (recipe.equals(recipeAfterConversion));


    }
}
