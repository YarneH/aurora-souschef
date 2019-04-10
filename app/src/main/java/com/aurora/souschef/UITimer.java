package com.aurora.souschef;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LifecycleOwner;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * A UI class representing a RecipeTimer. It adds a CountDownTimer to a already existing TextView
 * mRunning: a boolean, representing whether the timer is running
 * mTimeSetByUser: an int, representing a value between the upper and lower bound, chosen by the user
 * mMillisLeft: a long, representing the amount of milliseconds left on the CountDownTimer
 * mTextViewTimer: the TextView of the timer
 * mCountDownTimer: a CountDownTimer that counts down the seconds of the timer
 */
public class UITimer {
    private static final int AMOUNT_SEC_IN_HOUR = 3600;
    private static final int AMOUNT_SEC_IN_HALF_HOUR = 1800;
    private static final int AMOUNT_SEC_IN_QUARTER = 900;
    private static final int MINUTE_STEP = 60;
    private static final int HALF_MINUTE_STEP = 30;
    private static final int QUARTER_MINUTE_STEP = 15;
    private static final int SECOND_STEP = 1;
    private static final int PERCENT = 100;

    private final LiveDataTimer mLiveDataTimer;
    private View mTimerCard;

    public UITimer(LiveDataTimer liveDataTimer, View timerCard, LifecycleOwner owner) {
        this.mLiveDataTimer = liveDataTimer;
        this.mTimerCard = timerCard;

        // set timer observer to update text field.
        TextView timerText = timerCard.findViewById(R.id.tv_timer);
        liveDataTimer.getMillisLeft().observe(owner, aLong -> {
            if (aLong != null) {
                timerText.setText(LiveDataTimer.convertTimeToString(aLong));
            }
        });

        setOnClickListeners(timerText);
        this.mLiveDataTimer.getIsFinished().observe(owner, aBoolean -> onTimerFinished());
    }

    private void onTimerFinished() {
        // TODO: implement what happens when timer finishes.
    }

    /**
     * Create a new onClickListener and onLongClickListener for the Timer TextView.
     * <p>
     * Uses toggleTimer.
     */
    private void setOnClickListeners(View clickableView) {

        clickableView.setOnClickListener(v -> {
            mLiveDataTimer.toggleTimer();
        });
        clickableView.setOnLongClickListener(v -> {
            if (mLiveDataTimer.canChangeTimer()) {
                setTimerPopup();
            }
            return true;
        });
    }

    /**
     * Creates a popup and show it to the user.
     * The user then can select the value between the upper and lower bound he/she prefers
     */
    private void setTimerPopup() {
        // Calculate the difference and the associated step for the Seekbar
        int difference = mLiveDataTimer.getUpperBound() - mLiveDataTimer.getLowerBound();
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
        LayoutInflater li = LayoutInflater.from(mTimerCard.getContext());
        // using null as root is allowed here since it is a promptView
        @SuppressLint("InflateParams")
        View promptView = li.inflate(R.layout.card_timer, null);
        SeekBar seekBar = promptView.findViewById(R.id.sk_timer);

        // Get the TextView of the popup and set to the initial value
        final TextView seekBarValue = promptView.findViewById(R.id.tv_timer);
        seekBarValue.setText(LiveDataTimer.convertTimeToString(mLiveDataTimer.getLowerBound()));

        // Set the listener of the SeekBar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int newValue = convertProgressToSeconds(progress, step);

                seekBarValue.setText(LiveDataTimer.convertTimeToString(newValue));
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
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mTimerCard.getContext());
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Ok", (dialogInterface, i) -> {
                    int timeSetByUser = convertProgressToSeconds(seekBar.getProgress(), step);
                    mLiveDataTimer.setTimeSetByUser(timeSetByUser);
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
        int difference = mLiveDataTimer.getUpperBound() - mLiveDataTimer.getLowerBound();
        double progressValue = ((double) (difference)) * progress / PERCENT;
        int incrementValue = (int) Math.floor(progressValue / step) * step;

        return mLiveDataTimer.getLowerBound() + incrementValue;
    }

}
