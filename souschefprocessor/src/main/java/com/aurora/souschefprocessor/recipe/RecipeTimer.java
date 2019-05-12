package com.aurora.souschefprocessor.recipe;

import java.util.Objects;

/**
 * A DataClass representing a timer it has three fields.
 * mUpperBound: an integer, representing the maximum time in seconds of the timer
 * mLowerBound: an integer, representing the minimum time in seconds of the timer
 * mPosition: the position of the timer in the text where it was detected
 * If the timer has only one value for the time, then mUpperBound == mLowerBound
 */
public class RecipeTimer {
    /**
     * The upperbound of the timer = the maximum time of this detected timer (in seconds).
     * Example: "Boil for 7-9 minutes", the upperbound is equal to 9 minutes (540 seconds).
     * If there is only one value (Boil for 7 minutes), then upper- and lowerbound are equal.
     * This field cannot be negative and is always bigger than or equal to the lowerbound.
     */
    private int mUpperBound;

    /**
     * The lowerbound of the timer = the minimum time of this detected timer (in seconds).
     * Example: "Boil for 7-9 minutes", the lowerbound is equal to 7 minutes (420 seconds).
     * If there is only one value (Boil for 7 minutes), then upper- and lowerbound are equal.
     * This field cannot be negative and is always lower than or equal to the upperbound.
     */
    private int mLowerBound;

    /**
     * The position of this timer in the string it was detected in
     */
    private Position mPosition;


    /**
     * Construct a recipetimer with a position, lower- and upperbound. If the first argument is smaller
     * than the second argument the lower and upperbound are switched in order to not violate the constraint
     * that the {@link #mLowerBound} cannot be bigger than the {@link #mUpperBound}.
     * <p>
     * If one of the bounds is negative, an IllegalArgumentException is thrown.
     *
     * @param upperBound The upperbound for this timer, sets the {@link #mUpperBound} (if it is not
     *                   smaller than the lowerBound.
     * @param lowerBound The lowerbound for this timer, sets the {@link #mLowerBound} (if it is not
     *                   bigger than the upperBound.
     * @param position   The position of the detected timer in the string it was detected in.
     */
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

    /**
     * Constructs a timer where the lower and upperbound are equal. Throws an exception if the
     * time argument is negative
     *
     * @param time     The value to set the {@link #mLowerBound} and {@link #mUpperBound}
     * @param position The position of the timer in the string it was detected in
     */
    public RecipeTimer(int time, Position position) {
        if (time <= 0) {
            throw new IllegalArgumentException("Time is negative");
        }
        this.mUpperBound = time;
        this.mLowerBound = time;
        this.mPosition = position;
    }

    public Position getPosition() {
        return mPosition;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mLowerBound, mUpperBound);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof RecipeTimer) {
            RecipeTimer rt = (RecipeTimer) o;
            return (rt.getLowerBound() == mLowerBound && rt.getUpperBound() == mUpperBound);

        }
        return false;
    }

    public int getLowerBound() {
        return mLowerBound;
    }

    public int getUpperBound() {
        return mUpperBound;
    }

    @Override
    public String toString() {
        if (mLowerBound == mUpperBound) {
            return mLowerBound + " seconds";
        } else {
            return mLowerBound + " - " +
                    mUpperBound + " seconds";
        }
    }
}
