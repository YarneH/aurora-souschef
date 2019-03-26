package com.aurora.souschefprocessor.recipe;

import org.junit.Test;

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
}
