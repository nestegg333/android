package io.github.nestegg333.nestegg;

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

/**
 * Created by aqeelp on 4/18/16.
 */
public class FetchUser extends AsyncTask<String, Void, String> {
    private final static String TAG = "NestEgg";
    LogInActivity context;

    public FetchUser(String username, String password, LogInActivity c) {
        Log.v(TAG, "Fetching user using username and password");
        context = c;
        trustEveryone();
    }

    @Override
    protected String doInBackground(String... params) {
        Log.v(TAG, "Do in background - Attempting to get data url " + params[0]);
        try {
            return get(new URL(params[0]));
        } catch (Exception e) {
            Log.v(TAG, "Do in background - Failed to retrieve properly" + e.toString());
            return null;
        }
    }

    protected void onPostExecute(String data) {
        Log.v(TAG, "On Post Execute - Attempting to create bundle from data");
        Log.v(TAG, data);
        try {
            InputStream stream = new ByteArrayInputStream(data.getBytes("UTF-8"));
            Bundle bundle = readJsonStream(stream);
            context.launchMainActivity(bundle);
        } catch (IOException e) {
            Log.v(TAG, "On Post Execute - Failed to parse JSON response properly");
        }
    }

    public Bundle readJsonStream(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            Bundle bundle = readUser(reader);
            return bundle;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Bundle readUser(JsonReader reader) throws IOException {
        int pinId = -1;
        int userId = -1;
        String pinType = null;
        String title = null;
        double latitude = 0.0;
        double longitude = 0.0;
        String dateCreated = null;
        String dateModified = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("pinId")) {
                pinId = reader.nextInt();
            } else if (name.equals("userId")) {
                userId = reader.nextInt();
            } else if (name.equals("pinType")) {
                pinType = reader.nextString();
            } else if (name.equals("title")) {
                title = reader.nextString();
            } else if (name.equals("latitude")) {
                latitude = reader.nextDouble();
            } else if (name.equals("longitude")) {
                longitude = reader.nextDouble();
            } else if (name.equals("dateCreated")) {
                dateCreated = reader.nextString();
            } else if (name.equals("dateModified")) {
                dateModified = reader.nextString();
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
