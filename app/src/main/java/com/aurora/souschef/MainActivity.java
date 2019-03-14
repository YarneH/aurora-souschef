package com.aurora.souschef;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.aurora.auroralib.Constants;

public class MainActivity extends AppCompatActivity {
    private static final int AMOUNT_SEC_IN_HOUR = 3600;
    private static final int AMOUNT_SEC_IN_HALF_HOUR = 1800;
    private static final int AMOUNT_SEC_IN_QUARTER = 900;
    private static final int AMOUNT_SEC_IN_MIN = 60;
    private static final int MINUTE_STEP = 60;
    private static final int HALF_MINUTE_STEP = 30;
    private static final int QUARTER_MINUTE_STEP = 15;
    private static final int SECOND_STEP = 1;
    private static final int PERCENT = 100;

    // TODO: remove dummy values
    private final static int DUMMY_LOWERBOUND = 45 * AMOUNT_SEC_IN_MIN;
    private final static int DUMMY_UPPERBOUND = 90 * AMOUNT_SEC_IN_MIN;


    private TextView mTextView;

    public MainActivity() {
        // default constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.textView);

        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity.getAction().equals(Constants.PLUGIN_ACTION)) {
            if (intentThatStartedThisActivity.hasExtra(Constants.PLUGIN_INPUT_TEXT)) {
                String inputText = intentThatStartedThisActivity.getStringExtra(Constants.PLUGIN_INPUT_TEXT);
                mTextView.setText(inputText);
                // Not implemented yet Recipe recipe = Communicator.delegate(inputText)
                // Not implemented yet String result = basicPluginObject.getResult()
                // Not implemented yet mTextView.setText(result)
            } else if (intentThatStartedThisActivity.hasExtra(Constants.PLUGIN_INPUT_OBJECT)) {
                // TODO: Handle a PluginObject that was cached
                //dummy
                Log.d("NO IMPLEMENTATION", "not implemented yet");

            }
        }

        // TODO: Change location of timer
        setTimerPopup();
    }

    // TODO: Change location of timer
    public void setTimerPopup() {
        LayoutInflater li = LayoutInflater.from(this);
        View promptView = li.inflate(R.layout.card_timer, null);

        int lowerBound = DUMMY_LOWERBOUND;
        int upperBound = DUMMY_UPPERBOUND;
        int difference = upperBound - lowerBound;

        SeekBar seekBar = (SeekBar) promptView.findViewById(R.id.sk_timer);

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

        final TextView seekBarValue = (TextView) promptView.findViewById(R.id.tv_timer);
        seekBarValue.setText(convertTimeToString(lowerBound));

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO Auto-generated method stub
                double progressValue = ((double) (upperBound - lowerBound)) * progress / PERCENT;
                int incrementValue = (int) Math.floor(progressValue / step) * step;
                int newValue = lowerBound + incrementValue;

                String timerText = convertTimeToString(newValue);

                seekBarValue.setText(timerText);
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

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // TODO: Set new value of timer
                    }
                });

        alertDialogBuilder.create().show();

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

