package com.aurora.souschef;

import android.os.CountDownTimer;
import android.widget.TextView;

import com.aurora.souschefprocessor.recipe.RecipeTimer;

public class UITimer extends RecipeTimer {
    private static final int AMOUNT_MILLISEC_IN_SEC = 1000;
    private static final int AMOUNT_SEC_IN_HOUR = 3600;
    private static final int AMOUNT_SEC_IN_MIN = 60;


    private boolean mRunning = false;
    private int mTimeSetByUser;
    private long mMillisLeft;
    private CountDownTimer mCountDownTimer;
    private TextView mTextViewTimer;

    public UITimer(int lowerBound, int upperBound, TextView textView) {
        super(lowerBound, upperBound);
        mTimeSetByUser = lowerBound;
        mTextViewTimer = textView;
        // One is subtracted to make sure the timer shows the correct value when started
        mMillisLeft = mTimeSetByUser * AMOUNT_MILLISEC_IN_SEC - 1;
    }

    public void setTimeSetByUser(int value) {
        mTimeSetByUser = value;
    }

    // Resets when called multiple times!
    public void startTimer() {
        mCountDownTimer = new CountDownTimer(mMillisLeft, AMOUNT_MILLISEC_IN_SEC) {
            @Override
            public void onTick(long millisUntilFinished) {
                performTick(millisUntilFinished);
            }

            public void onFinish() {
                mTextViewTimer.setText("Done!");
            }
        }.start();


        mRunning = true;
    }

    // Function called every second by the CountDownTimer
    private void performTick(long millis) {
        mMillisLeft = millis;
        int secondsLeft = (int) millis / AMOUNT_MILLISEC_IN_SEC;

        String timerText = convertTimeToString(secondsLeft);
        mTextViewTimer.setText(timerText);
    }

    public void pauseTimer() {
        mRunning = false;
        mCountDownTimer.cancel();
    }

    public boolean isRunning() {
        return mRunning;
    }

    private String convertTimeToString(int time) {
        int amountHours = time / AMOUNT_SEC_IN_HOUR;
        int amountMins = (time - amountHours * AMOUNT_SEC_IN_HOUR) / AMOUNT_SEC_IN_MIN;
        int amountSec = time - amountHours * AMOUNT_SEC_IN_HOUR - amountMins * AMOUNT_SEC_IN_MIN;

        String timerText = "";

        if (amountHours != 0) {
            timerText += amountHours + ":";
        }
        timerText += String.format("%02d", amountMins) + ":" + String.format("%02d", amountSec);

        return timerText;
    }

}
