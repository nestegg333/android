package io.github.nestegg333.nestegg.fetch;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;

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

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import io.github.nestegg333.nestegg.LogInActivity;
import io.github.nestegg333.nestegg.Utils;

/**
 * Created by aqeelp on 4/18/16.
 */
public class FetchOwner extends AsyncTask<String, Void, String> {
    private final static String TAG = "NestEgg";
    Activity context;
    int userId;

    public FetchOwner(int id, Activity c) {
        Log.d(TAG, "Fetching owner using id");
        context = c;
        userId = id;
        trustEveryone();
    }

    @Override
    protected String doInBackground(String... params) {
        Log.d(TAG, "Do in background - Attempting to get data url " + params[0]);
        try {
            return get(new URL(params[0]));
        } catch (Exception e) {
            Log.d(TAG, "Do in background - Failed to retrieve properly" + e.toString());
            return null;
        }
    }

    protected void onPostExecute(String data) {
        Log.d(TAG, "On Post Execute - Attempting to create bundle from data");
        try {
            InputStream stream = new ByteArrayInputStream(data.getBytes("UTF-8"));
            Bundle bundle = readJsonStream(stream);
            Log.d(TAG, bundle.toString());
            // context.launchMainActivity(bundle);
        } catch (IOException e) {
            Log.d(TAG, "On Post Execute - Failed to parse JSON response properly");
        }
    }

    public Bundle readJsonStream(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            Bundle bundle = readOwner(reader);
            return bundle;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Bundle readOwner(JsonReader reader) throws IOException {
        Bundle ownerInfo = new Bundle();

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("user")) {
                reader.beginObject();
                while (reader.hasNext()) {
                    if (reader.nextName().equals("username"))
                        ownerInfo.putString(Utils.USERNAME, reader.nextString());
                }
                reader.endObject();
            } else if (name.equals("pet")) {
                reader.beginObject();
                while (reader.hasNext()) {
                    if (reader.nextName().equals("name"))
                        ownerInfo.putString(Utils.PETNAME, reader.nextString());
                }
                reader.endObject();
            } else if (name.equals("numPets")) {
                ownerInfo.putInt(Utils.PETS, reader.nextInt());
            } else if (name.equals("goal")) {
                ownerInfo.putInt(Utils.GOAL, reader.nextInt());
            } else if (name.equals("progress")) {
                ownerInfo.putInt(Utils.PROGRESS, reader.nextInt());
            } else if (name.equals("interactionOrder")) {
                ownerInfo.putString(Utils.INTERACTIONS, reader.nextString());
            } else if (name.equals("baseCost")) {
                ownerInfo.putInt(Utils.COST, reader.nextInt());
            } else if (name.equals("numTrans")) {
                ownerInfo.putInt(Utils.TRANSACTIONS, reader.nextInt());
            } else if (name.equals("lastPay")) {
                ownerInfo.putString(Utils.LAST_PAYMENT, reader.nextString());
            }
        }
        reader.endObject();

        return new Bundle();
    }

    private String get(URL url) throws IOException {
        URLConnection urlConnection = url.openConnection();
        InputStream in = new BufferedInputStream(urlConnection.getInputStream());

        try {
            byte[] response = readStream(in);
            return new String(response, "UTF-8");
        } finally {
            in.close();
        }
    }

    private byte[] readStream(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        for (int count; (count = in.read(buffer)) != -1; ) {
            out.write(buffer, 0, count);
        }
        return out.toByteArray();
    }

    private void trustEveryone() {
        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[]{new X509TrustManager(){
                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {}
                public void checkServerTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {}
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }}}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(
                    context.getSocketFactory());
        } catch (Exception e) { // should never happen
            e.printStackTrace();
        }
    }
}
