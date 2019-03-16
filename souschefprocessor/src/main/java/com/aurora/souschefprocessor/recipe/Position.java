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
        if (beginIndex < 0 || endIndex < 0) {
            throw new IllegalArgumentException("At least one of the indexes is negative!");
        }
        if (beginIndex == endIndex) {
            throw new IllegalArgumentException("Begin index cannot be equal to end index");
        }
        if (endIndex < beginIndex) {
            mBeginIndex = endIndex;
            mEndIndex = beginIndex;
        } else {
            mBeginIndex = beginIndex;
            mEndIndex = endIndex;
        }
    }

    public int getBeginIndex() {
        return mBeginIndex;
    }

    public int getEndIndex() {
        return mEndIndex;
    }

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
