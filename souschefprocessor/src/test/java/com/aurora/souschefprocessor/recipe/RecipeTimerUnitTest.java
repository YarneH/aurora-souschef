package com.aurora.souschefprocessor.recipe;

import org.junit.Test;

public class RecipeTimerUnitTest {
    private Position irrelevant = new Position(0, 1);

    @Test
    public void RecipeTimer_Constructor_LowerBoundNotBiggerThanUpperBound() throws IllegalArgumentException {
        /**
         * After construction of the lowerbound is never  bigger than the upperbound
         */

        //four cases
        //case 1 upperbound argument bigger than lowerbound
        // Arrange
        int upperbound = 20;
        int lowerbound = 10;
        // Act
        RecipeTimer recipeTimer = new RecipeTimer(upperbound, lowerbound, irrelevant);
        // Assert

        assert (recipeTimer.getLowerBound() <= recipeTimer.getUpperBound());

        //case 2 upperbound argument same as lowerbound
        // Arrange
        lowerbound = upperbound;
        // Act
        recipeTimer = new RecipeTimer(upperbound, lowerbound, irrelevant);
        // Assert
        assert (recipeTimer.getLowerBound() <= recipeTimer.getUpperBound());

        //case 3 only one argument
        // Act
        recipeTimer = new RecipeTimer(upperbound, irrelevant);
        // Assert
        assert (recipeTimer.getLowerBound() <= recipeTimer.getUpperBound());

        //case 4 upperbound argument smaller than lowerbound argument
        // Arrange
        lowerbound = 100;
        // Act
        recipeTimer = new RecipeTimer(upperbound, lowerbound, irrelevant);
        // Assert
        assert (recipeTimer.getLowerBound() <= recipeTimer.getUpperBound());


    }

    @Test
    public void RecipeTimer_Constructor_NegativeArgumentRaisesException() {
        /**
         * The bounds of a timer cannot be negative, constructing this throws an error
         */
        //case 1 upperbound negative
        // Arrange
        int upperbound = -10;
        int lowerbound = 10;
        boolean thrown = false;
        // Act
        try {
            RecipeTimer recipeTimer = new RecipeTimer(upperbound, lowerbound, irrelevant);
        } catch (IllegalArgumentException iae) {
            thrown = true;
        }
        // Assert
        assert (thrown);

        //case 2 lowerbound negative
        // Arrange
        upperbound = 10;
        lowerbound = -10;
        thrown = false;
        // Act
        try {
            RecipeTimer recipeTimer = new RecipeTimer(upperbound, lowerbound, irrelevant);
        } catch (IllegalArgumentException iae) {
            thrown = true;
        }
        // Assert
        assert (thrown);

        //case 3 time negative
        // Arrange
        int time = -7;
        thrown = false;
        // Act
        try {
            RecipeTimer recipeTimer = new RecipeTimer(time, irrelevant);
        } catch (IllegalArgumentException iae) {
            thrown = true;
        }
        // Assert
        assert (thrown);


    }


}
