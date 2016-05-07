package io.github.nestegg333.nestegg.services;

/**
 * Created by aqeelp on 5/3/16.
 * Adapted from: https://github.com/commonsguy/cw-omnibus/tree/master/AlarmManager/Scheduled
 */

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;

import io.github.nestegg333.nestegg.R;
import io.github.nestegg333.nestegg.Utils;

public class NotificationService extends IntentService {
    private final static String TAG = "NestEgg";
    public static Context context;
    private final static int PERIOD = 3600000;

    public NotificationService() {
        super("NotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (context == null) return;

        try {
            makeNotification(0);
            byte[] buffer = new byte[28];
            FileInputStream inputStream = context.openFileInput("lastPayment");
            inputStream.read(buffer);
            String date = new String(buffer);

            long lastPayment = Date.parse(date);
            long now = (new Date()).getTime();
            if (now - lastPayment > PERIOD)
                makeNotification((int) (now - lastPayment));
        } catch (IOException e) {
            // e.printStackTrace();
            // File hasn't been made yet
        }
    }

    public static void makeNotification(int difference) {
        Log.d(TAG, "NotificationService - Making notification");

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean notifs = sharedPreferences.getBoolean("notifications", true);
        boolean vibrates = sharedPreferences.getBoolean("vibrations", true);
        if (!notifs) return;

        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.eggicon)
                        .setContentTitle("Your pet misses you!")
                        .setColor(Utils.GREEN);

        if (vibrates) {
            long[] vibPattern = {100, 100, 100, 100, 100, 100, 100};
            mBuilder.setVibrate(vibPattern);
        }

        if (difference < 1 * Utils.DAYS) {
            mBuilder.setContentText("See how they're doing!");
        } else if (difference < 2 * Utils.DAYS) {
            mBuilder.setContentText("They're feeling lonely...");
        } else if (difference < 3 * Utils.DAYS) {
            mBuilder.setContentText("It's packing its things!");
        }

        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(1, mBuilder.build());
    }
}
