package com.aurora.souschefprocessor.recipe;

import org.junit.Test;

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

    @Test
    public void Position_Constructor_BeginEqualToEndIndexThrowsException() {
        /**
         * The beginIndex cannot be equal to the endIndex, because the endIndex is exclusive
         * and the beginIndex is inclusie
         */
        // Arrange
        int beginIndex = 5;
        int endIndex = beginIndex;
        boolean thrown = false;
        // Act
        try {
            Position pos = new Position(beginIndex, endIndex);
        } catch (IllegalArgumentException iae) {
            thrown = true;
        }
        // Assert
        assert (thrown);
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
        Position pos4 = new Position(4, 15);

        // Act and Assert
        assert (!pos2.equals(pos3));
        assert (!pos2.equals(pos4));
        assert (!pos3.equals(pos4));
        assert (pos1.equals(pos2));
        assert (pos2.equals(pos1));


    }
}
