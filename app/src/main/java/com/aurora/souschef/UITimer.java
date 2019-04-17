package com.aurora.souschef;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LifecycleOwner;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * A UI class responsible for filling in the UI with timer data.
 */
public class UITimer {
    /**
     * Time constant: seconds in an hour.
     */
    private static final int AMOUNT_SEC_IN_HOUR = 3600;
    /**
     * Time constant: seconds in half an hour.
     */
    private static final int AMOUNT_SEC_IN_HALF_HOUR = 1800;
    /**
     * Time constant: seconds in a quarter hour.
     */
    private static final int AMOUNT_SEC_IN_QUARTER = 900;
    private static final int AMOUNT_SEC_IN_MIN = 60;
    private static final int CHANGE_COLOR_MILLISEC_DELAY = 250;
    /**
     * Time constant: seconds in a minute.
     */
    private static final int MINUTE_STEP = 60;
    /**
     * Time constant: seconds in half a minute.
     */
    private static final int HALF_MINUTE_STEP = 30;
    /**
     * Time constant: seconds in 15 seconds (?!?).
     */
    private static final int QUARTER_MINUTE_STEP = 15;
    /**
     * Time constant: the amount of seconds in exactly one second.
     */
    private static final int SECOND_STEP = 1;
    /**
     * Maximum percentage. Preventing magic numbers.
     */
    private static final int PERCENT = 100;

    /**
     * Data container for timers.
     */
    private final LiveDataTimer mLiveDataTimer;
    /**
     * View where the timer is displayed.
     */
    private View mTimerCard;

    /**
     * Sets up text and timer views.
     *
     * @param liveDataTimer timer data container
     * @param timerCard     view where to put the timer. Should be a timer_card.xml
     * @param owner         LifeCycleOwner responsible for the LiveData objects.
     *                      Normally the activity.
     */
    public UITimer(LiveDataTimer liveDataTimer, View timerCard, LifecycleOwner owner) {
        this.mLiveDataTimer = liveDataTimer;
        this.mTimerCard = timerCard;

        // set timer observer to update text field.
        TextView timerText = timerCard.findViewById(R.id.tv_timer);
        liveDataTimer.getMillisLeft().observe(owner, (Long millisLeft) -> {
            if (millisLeft != null) {
                timerText.setText(LiveDataTimer.convertTimeToString(millisLeft));
            }
        });

        setOnClickListeners(timerText);
        this.mLiveDataTimer.getIsFinished().observe(owner, aBoolean -> onTimerFinished());
    }

    /**
     * TODO: What happens on timer completion?
     */
    // NOSONAR
    private void onTimerFinished() {
        // TODO: implement what happens when timer finishes.
    }

    /**
     * Create a new onClickListener and onLongClickListener for the Timer TextView.
     * <p>
     * Uses toggleTimer.
     */
    private void setOnClickListeners(View clickableView) {
        clickableView.setOnClickListener((View v) -> mLiveDataTimer.toggleTimer());
        clickableView.setOnLongClickListener((View v) -> {
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
        View promptView = li.inflate(R.layout.prompt_timer_card, null);
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
                .setPositiveButton("Ok", (DialogInterface dialogInterface, int id) -> {
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
