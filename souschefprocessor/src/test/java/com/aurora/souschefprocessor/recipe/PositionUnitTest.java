package com.aurora.souschefprocessor.recipe;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class PositionUnitTest {

    @Test
    public void Positon_Constructor_BeginSmallerThanEnd() {
        /**
         * After constructing a Postion the beginindex should always be smaller than the endindex
         */
        // two cases
        // case 1: beginIndex < endIndex
        // Arrange
        int beginIndex = 1;
        int endIndex = 2;
        // Act
        Position case1 = new Position(beginIndex, endIndex);
        // Assert
        assert (case1.getBeginIndex() < case1.getEndIndex());

        // case 2: beginIndex > endIndex
        // Arrange
        endIndex = 1;
        beginIndex = 2;
        // Act
        Position case2 = new Position(beginIndex, endIndex);
        // Assert
        assert (case2.getBeginIndex() < case1.getEndIndex());
    }

    @Test
    public void Position_Constructor_NegativeArgumentThrowsException() {
        /**
         * The positions can never be negative, constructing throws an error
         */
        // three cases:
        // case 1: begin negative
        // Arrange
        int beginIndex = -1;
        int endIndex = 1;
        boolean thrown = false;
        // Act
        try {
            Position case1 = new Position(beginIndex, endIndex);
        } catch (IllegalArgumentException iae) {
            thrown = true;
        }
        // Assert
        assert (thrown);

        // case 2: both  negative
        // Arrange
        endIndex = -1;
        thrown = false;
        // Act
        try {
            Position case2 = new Position(beginIndex, endIndex);
        } catch (IllegalArgumentException iae) {
            thrown = true;
        }
        // Assert
        assert (thrown);

        // case 3: end negative
        // Arrange
        beginIndex = 1;
        thrown = false;
        // Act
        try {
            Position case3 = new Position(beginIndex, endIndex);
        } catch (IllegalArgumentException iae) {
            thrown = true;
        }
        // Assert
        assert (thrown);


    }

    @Test(expected = IllegalArgumentException.class)
    public void Position_Constructor_BeginEqualToEndIndexThrowsException() {
        /**
         * The beginIndex cannot be equal to the endIndex, because the endIndex is exclusive
         * and the beginIndex is inclusie
         */
        // Arrange
        int beginIndex = 5;
        int endIndex = beginIndex;
        Position pos = new Position(beginIndex, endIndex);

    }

    @Test
    public void Position_equals_BehavesAsExpected() {
        /**
         * Positions are equal if and only if the beginIndices and endIndices are equal
         */
        // Arrange
        Position pos1 = new Position(6, 15);
        Position pos2 = new Position(6, 15);
        Position pos3 = new Position(6, 8);
        Position pos4 = new Position(7, 15);

        // Act and Assert
        assertEquals("Same inputs do not render equality", pos1, pos2);
        assertEquals("Equality is not commutative", pos2, pos1);
        assertNotEquals("Different upperbound gives equality", pos1, pos3);
        assertNotEquals("Differnt lowerbound gives equality", pos4, pos1);


    }
}
