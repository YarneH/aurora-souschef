package com.aurora.souschef;

import android.content.DialogInterface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;
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
    private static final int CHANGE_COLOR_MILLISEC_DELAY = 250;
    private static final int MINUTE_STEP = 60;
    private static final int HALF_MINUTE_STEP = 30;
    private static final int QUARTER_MINUTE_STEP = 15;
    private static final int SECOND_STEP = 1;
    private static final int PERCENT = 100;

    // A boolean which indicates whether the timer is running
    private boolean mRunning = false;
    // A boolean which indicates whether the alarm is playing
    private boolean mAlarming = false;
    // A boolean which indicates whether the background is ColorPrimary or ColorPrimaryDark
    private boolean mColorDark = true;
    // An integer indicating the time set by the user, in seconds
    private int mTimeSetByUser;
    // An integer indicating the amount of milliseconds left on the timer
    private long mMillisLeft;
    private CountDownTimer mCountDownTimer;
    // A Handler used for the change in background color when the alarm is playing
    private Handler mHandler;
    private TextView mTextViewTimer;
    private Ringtone mRingtone;

    public UITimer(RecipeTimer timer, TextView textView) {
        super(timer.getLowerBound(), timer.getLowerBound(), null);
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
                performTick(millisUntilFinished - AMOUNT_MILLISEC_IN_SEC);
            }

            @Override
            public void onFinish() {
                // The last tick is the one of 1 second remaining, because the actual last tick is a
                // long one.
            }
        }.start();
        // Set the background color to non-dark primary
        mTextViewTimer.setBackgroundColor(mTextViewTimer.getResources().getColor(R.color.colorPrimary));

        mRunning = true;

        // Preparing the ringtone for the alarm
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alert == null) {
            // alert is null, using backup
            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            // I can't see this ever being null (as always have a default notification)
            // but just in case
            if (alert == null) {
                // alert backup is null, using 2nd backup
                alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }
        mRingtone = RingtoneManager.getRingtone(mTextViewTimer.getContext(), alert);
    }

    /**
     * This function is called every tick of the CountDownTimer
     * It adjusts the amount of milliseconds remaining and the TextView of the timer
     *
     * @param millis the amount of milliseconds remaining
     */
    private void performTick(long millis) {
        // Store the amount of milliseconds left and calculate the amount of seconds left
        mMillisLeft = millis;
        int secondsLeft = (int) millis / AMOUNT_MILLISEC_IN_SEC;

        // Convert the amount left into a string representation and set the TextView
        String timerText = convertTimeToString(secondsLeft);
        mTextViewTimer.setText(timerText);

        // Check if this is the last tick (NOTE: Timer ends at 1!)
        if (secondsLeft <= 1) {
            mTextViewTimer.setBackgroundColor(mTextViewTimer.getResources().getColor(R.color.colorPrimaryDark));
            // Start the alarm
            mRingtone.play();
            mAlarming = true;

            // Start the handler, which changes to color every CHANGE_COLOR_MILLISEC_DELAY milliseconds
            if (mHandler == null) {
                mHandler = new Handler();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mColorDark) {
                            mTextViewTimer.setBackgroundColor(
                                    mTextViewTimer.getResources().getColor(R.color.colorPrimary));
                            mColorDark = false;
                        } else {
                            mTextViewTimer.setBackgroundColor(
                                    mTextViewTimer.getResources().getColor(R.color.colorPrimaryDark));
                            mColorDark = true;
                        }
                        mHandler.postDelayed(this, CHANGE_COLOR_MILLISEC_DELAY);
                    }
                }, CHANGE_COLOR_MILLISEC_DELAY);
            }
        }
    }

    /**
     * Pause the timer (Can be made public if needed)
     */
    private void pauseTimer() {
        if (mCountDownTimer != null) {
            mRunning = false;
            mCountDownTimer.cancel();
            mTextViewTimer.setBackgroundColor(mTextViewTimer.getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    /**
     * Reset the timer (Can be made public if needed)
     */
    private void resetTimer() {
        // By adding one second, we can stop the timer at 1 second left, skipping the last and longer tick
        // Subtract one to make sure the timer shows the correct value when started
        mMillisLeft = (long) mTimeSetByUser * AMOUNT_MILLISEC_IN_SEC + AMOUNT_MILLISEC_IN_SEC - 1;
        mTextViewTimer.setText(convertTimeToString(mTimeSetByUser));
        mTextViewTimer.setBackgroundColor(mTextViewTimer.getResources().getColor(R.color.colorPrimaryDark));
    }

    /**
     * Create a new onClickListener and onLongClickListener for the Timer TextView
     */
    public void setOnClickListeners() {
        // Add a listener for a short click (Pausing and resuming)
        mTextViewTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAlarming) {
                    // Stop the alarm
                    mRingtone.stop();
                    mAlarming = false;

                    // Stop changing the color
                    if (mHandler != null){
                        mHandler.removeCallbacksAndMessages(null);
                    }
                } else {
                    if (mRunning) {
                        pauseTimer();
                    } else {
                        startTimer();
                    }
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
        View promptView = li.inflate(R.layout.prompt_timer_card, null);
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
