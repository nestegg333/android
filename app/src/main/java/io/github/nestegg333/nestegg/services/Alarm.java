package io.github.nestegg333.nestegg.services;

/**
 * Created by aqeelp on 5/3/16.
 * Adapted from: https://github.com/commonsguy/cw-omnibus/tree/master/AlarmManager/Scheduled
 */

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

public class Alarm extends BroadcastReceiver {
    private static final int PERIOD = 5000;
    private final static String TAG = "NestEgg";

    @Override
    public void onReceive(Context context, Intent i) {
        scheduleAlarms(context);
    }

    public static void scheduleAlarms(Context context) {
        Log.d(TAG, "Setting alarm...");

        AlarmManager manager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationService.class);
        PendingIntent pi = PendingIntent.getService(context, 0, intent, 0);

        manager.setRepeating(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + PERIOD, PERIOD, pi);
    }
}
