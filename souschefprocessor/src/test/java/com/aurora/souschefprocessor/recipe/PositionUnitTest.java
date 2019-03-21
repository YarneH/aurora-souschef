package com.aurora.souschefprocessor.recipe;

import org.junit.Test;

public class PositionUnitTest {

    @Test
    public void Positon_Constructor_BeginSmallerThanEnd() {
        // two cases
        // case 2: beginIndex < endIndex
        int beginIndex = 1;
        int endIndex = 2;
        Position case1 = new Position(beginIndex, endIndex);
        assert (case1.getBeginIndex() < case1.getEndIndex());

        // case 1: beginIndex > endIndex
        endIndex = 1;
        beginIndex = 2;
        Position case3 = new Position(beginIndex, endIndex);
        assert (case3.getBeginIndex() < case1.getEndIndex());
    }

    @Test
    public void Position_Constructor_NegativeArgumentThrowsException() {
        // three cases:
        // case 1: begin negative
        int beginIndex = -1;
        int endIndex = 1;
        boolean thrown = false;
        try {
            Position case1 = new Position(beginIndex, endIndex);
        } catch (IllegalArgumentException iae) {
            thrown = true;
        }
        assert (thrown);

        // case 2: both  negative
        endIndex = -1;
        thrown = false;
        try {
            Position case2 = new Position(beginIndex, endIndex);
        } catch (IllegalArgumentException iae) {
            thrown = true;
        }
        assert (thrown);

        // case 3: end negative
        beginIndex = 1;
        thrown = false;
        try {
            Position case3 = new Position(beginIndex, endIndex);
        } catch (IllegalArgumentException iae) {
            thrown = true;
        }
        assert (thrown);


    }

    @Test
    public void Position_Constructor_BeginEqualToEndIndexThrowsException() {
        int beginIndex = 5;
        int endIndex = beginIndex;
        boolean thrown = false;
        try {
            Position pos = new Position(beginIndex, endIndex);
        } catch (IllegalArgumentException iae) {
            thrown = true;
        }
        assert (thrown);
    }

    @Test
    public void Position_equals_BehavesAsExpected() {
        Position pos1 = new Position(6, 15);
        Position pos2 = new Position(6, 15);
        Position pos3 = new Position(6, 8);
        Position pos4 = new Position(4, 15);

        assert (!pos2.equals(pos3));
        assert (!pos2.equals(pos4));
        assert (!pos3.equals(pos4));
        assert (pos1.equals(pos2));
        assert (pos2.equals(pos1));


    }
}
