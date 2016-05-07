package io.github.nestegg333.nestegg.post;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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

/**
 * Created by aqeelp on 12/28/15.
 */
public class NewPaymentPost extends AsyncTask<String, Void, String> {
    private final static String TAG = "NestEgg";
    private JSONObject json;
    private int amount;
    private int ownerID;
    private Context context;

    public NewPaymentPost(int a, int o, Context c) {
        Log.d(TAG, "Posting new payment");
        amount = a;
        ownerID = o;
        context = c;

        json = makePaymentJSON();
        this.execute("http://nestegg.herokuapp.com/api/payments/");
    }

    protected String doInBackground(String... params) {
        // Issue post request
        try {

            Log.d(TAG, "Issuing payment post request... " + json.toString());

            ByteArrayOutputStream result = new ByteArrayOutputStream();
            HttpRequest.post(params[0])
                    .contentType(HttpRequest.CONTENT_TYPE_JSON)
                    .send(json.toString())
                    .receive(result);

            Log.d(TAG, "Completed.");
            return result.toString();

        } catch (HttpRequest.HttpRequestException e) {
            Log.d(TAG, "Exception: " + e.toString());
        }
        return null;
    }

    protected void onPostExecute(String response) {
        Log.d(TAG, "Response received: " + response);

        try {
            String writeString = (new Date()).toString();
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
            paymentJSON.put("owner", "http://nestegg.herokuapp.com/api/owners/" + ownerID + "/");
            paymentJSON.put("amount", amount);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return paymentJSON;
    }
}
