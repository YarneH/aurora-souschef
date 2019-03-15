package com.aurora.souschef;

import android.os.CountDownTimer;

import com.aurora.souschefprocessor.recipe.RecipeTimer;

public class UITimer extends RecipeTimer {
    private boolean mRunning = false;
    private int mTimeSetByUser;
    private CountDownTimer mCountDownTimer;

    public UITimer(int lowerBound, int upperBound) {
        super(lowerBound, upperBound);
        mTimeSetByUser = lowerBound;
    }

    public void setTimeSetByUser(int value) {
        mTimeSetByUser = value;
    }

    public boolean isRunning() {
        return mRunning;
    }

}
