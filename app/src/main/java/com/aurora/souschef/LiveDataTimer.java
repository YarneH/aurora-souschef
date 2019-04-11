package com.aurora.souschef;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.CountDownTimer;

import com.aurora.souschefprocessor.recipe.RecipeTimer;

import java.util.Locale;

/**
 * Class that keeps track of a timer, while being independent of the UI.
 * Uses LiveData to update the UI.
 */
public class LiveDataTimer {
    /**
     * The amount of milliseconds in a second. Needed to convert
     * RecipeTimers (which are in seconds) to actual timers.
     */
    private static final int MILLIS = 1000;
    /**
     * Amount of time units in a bigger time unit :)
     * (why is there no name for that? I propose time-babies.)
     */
    private static final int TIME_BABIES = 60;
    /**
     * The actual timer that can count down.
     */
    private CountDownTimer mCountDownTimer;
    /**
     * The timer representing the actual values from the recipe.
     */
    private RecipeTimer mRecipeTimer;
    /**
     * State of the timer. Is not running when false.
     */
    private boolean mRunning = false;
    /**
     * The original time received from the user or from the recipe.
     */
    private int mTimeSetByUser;

    private MutableLiveData<Boolean> mFinished = new MutableLiveData<>();

    /**
     * Holds the observable time left until finished.
     * Call observe on this field to keep track of time in the UI.
     */
    private MutableLiveData<Long> mMillisLeft = new MutableLiveData<>();

    /**
     * Create a new timer based on a timer described in a recipe.
     *
     * @param recipeTimer the timer it is based on.
     */
    public LiveDataTimer(RecipeTimer recipeTimer) {
        mRecipeTimer = new RecipeTimer(recipeTimer.getUpperBound(), recipeTimer.getLowerBound(), null);
        mTimeSetByUser = recipeTimer.getLowerBound();
        mFinished.setValue(false);
        mMillisLeft.setValue((long) (mTimeSetByUser * MILLIS));
        mRunning = false;

    }

    /**
     * Create a new timer based on upper and lower bound.
     *
     * @param lowerBound lower bound of time. Is swapped in case it is larger than upper
     *                   bound. This swap happens in the RecipeTimer class
     * @param upperBound upper bound of time.
     */
    public LiveDataTimer(int lowerBound, int upperBound) {
        mRecipeTimer = new RecipeTimer(upperBound, lowerBound, null);
        mTimeSetByUser = mRecipeTimer.getLowerBound();
        mFinished.setValue(false);
        mMillisLeft.setValue((long) (mTimeSetByUser * MILLIS));
        mRunning = false;
    }

    public void resetTimer() {
        mMillisLeft.setValue((long) (mTimeSetByUser * MILLIS));
        mRunning = false;
    }

    /**
     * Toggles playing paused state of the timer.
     */
    public void toggleTimer() {

        if (mRunning) {
            mRunning = false;
            mCountDownTimer.cancel();
            return;
        }
        this.mCountDownTimer = new CountDownTimer(mMillisLeft.getValue() - 1, MILLIS) {
            @Override
            public void onTick(long millisUntilFinished) {
                mMillisLeft.setValue(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                mFinished.setValue(true);
                //TODO: check if running needs to be set to false
                mRunning = false;
            }
        };
        this.mCountDownTimer.start();
        mRunning = true;
    }

    public LiveData<Long> getMillisLeft() {
        return mMillisLeft;
    }

    /**
     * The user can change the time by long-pressing.
     * This can only be done when upperbound != lowerbound.
     * This seems weird, but okay.
     *
     * @param timeInSeconds set time.
     */
    public void setTimeSetByUser(int timeInSeconds) {
        mTimeSetByUser = timeInSeconds;
        resetTimer();
    }

    /**
     * Convert an amount of seconds into a string representation
     *
     * @param amountMilliSeconds long representing the amount of milliseconds
     * @return a String representation of the time
     */
    public static String convertTimeToString(long amountMilliSeconds) {
        int amountSeconds = (int) (amountMilliSeconds / MILLIS);
        // seconds / 3600, or divide twice by 60.
        int amountHours = amountSeconds / TIME_BABIES / TIME_BABIES;
        // subtract the amount of hours first, then divide seconds by 60 to get minutes.
        int amountMins = (amountSeconds % (TIME_BABIES * TIME_BABIES)) / TIME_BABIES;
        // remaining time in seconds.
        int amountSec = amountSeconds % TIME_BABIES;

        String timerText = "";

        // Only add hours when there are hours.
        if (amountHours != 0) {
            timerText += amountHours + ":";
        }
        timerText += String.format(Locale.getDefault(), "%02d:%02d", amountMins, amountSec);

        return timerText;
    }

    /**
     * Returns whether or not the timer can be changed.
     *
     * @return
     */
    public boolean canChangeTimer() {
        return mRunning && (mRecipeTimer.getLowerBound() != mRecipeTimer.getUpperBound());
    }

    public int getUpperBound() {
        return mRecipeTimer.getUpperBound();
    }

    public int getLowerBound() {
        return mRecipeTimer.getLowerBound();
    }

    public LiveData<Boolean> getIsFinished() {
        return mFinished;
    }
}
