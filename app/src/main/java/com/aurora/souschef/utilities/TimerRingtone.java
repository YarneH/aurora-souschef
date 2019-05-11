package com.aurora.souschef.utilities;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

/**
 * A singleton class for the Ringtone playing when one or multiple timers finishes
 */
public class TimerRingtone {
    /**
     * The singleton instance
     */
    private static final TimerRingtone mInstance = new TimerRingtone();

    /**
     * The Ringtone used as TimerRingtone
     */
    private Ringtone mRingtone = null;

    /**
     * The amount of timers going off
     */
    private int mAmountGoingOff = 0;

    /**
     * A boolean representing whether the TimerRingtone has been initiated
     */
    private boolean mInitiated = false;

    private TimerRingtone() {
        // Private constructor for singleton
    }

    /**
     * Get the TimerRingtone instance
     *
     * @return the instance
     */
    public static TimerRingtone getInstance() {
        return mInstance;
    }

    public void initiate(Context context) {
        if (!mInitiated) {
            // Preparing the ringtone for the alarm
            Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (alert == null) {
                // alert is null, using backup
                alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                // in case this is also null again
                if (alert == null) {
                    // alert backup is null, using 2nd backup
                    alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                }
            }
            mRingtone = RingtoneManager.getRingtone(context, alert);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                mRingtone.setLooping(true);
            }

            mInitiated = true;
        }
    }

    public void addTimerGoingOff() {
        mAmountGoingOff++;
        updateRingtone();
    }

    public void removeTimerGoingOff() {
        mAmountGoingOff--;
        updateRingtone();
    }

    private void updateRingtone() {
        if (mAmountGoingOff > 0 && !mRingtone.isPlaying()) {
            mRingtone.play();
        } else if (mAmountGoingOff == 0 && mRingtone.isPlaying()) {
            mRingtone.stop();
        }
    }
}
