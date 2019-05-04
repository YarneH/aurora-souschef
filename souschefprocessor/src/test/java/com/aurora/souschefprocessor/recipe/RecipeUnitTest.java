package com.aurora.souschefprocessor.recipe;

import com.aurora.auroralib.ExtractedText;
import com.aurora.souschefprocessor.task.RecipeInProgress;
import com.aurora.souschefprocessor.task.RecipeStepInProgress;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class RecipeUnitTest {

    private static Position irrelevantPosition = new Position(0, 1);
    private static HashMap<Ingredient.PositionKeysForIngredients, Position> irrelevantPositions = new HashMap<>();
    private static RecipeInProgress rip;
    private static ExtractedText emptyExtractedText = new ExtractedText("", null);


    @BeforeClass
    public static void initialize() {

        for (Ingredient.PositionKeysForIngredients key : Ingredient.PositionKeysForIngredients.values()) {
            irrelevantPositions.put(key, irrelevantPosition);
        }
        rip = new RecipeInProgress(emptyExtractedText);
        RecipeStepInProgress step1 = new RecipeStepInProgress("Let the pasta boil for 10 minutes");
        RecipeTimer timer1 = new RecipeTimer(10 * 60, irrelevantPosition);

        step1.add(timer1);
        Position qpos1 = new Position(0, step1.getDescription().length());
        Position upos1 = new Position(0, step1.getDescription().length());
        Position npos1 = new Position(step1.getDescription().indexOf("pasta"),step1.getDescription().indexOf("pasta") + 5);
        Map<Ingredient.PositionKeysForIngredients, Position> map1 =new HashMap<Ingredient.PositionKeysForIngredients, Position>() {{
            put(Ingredient.PositionKeysForIngredients.QUANTITY, qpos1);
            put(Ingredient.PositionKeysForIngredients.UNIT, upos1);
            put(Ingredient.PositionKeysForIngredients.NAME, npos1);
        }};
        Ingredient ing1 = new Ingredient("pasta", "", 1, map1);
        step1.add(ing1);
        RecipeStepInProgress step2 = new RecipeStepInProgress("Put 500 gram sauce in the microwave for 3 to 5 minutes");
        RecipeTimer timer2 = new RecipeTimer(3 * 60, 5 * 60, irrelevantPosition);
        step2.add(timer2);

        String description2 = step2.getDescription();
        int qbegin = description2.indexOf("500");
;        Position qpos2 = new Position(qbegin,qbegin + 3);
int ubegin = description2.indexOf("gram");
        Position upos2 = new Position(ubegin,ubegin+4);
        int nbegin = description2.indexOf("sauce");
        Position npos2 = new Position(nbegin, nbegin + 5);
        Map<Ingredient.PositionKeysForIngredients, Position> map2 =new HashMap<Ingredient.PositionKeysForIngredients, Position>() {{
            put(Ingredient.PositionKeysForIngredients.QUANTITY, qpos2);
            put(Ingredient.PositionKeysForIngredients.UNIT, upos2);
            put(Ingredient.PositionKeysForIngredients.NAME, npos2);
        }};
        Ingredient ing2 = new Ingredient("sauce", "gram", 500, map2);
        step2.add(ing2);

        List<RecipeStepInProgress> steps = new ArrayList<>(Arrays.asList(step1, step2));
        rip.setStepsInProgress(steps);

        Map<Ingredient.PositionKeysForIngredients, Position> listMap =new HashMap<Ingredient.PositionKeysForIngredients, Position>() {{
            put(Ingredient.PositionKeysForIngredients.QUANTITY, new Position(0,3));
            put(Ingredient.PositionKeysForIngredients.UNIT, new Position(4,8));
            put(Ingredient.PositionKeysForIngredients.NAME, new Position(9,14));
        }};
        ListIngredient LI1 = new ListIngredient("pasta", "gram", 500, "500 gram pasta", irrelevantPositions);
        ListIngredient LI2 = new ListIngredient("sauce", "gram", 500, "500 gram sauce", irrelevantPositions);
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
        Recipe recipeAfterConversion = Recipe.fromJson(json, Recipe.class);

        // Assert
        assert (recipe.equals(recipeAfterConversion));


    }

    @Test
    public void Recipe_convertUnit_afterConvertingTwiceRecipeIsEqual(){
        /*
        Converting the units of a recipe should be reversible, so converting twice should yield the
        original recipe
         */

        // get the original
        Recipe recipe = rip.convertToRecipe();

        Recipe unitConvertingRecipe = rip.convertToRecipe();

        // convert the converting recipe twice
        unitConvertingRecipe.convertUnit(false);
        unitConvertingRecipe.convertUnit(true);

        // Assert after converting twice it is back the same recipe
        assertEquals("The recipe is not the same after converting twice", recipe, unitConvertingRecipe);



    }

}
