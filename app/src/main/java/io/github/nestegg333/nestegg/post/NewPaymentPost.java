package io.github.nestegg333.nestegg.post;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import io.github.nestegg333.nestegg.HttpRequest;
import io.github.nestegg333.nestegg.MainActivity;
import io.github.nestegg333.nestegg.Utils;

/**
 * Created by aqeelp on 12/28/15.
 */
public class NewPaymentPost extends AsyncTask<String, Void, String> {
    private final static String TAG = "NestEgg";
    private JSONObject json;
    private int amount;
    private Bundle data;
    private MainActivity context;
    private String now;

    public NewPaymentPost(int a, Bundle b, MainActivity c) {
        Log.d(TAG, "Posting new payment");
        amount = a;
        context = c;
        data = b;

        json = makePaymentJSON();
        this.execute("http://nestegg.herokuapp.com/api/payments/");
    }

    protected String doInBackground(String... params) {
        // Issue post request
        try {

            Log.d(TAG, "Issuing payment post request... " + json.toString());

            // POST payment
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            HttpRequest.post(params[0])
                    .contentType(HttpRequest.CONTENT_TYPE_JSON)
                    .send(json.toString())
                    .receive(result);
            Log.d(TAG, "Payment response received: " + result);

            // PUT update owner
            JSONObject response = new JSONObject(result.toString());
            String ownerURL = response.getString("owner");
            result = new ByteArrayOutputStream();
            HttpRequest.put(ownerURL)
                    .contentType(HttpRequest.CONTENT_TYPE_JSON)
                    .send(makeOwnerJSON().toString())
                    .receive(result);
            Log.d(TAG, "Owner update response received: " + result);

            return "y";
        } catch (Exception e) {
            Log.d(TAG, "Exception: " + e.toString());
            Toast.makeText(context, "Error with making payment", Toast.LENGTH_LONG).show();
        }
        return null;
    }

    protected void onPostExecute(String response) {
        Log.d(TAG, "Response received: " + response);

        if (response == null) {
            Toast.makeText(context, "Error with making payment", Toast.LENGTH_LONG).show();
            return;
        }

        data.putInt(Utils.TRANSACTIONS, data.getInt(Utils.TRANSACTIONS) + 1);
        data.putInt(Utils.PROGRESS, data.getInt(Utils.PROGRESS) + amount);
        data.putString(Utils.LAST_PAYMENT, now);
        context.paymentSuccess(data);

        try {
            String writeString = now;
            FileOutputStream outputStream = context.openFileOutput("lastPayment", Context.MODE_PRIVATE);
            outputStream.write(writeString.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JSONObject makePaymentJSON() {
        JSONObject paymentJSON = new JSONObject();

        try {
            paymentJSON.put("owner", "http://nestegg.herokuapp.com/api/owners/" + data.getInt(Utils.OWNER_ID) + "/");
            paymentJSON.put("amount", amount);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return paymentJSON;
    }

    private JSONObject makeOwnerJSON() {
        JSONObject userJSON = new JSONObject();

        try {
            userJSON.put("user", "http://nestegg.herokuapp.com/api/users/" + data.getInt(Utils.USER_ID) + "/");
            userJSON.put("pet", "http://nestegg.herokuapp.com/api/pets/" + data.getInt(Utils.PET_ID) + "/");
            userJSON.put("numTrans", data.getInt(Utils.TRANSACTIONS) + 1);
            userJSON.put("progress", data.getInt(Utils.PROGRESS) + amount);
            now = (new Date()).toString();
            userJSON.put("lastPay", now);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return userJSON;
    }
}
