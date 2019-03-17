package com.aurora.souschefprocessor.recipe;

import java.util.Objects;

/**
 * A DataClass representing a timer it has two fields
 * mUpperBound: an integer, representing the maximum time in seconds of the timer
 * mLowerBound: an integer, representing the minimum time in seconds of the timer
 * If the timer has only one value for the time, then mUpperBound == mLowerBound
 */
public class RecipeTimer {


    private int mUpperBound;
    private int mLowerBound;

    private Position mPosition;


    public RecipeTimer(int upperBound, int lowerBound, Position position) {
        if (upperBound <= 0) {
            throw new IllegalArgumentException("UpperBound is negative");
        }
        if (lowerBound <= 0) {
            throw new IllegalArgumentException("LowerBound is negative");
        }
        //TODO maybe also a check for too high values?
        if (upperBound >= lowerBound) {
            this.mUpperBound = upperBound;
            this.mLowerBound = lowerBound;
        } else {
            this.mLowerBound = upperBound;
            this.mUpperBound = lowerBound;
        }
        this.mPosition = position;
    }

    public RecipeTimer(int time, Position position) {
        if (time <= 0) {
            throw new IllegalArgumentException("Time is negative");
        }
        this.mUpperBound = time;
        this.mLowerBound = time;
        this.mPosition = position;
    }

    public int getUpperBound() {
        return mUpperBound;
    }

    public int getLowerBound() {
        return mLowerBound;
    }

   public Position getPosition(){
        return  mPosition;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mLowerBound, mUpperBound);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof RecipeTimer) {
            RecipeTimer rt = (RecipeTimer) o;
            if (rt.getLowerBound() == mLowerBound && rt.getUpperBound() == mUpperBound) {
                return true;
            }
        }
        return false;
    }
}
