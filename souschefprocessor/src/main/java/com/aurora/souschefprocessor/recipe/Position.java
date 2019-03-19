package com.aurora.souschefprocessor.recipe;

import java.util.Objects;

/**
 * A class describing the position of a detected element. It has two fields
 * mBeginIndex: the first index of the detected element
 * mEndIndex: the end index of the detected element. As usual in Java, this is the offset of the char after this token.
 * This means beginIndex and endindex cannot be equal
 */
public class Position {

    private int mBeginIndex;
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
    private void checkLegality(int beginIndex, int endIndex) {
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
    private boolean beginSmallerThanEnd(int beginIndex, int endIndex) {
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
     * @param string
     * @return
     */
    public boolean isLegalInString(String string) {
        int length = string.length();

        // beginIndex should be at least as small as the length of the string - 1
        if (mBeginIndex >= length) {
            return false;
        }
        // endIndex should be at least as small as the length of the string
        if (mEndIndex > length) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mBeginIndex, mEndIndex);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Position) {
            Position p = (Position) o;
            if (p.getBeginIndex() == mBeginIndex && p.getEndIndex() == mEndIndex) {
                return true;
            }
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
}
