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
import java.util.ArrayList;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

/**
 * Created by aqeelp on 4/18/16.
 */
public class FetchAllPayments extends AsyncTask<String, Void, String> {
    private final static String TAG = "NestEgg";
    private FullPaymentActivity context;
    private int userID;

    public FetchAllPayments(int uid, FullPaymentActivity c) {
        Log.d(TAG, "Fetching user using username and password");
        context = c;
        userID = uid;
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
        Log.d(TAG, "On Post Execute - Attempting to create adapter from data");
        Log.d(TAG, data);
        try {
            InputStream stream = new ByteArrayInputStream(data.getBytes("UTF-8"));
            ArrayList<PaymentAdapter.Payment> payments = readJsonStream(stream);
            PaymentAdapter paymentAdapter = new PaymentAdapter(context, payments);
            context.setAdapter(paymentAdapter);
        } catch (IOException e) {
            Log.d(TAG, "On Post Execute - Failed to parse JSON response properly");
        }
    }

    public ArrayList<PaymentAdapter.Payment> readJsonStream(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));

        ArrayList<PaymentAdapter.Payment> payments = new ArrayList<>();

        reader.beginArray();
        while (reader.hasNext()) {
            PaymentAdapter.Payment payment = readPayment(reader);
            payments.add(payment);
        }
        reader.endArray();
        return payments;
    }

    public PaymentAdapter.Payment readPayment(JsonReader reader) throws IOException {
        String date = null;
        int amount = 0;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("owner")) {
                reader.beginObject();
                while (reader.hasNext()) { }
                reader.endObject();
            } else if (name.equals("date")) {
                date = reader.nextString();
            } else if (name.equals("amount")) {
                amount = reader.nextInt();
            }
        }
        reader.endObject();

        return new PaymentAdapter.Payment(date, Utils.amountToString(amount));
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
