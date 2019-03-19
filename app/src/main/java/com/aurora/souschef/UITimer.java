package com.aurora.souschef;

import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.aurora.souschefprocessor.recipe.RecipeTimer;

/**
 * A UI class representing a RecipeTimer. It adds a CountDownTimer to a already existing TextView
 * mRunning: a boolean, representing whether the timer is running
 * mTimeSetByUser: an int, representing a value between the upper and lower bound, chosen by the user
 * mMillisLeft: a long, representing the amount of milliseconds left on the CountDownTimer
 * mTextViewTimer: the TextView of the timer
 * mCountDownTimer: a CountDownTimer that counts down the seconds of the timer
 */
public class UITimer extends RecipeTimer {
    private static final int AMOUNT_MILLISEC_IN_SEC = 1000;
    private static final int AMOUNT_SEC_IN_HOUR = 3600;
    private static final int AMOUNT_SEC_IN_HALF_HOUR = 1800;
    private static final int AMOUNT_SEC_IN_QUARTER = 900;
    private static final int AMOUNT_SEC_IN_MIN = 60;
    private static final int MINUTE_STEP = 60;
    private static final int HALF_MINUTE_STEP = 30;
    private static final int QUARTER_MINUTE_STEP = 15;
    private static final int SECOND_STEP = 1;
    private static final int PERCENT = 100;

    private boolean mRunning = false;
    private int mTimeSetByUser;
    private long mMillisLeft;
    private CountDownTimer mCountDownTimer;
    private TextView mTextViewTimer;

    public UITimer(int lowerBound, int upperBound, TextView textView) {
        super(lowerBound, upperBound, null);
        // Use getLowerBound so the lower and upper bound are switched if needed (implemented in RecipeTimer)
        mTimeSetByUser = getLowerBound();
        mTextViewTimer = textView;

        resetTimer();
    }

    /**
     * Starts the timer
     * If this is called a second time, the CountDownTimer resets with the value of mMillisLeft as
     * remaining milliseconds (Can be made public if needed)
     */
    private void startTimer() {
        mCountDownTimer = new CountDownTimer(mMillisLeft, AMOUNT_MILLISEC_IN_SEC) {
            @Override
            public void onTick(long millisUntilFinished) {
                performTick(millisUntilFinished);
            }

            // TODO: Call to onFinish is not as quick as onTick (takes +/- 1.8 sec)
            // We could fix this by adding a second to the timer and ending it at 1 sec
            @Override
            public void onFinish() {
                performTick(0);
            }
        }.start();

        mRunning = true;
    }

    /**
     * This function is called every tick of the CountDownTimer
     * It adjusts the amount of milliseconds remaining and the TextView of the timer
     *
     * @param millis the amount of milliseconds remaining
     */
    private void performTick(long millis) {
        mMillisLeft = millis;
        int secondsLeft = (int) millis / AMOUNT_MILLISEC_IN_SEC;
        String timerText = convertTimeToString(secondsLeft);
        mTextViewTimer.setText(timerText);
    }

    /**
     * Pause the timer (Can be made public if needed)
     */
    private void pauseTimer() {
        if (mCountDownTimer != null) {
            mRunning = false;
            mCountDownTimer.cancel();
        }
    }

    /**
     * Reset the timer (Can be made public if needed)
     */
    private void resetTimer() {
        // Subtract one to make sure the timer shows the correct value when started
        mMillisLeft = (long) mTimeSetByUser * AMOUNT_MILLISEC_IN_SEC - 1;
        mTextViewTimer.setText(convertTimeToString(mTimeSetByUser));
    }

    /**
     * Create a new onClickListener and onLongClickListener for the Timer TextView
     */
    public void setOnClickListeners() {
        // Add a listener for a short click (Pausing and resuming)
        mTextViewTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mRunning) {
                    pauseTimer();
                } else {
                    startTimer();
                }
            }
        });

        // Add a listener for a long click (Show an input for setting the timer, if timer isn't running)
        mTextViewTimer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!mRunning && getLowerBound() != getUpperBound()) {
                    pauseTimer();
                    setTimerPopup();
                }
                return true;
            }
        });
    }

    /**
     * Creates a popup and show it to the user.
     * The user then can select the value between the upper and lower bound he/she prefers
     */
    private void setTimerPopup() {
        // Calculate the difference and the associated step for the Seekbar
        int difference = getUpperBound() - getLowerBound();
        int step;
        if (difference >= AMOUNT_SEC_IN_HOUR) {
            step = MINUTE_STEP;
        } else if (difference >= AMOUNT_SEC_IN_HALF_HOUR) {
            step = HALF_MINUTE_STEP;
        } else if (difference > AMOUNT_SEC_IN_QUARTER) {
            step = QUARTER_MINUTE_STEP;
        } else {
            step = SECOND_STEP;
        }

        // Initiate the LayoutInflater and inflate the Popup layout
        LayoutInflater li = LayoutInflater.from(mTextViewTimer.getContext());
        View promptView = li.inflate(R.layout.card_timer, null);
        SeekBar seekBar = (SeekBar) promptView.findViewById(R.id.sk_timer);

        // Get the TextView of the popup and set to the initial value
        final TextView seekBarValue = (TextView) promptView.findViewById(R.id.tv_timer);
        seekBarValue.setText(convertTimeToString(getLowerBound()));

        // Set the listener of the SeekBar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int newValue = convertProgressToSeconds(progress, step);

                seekBarValue.setText(convertTimeToString(newValue));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Auto-generated method stub (not needed)
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Auto-generated method stub (not needed)
            }
        });

        // Build the actual popup and show it to the user
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mTextViewTimer.getContext());
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mTimeSetByUser = convertProgressToSeconds(seekBar.getProgress(), step);
                        resetTimer();
                    }
                });
        alertDialogBuilder.create().show();
    }

    /**
     * Calculate the amount of seconds set by user using the progress of the SeekBar
     *
     * @param progress The progress of the SeekBar, representing a value between 0 and 100
     * @return The amount of seconds currently chosen with the SeekBar
     */
    private int convertProgressToSeconds(int progress, int step) {
        int difference = getUpperBound() - getLowerBound();
        double progressValue = ((double) (difference)) * progress / PERCENT;
        int incrementValue = (int) Math.floor(progressValue / step) * step;

        return getLowerBound() + incrementValue;
    }

    /**
     * Convert an amount of seconds into a string representation
     *
     * @param amountSeconds integer representing the amount of seconds
     * @return a String representation of the time
     */
    private static String convertTimeToString(int amountSeconds) {
        int amountHours = amountSeconds / AMOUNT_SEC_IN_HOUR;
        int amountMins = (amountSeconds - amountHours * AMOUNT_SEC_IN_HOUR) / AMOUNT_SEC_IN_MIN;
        int amountSec = amountSeconds - amountHours * AMOUNT_SEC_IN_HOUR - amountMins * AMOUNT_SEC_IN_MIN;

        String timerText = "";

        if (amountHours != 0) {
            timerText += amountHours + ":";
        }
        timerText += String.format("%02d", amountMins) + ":" + String.format("%02d", amountSec);

        return timerText;
    }

}
