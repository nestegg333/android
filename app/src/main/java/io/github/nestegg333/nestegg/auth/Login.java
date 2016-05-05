package io.github.nestegg333.nestegg.auth;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import io.github.nestegg333.nestegg.FullPaymentActivity;
import io.github.nestegg333.nestegg.HttpRequest;
import io.github.nestegg333.nestegg.LogInActivity;
import io.github.nestegg333.nestegg.PaymentAdapter;
import io.github.nestegg333.nestegg.Utils;

/**
 * Created by aqeelp on 4/18/16.
 */
public class Login extends AsyncTask<String, Void, String> {
    private final static String TAG = "NestEgg";
    private LogInActivity activity;
    private Bundle userData;

    public Login(Bundle b, LogInActivity l) {
        Log.d(TAG, "Fetching user using username and password");
        activity = l;
        userData = b;

        // trigger neglect:
        if (userData.get(Utils.USERNAME).equals("neglect")) {
            userData.putString(Utils.TOKEN, "a");
            userData.putString(Utils.PETNAME, "James");
            userData.putString(Utils.INTERACTIONS, "RFFFFFTFFFFFTFFTVFFFFFFFTFFFFF");
            userData.putInt(Utils.PETS, 2);
            activity.petFailure(userData);
            return;
        } else if (userData.get(Utils.USERNAME).equals("diligent")) {
            userData.putString(Utils.TOKEN, "a");
            userData.putString(Utils.PETNAME, "Foo");
            userData.putString(Utils.INTERACTIONS, "FFFFFFTFFFFFTFFTVFFFFFFFTFFFFF");
            userData.putInt(Utils.COST, 212);
            userData.putInt(Utils.TRANSACTIONS, 29);
            userData.putString(Utils.LAST_PAYMENT, (new Date()).toString());
            userData.putInt(Utils.PROGRESS, 9780);
            userData.putInt(Utils.GOAL, 10000);
            userData.putInt(Utils.PETS, 2);
            activity.launchMainActivity(userData);
            return;
        }

        this.execute("http://nestegg.herokuapp.com/auth/login/");
    }

    @Override
    protected String doInBackground(String... params) {
        Log.d(TAG, "Do in background - Attempting to log in");
        try {
            JSONObject json = makeLoginJSON();
            if (json == null) activity.finish(); // TODO: crash...

            ByteArrayOutputStream result = new ByteArrayOutputStream();
            HttpRequest.post(params[0])
                    .contentType(HttpRequest.CONTENT_TYPE_JSON)
                    .send(json.toString())
                    .receive(result);

            return result.toString();
        } catch (Exception e) {
            Log.d(TAG, "Do in background - Failed to retrieve properly" + e.toString());
            return null;
        }
    }

    protected void onPostExecute(String data) {
        Log.d(TAG, "On Post Execute - Attempting to create adapter from data");
        Log.d(TAG, data);
        try {
            JSONObject loginReceived = new JSONObject(data);
            // TODO: get/parse owner and pet object

            userData.putString(Utils.TOKEN, loginReceived.getString("auth_token"));
            userData.putString(Utils.PETNAME, "Foo");
            userData.putString(Utils.INTERACTIONS, "FFFTVRTFFFFFTFFFFFFFFFFFTFFFFF");
            userData.putInt(Utils.COST, 212);
            userData.putInt(Utils.TRANSACTIONS, 2);
            userData.putString(Utils.LAST_PAYMENT, (new Date()).toString());
            userData.putInt(Utils.PROGRESS, 424);
            userData.putInt(Utils.GOAL, 10000);
            userData.putInt(Utils.PETS, 2);

            long lastPayment = Date.parse(userData.getString(Utils.LAST_PAYMENT));
            // TODO to trigger neglect: lastPayment -= 4 * Utils.DAYS;

            long now = (new Date()).getTime();
            if (now - lastPayment > 3 * Utils.DAYS) {
                activity.petFailure(userData);
            } else {
                activity.launchMainActivity(userData);
            }
        } catch (JSONException e) {
            Toast.makeText(activity, "Log-in Failed", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private JSONObject makeLoginJSON() {
        JSONObject paymentJSON = new JSONObject();

        try {
            paymentJSON.put("username", userData.get(Utils.USERNAME));
            paymentJSON.put("password", userData.get(Utils.PASSWORD));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return paymentJSON;
    }
}
