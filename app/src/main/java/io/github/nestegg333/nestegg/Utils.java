package io.github.nestegg333.nestegg;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.lang.reflect.Field;

/**
 * Created by aqeelp on 4/12/16.
 */
public class Utils {
    public static String
            TOKEN = "TOKEN",
            USERNAME = "USERNAME",
            PASSWORD = "PASSWORD",
            CHECKING_ACCT = "CHECKING",
            SAVINGS_ACCT = "SAVINGS",
            PETNAME = "PETNAME",
            INTERACTIONS = "INTERACTION_SEQUENCE",
            COST = "BASELINE_COST",
            TRANSACTIONS = "TRANSACTIONS",
            LAST_PAYMENT = "LAST_PAYMENT",
            PROGRESS = "PROGRESS",
            GOAL = "GOAL",
            PETS = "PETS_RAISED",
            OWNER_ID = "OWNER_ID",
            PET_ID = "PET_ID",
            USER_ID = "USER_ID";
    public static int GREEN = 0x3D7423;
    public final static int DAYS = 86400000;

    // Hide the sticky action bar
    public static void hideActionBar(AppCompatActivity activity) {
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().hide();
    }

    // Convert @param amount to a String of the form "5.00"
    public static String amountToString(int amount) {
        String intString = Integer.toString(amount);
        String finalCost = "";
        if (intString.length() >= 3) {
            for (int i = 0; i < intString.length(); i++) {
                if (i == intString.length() - 2)
                    finalCost += '.';
                finalCost += intString.charAt(i);
            }
        } else if (intString.length() == 2) {
            finalCost = "0." + intString;
        } else if (intString.length() == 1) {
            finalCost = "0.0" + intString;
        }
        return finalCost;
    }

    public static void makeNotification(int num, Context context) {
        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.eggicon)
                        .setContentTitle("Your pet misses you!")
                        .setColor(Utils.GREEN);

        if (num == 1) {
            mBuilder.setContentText("See how they're doing!");
        } else if (num == 2) {
            mBuilder.setContentText("They're feeling lonely...");
        } else if (num == 3) {
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

    // OVERRIDE activity-wide font to custom font:
    public static void setDefaultFont(Context context,
                                      String staticTypefaceFieldName, String fontAssetName) {
        final Typeface regular = Typeface.createFromAsset(context.getAssets(),
                fontAssetName);
        replaceFont(staticTypefaceFieldName, regular);
    }

    protected static void replaceFont(String staticTypefaceFieldName,
                                      final Typeface newTypeface) {
        try {
            final Field staticField = Typeface.class
                    .getDeclaredField(staticTypefaceFieldName);
            staticField.setAccessible(true);
            staticField.set(null, newTypeface);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
