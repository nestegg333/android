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
    private final static int DAYS = 86400000;

    public NotificationService() {
        super("NotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (context == null) return;

        try {
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

        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.eggicon)
                        .setContentTitle("Your pet misses you!")
                        .setColor(Utils.GREEN);

        if (difference < 1 * DAYS) {
            mBuilder.setContentText("See how they're doing!");
        } else if (difference < 2 * DAYS) {
            mBuilder.setContentText("They're feeling lonely...");
        } else if (difference < 3 * DAYS) {
            mBuilder.setContentText("It's packing its things!");
        }

        //Intent resultIntent = new Intent(context, LogInActivity.class);
        Intent intentToStart = context.getPackageManager().getLaunchIntentForPackage("io.github.nestegg333.nestegg");
        intentToStart.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(context, 0, intentToStart,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(1, mBuilder.build());
    }
}
