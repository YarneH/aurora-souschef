package com.aurora.souschefprocessor.recipe;

import org.junit.Test;

public class RecipeStepTest {

    @Test
    public void RecipeTimer_PositionOfTimerBiggerThanLengthOfStepDescriptionThrowsException() {

        String originalText = "This is the original Text";
        RecipeStep step = new RecipeStep(originalText);
        // upper and lowerbound are irrelevant for this test
        int lowerbound = 40;
        int upperbound = 80;

        // 2 cases
        // case 1 beginindex small enough, endindex too big
        int beginIndex = 0;
        int endIndex = originalText.length() + 1;
        boolean case1Thrown = false;
        try {
            Position pos = new Position(beginIndex, endIndex);
            RecipeTimer timer = new RecipeTimer(upperbound, lowerbound, pos);
            step.add(timer);


        } catch (IllegalArgumentException iae) {
            case1Thrown = true;
        }
        assert (case1Thrown);

        // case 2 both too big
        beginIndex = originalText.length();
        boolean case2Thrown = false;
        try {
            Position pos = new Position(beginIndex, endIndex);
            RecipeTimer timer = new RecipeTimer(upperbound, lowerbound, pos);
            step.add(timer);
        } catch (IllegalArgumentException iae) {
            case2Thrown = true;
        }
        assert (case2Thrown);
    }
}
