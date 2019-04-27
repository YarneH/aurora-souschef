package com.aurora.souschefprocessor.recipe;

import com.aurora.souschefprocessor.task.RecipeInProgress;
import com.aurora.souschefprocessor.task.ingredientdetector.DetectIngredientsInStepTask;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class RecipeStepUnitTest {

    @Test
    public void RecipeStep_addTimer_PositionOfTimerBiggerThanLengthOfStepDescriptionThrowsException() {
        /**
         * The positon of a timer cannot be bigger than the length of the original text
         */

        // Arrange
        String originalText = "This is the original Text";
        RecipeStep step = new RecipeStep(originalText);
        // upper and lowerbound are irrelevant for this test
        int lowerbound = 40;
        int upperbound = 80;

        // 2 cases
        // case 1 beginindex small enough, endindex too big
        // Arrange
        int beginIndex = 0;
        int endIndex = originalText.length() + 1;
        boolean case1Thrown = false;
        Position pos = new Position(beginIndex, endIndex);
        RecipeTimer timer = new RecipeTimer(upperbound, lowerbound, pos);
        // Act
        try {
            step.add(timer);
        } catch (IllegalArgumentException iae) {
            case1Thrown = true;
        }
        // Assert
        assert (case1Thrown);

        // case 2 both too big
        // Arrange
        beginIndex = originalText.length();
        boolean case2Thrown = false;
        pos = new Position(beginIndex, endIndex);
        timer = new RecipeTimer(upperbound, lowerbound, pos);
        // Act
        try {
            step.add(timer);
        } catch (IllegalArgumentException iae) {
            case2Thrown = true;
        }
        // Assert
        assert (case2Thrown);
    }

    @Test
    public void RecipeStep_convertUnit_correctConversion() {

        // Add the ingredient to the recipe
        RecipeInProgress rip = new RecipeInProgress(null);
        EnumMap<Ingredient.PositionKeysForIngredients, Position> irrelevantPositions = new EnumMap<>(Ingredient.PositionKeysForIngredients.class);
        Position pos = new Position(0, 1);
        for (Ingredient.PositionKeysForIngredients key : Ingredient.PositionKeysForIngredients.values()) {
            irrelevantPositions.put(key, pos);
        }
        ListIngredient ingredient = new ListIngredient("olive oil", "cup", 1 / 2.0, "1/2 cup extra-virgin olive oil, divided", irrelevantPositions);
        rip.setIngredients(new ArrayList<>(Arrays.asList(ingredient)));

        // construct the step and add it to the recipe
        String originalDescription = "Heat 0.25 cup oil in a large deep-sided skillet over medium-high";

        RecipeStep step = new RecipeStep(originalDescription);
        rip.setRecipeSteps(new ArrayList<RecipeStep>(Arrays.asList(step)));

        // detect the ingredients in the step
        DetectIngredientsInStepTask task = new DetectIngredientsInStepTask(rip, 0);
        task.doTask();

        // convert the step
        step.convertUnit(true);
        assertNotEquals("The description is not as expected after conversion", "Heat 60 milliliter oil in a large deep-sided skillet over medium-high", step.getDescription());

        // convert back
        step.convertUnit(false);
        assertEquals("The description is not the same after converting twice", originalDescription, step.getDescription());
    }
}
