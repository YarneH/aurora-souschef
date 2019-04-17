package com.aurora.souschefprocessor.recipe;

import java.util.Objects;

/**
 * A class describing the position of a detected element. It has two fields
 * mBeginIndex: the first index of the detected element
 * mEndIndex: the end index of the detected element. As usual in Java, this is the offset of the char after this token.
 * This means beginIndex and endindex cannot be equal
 */
public class Position {

    /**
     * The beginIndex of this position. This is the index of the first character of the detected
     * element.
     */
    private int mBeginIndex;
    /**
     * The endindex. This is (as usual in Java) the offset of the char after this detected element.
     */
    private int mEndIndex;

    public Position(int beginIndex, int endIndex) {
        // check if the arguments are legal
        checkLegality(beginIndex, endIndex);

        // make sure beginindex is smaller than endindex
        if (!beginSmallerThanEnd(beginIndex, endIndex)) {
            mBeginIndex = endIndex;
            mEndIndex = beginIndex;
        } else {
            mBeginIndex = beginIndex;
            mEndIndex = endIndex;
        }
    }

    /**
     * A function to check if the beginIndex and endIndex are legal. An index cannot be negative,
     * nor can begin and endindex be equal. If they are not legal an IllegalArgumentException is
     * thrown
     *
     * @param beginIndex The beginIndex
     * @param endIndex   The endIndex
     */
    private static void checkLegality(int beginIndex, int endIndex) {
        if (beginIndex < 0 || endIndex < 0) {
            throw new IllegalArgumentException("At least one of the indexes is negative!");
        }
        if (beginIndex == endIndex) {
            throw new IllegalArgumentException("Begin index cannot be equal to end index");
        }
    }

    /**
     * A function that checks is the beginIndex is smaller than the index
     *
     * @param beginIndex the beginIndex
     * @param endIndex   the endIndex
     * @return A boolean indicating if the beginindex is smaller than the endIndex
     */
    private static boolean beginSmallerThanEnd(int beginIndex, int endIndex) {
        return beginIndex < endIndex;
    }

    public int getBeginIndex() {
        return mBeginIndex;
    }

    public void setBeginIndex(int beginIndex) {
        // first check if this is a legal argument and throw if illegal
        checkLegality(beginIndex, mEndIndex);

        // check if begin is still smaller than end
        // else throw an exception
        if (beginSmallerThanEnd(beginIndex, mEndIndex)) {

            mBeginIndex = beginIndex;
        } else {
            throw new IllegalArgumentException("The new beginIndex cannot be bigger than the endIndex!");
        }
    }

    public int getEndIndex() {
        return mEndIndex;
    }

    public void setEndIndex(int endIndex) {
        // first check if this is a legal argument and throw if illegal
        checkLegality(mBeginIndex, endIndex);

        // check if begin is still smaller than end
        // else throw an exception
        if (beginSmallerThanEnd(mBeginIndex, endIndex)) {

            mEndIndex = endIndex;
        } else {
            throw new IllegalArgumentException("The new endIndex cannot be smaller than the beginIndex!");
        }
    }

    /**
     * A function that checks if this Position is legal in a String. A position is legal if the
     * beginIndex is at most the index of the last character of the string and the endIndex is at most
     * the length of the string (index after the last character)
     *
     * @param string the string to check the legality of the position in
     * @return A boolean that indicates whether this position is legal in the string
     */
    boolean isLegalInString(String string) {
        if (string == null) {
            return false;
        }
        int length = string.length();

        // beginIndex should be at least as small as the length of the string - 1
        if (mBeginIndex >= length) {
            return false;
        }
        // endIndex should be at least as small as the length of the string
        return mEndIndex <= length;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mBeginIndex, mEndIndex);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Position) {
            Position p = (Position) o;
            return (p.getBeginIndex() == mBeginIndex && p.getEndIndex() == mEndIndex);

        }
        return false;
    }

    @Override
    public String toString() {
        return "Position{" +
                "mBeginIndex=" + mBeginIndex +
                ", mEndIndex=" + mEndIndex +
                '}';
    }

    /**
     * Trims the position to the given string. This ensures that the endIndex is never bigger than
     * the string
     *
     * @param s The string to trim to
     */
    void trimToLengthOfString(String s) {
        int length = s.length();
        if (mBeginIndex >= length) {
            throw new IllegalArgumentException("This string is shorter than the beginIndex of " +
                    "this position, trimming is impossible");
        }
        if (mEndIndex > length) {
            mEndIndex = length;
        }
    }

    /**
     * Sets both the beginIndex and endIndex
     *
     * @param beginIndex the beginindex
     * @param endIndex   the endIndex
     */
    public void setIndices(int beginIndex, int endIndex) {
        // check if the arguments are legal
        checkLegality(beginIndex, endIndex);

        // make sure beginindex is smaller than endindex
        if (!beginSmallerThanEnd(beginIndex, endIndex)) {
            mBeginIndex = endIndex;
            mEndIndex = beginIndex;
        } else {
            mBeginIndex = beginIndex;
            mEndIndex = endIndex;
        }
    }

}
