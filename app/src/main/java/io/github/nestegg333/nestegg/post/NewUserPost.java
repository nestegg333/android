package io.github.nestegg333.nestegg.post;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import io.github.nestegg333.nestegg.HttpRequest;
import io.github.nestegg333.nestegg.LogInActivity;

/**
 * Created by aqeelp on 12/28/15.
 */
public class NewUserPost extends AsyncTask<String, Void, String> {
    private final static String TAG = "NestEgg";
    JSONObject json;
    LogInActivity context;

    public NewUserPost(LogInActivity c) {
        Log.d(TAG, "Posting new user");
        context = c;

        trustEveryone();
        // Create the JSON object for this user
        json = makeUserJSON();
        // this.execute()
    }

    protected String doInBackground(String... params) {
        // Issue post request
        try {

            Log.d(TAG, "Issuing user post request...");

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
            JSONObject newUserReceivedJSON = new JSONObject(response);

            context.launchMainActivity(new Bundle());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private JSONObject makeUserJSON() {
        JSONObject userJSON = new JSONObject();

        return null;
    }

    private void trustEveryone() {
        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[]{new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(
                    context.getSocketFactory());
        } catch (Exception e) { // should never happen
            e.printStackTrace();
        }
    }
}
