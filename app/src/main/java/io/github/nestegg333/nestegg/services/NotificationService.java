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

import io.github.nestegg333.nestegg.LogInActivity;
import io.github.nestegg333.nestegg.R;
import io.github.nestegg333.nestegg.Utils;

public class NotificationService extends IntentService {
    private final static String TAG = "NestEgg";
    public static Context context;

    public NotificationService() {
        super("NotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "NotificationService - Making notification");
        makeNotification();
    }

    public static void makeNotification() {
        if (context == null) return;
        
        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.eggicon)
                        .setContentTitle("Your pet misses you!")
                        .setContentText("See what they've been up to while you've been gone.")
                        .setColor(Utils.GREEN);

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
