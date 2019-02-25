package com.aurora.souschef.SouchefProcessor.Recipe;

/**
 * A DataClass representing a timer it has two fields
 * mUpperBound: an integer, representing the maximum time in seconds of the timer
 * mLowerBound: an integer, representing the minimum time in seconds of the timer
 * If the timer has only one value for the time, then mUpperBound == mLowerBound
 */
public class RecipeTimer {


    private int mUpperBound;
    private int mLowerBound;


    public RecipeTimer(int upperBound, int lowerBound) throws IllegalArgumentException {
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
    }

    public RecipeTimer(int time) throws IllegalArgumentException {
        if (time <= 0) {
            throw new IllegalArgumentException("Time is negative");
        }
        this.mUpperBound = time;
        this.mLowerBound = time;
    }

    public int getUpperBound() {
        return mUpperBound;
    }

    public int getLowerBound() {
        return mLowerBound;
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

    @Override
    public String toString(){
        if(mUpperBound == mLowerBound){
            return "Time: "+mUpperBound;
        }
        else{
            return "Time: ["+mLowerBound+", "+mUpperBound+"]";
        }
    }

}
