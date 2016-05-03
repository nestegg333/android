package io.github.nestegg333.nestegg.services;

/**
 * Created by aqeelp on 5/3/16.
 * Adapted from: https://github.com/commonsguy/cw-omnibus/tree/master/AlarmManager/Scheduled
 */

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class NotificationService extends IntentService {
    private final static String TAG = "NestEgg";

    public NotificationService() {
        super("NotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "I ran!");
    }
}
