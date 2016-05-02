package io.github.nestegg333.nestegg.post;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

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
import io.github.nestegg333.nestegg.MainActivity;
import io.github.nestegg333.nestegg.Utils;

/**
 * Created by aqeelp on 12/28/15.
 */
public class NewOwnerPost {
    private final static String TAG = "NestEgg";
    private Bundle data;
    private int petid;
    private LogInActivity activity;

    public NewOwnerPost(Bundle b, LogInActivity l) {
        Log.d(TAG, "Posting new payment");
        data = b;
        activity = l;

        (new PetPost()).execute("http://nestegg.herokuapp.com/api/pets/");
    }

    private class OwnerPost extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... params) {
            // Issue post request
            try {
                JSONObject json = makeOwnerJSON();
                if (json == null) return "";
                Log.d(TAG, "Issuing owner post request... " + json.toString());

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
                JSONObject responseJSON = new JSONObject(response);
                Bundle ownerData = new Bundle();
                ownerData.putAll(data);
                ownerData.putInt(Utils.OWNER_ID, responseJSON.getInt("id"));
                ownerData.putInt(Utils.PETS, 1);
                ownerData.putInt(Utils.PET_ID, petid);
                ownerData.putInt(Utils.GOAL, data.getInt(Utils.GOAL));
                ownerData.putInt(Utils.PROGRESS, 0);
                ownerData.putString(Utils.INTERACTIONS, responseJSON.getString("interactionOrder"));
                ownerData.putInt(Utils.COST, responseJSON.getInt("baseCost"));
                ownerData.putInt(Utils.TRANSACTIONS, 0);

                String[] userURL = responseJSON.getString("user").split("/");
                int userID = Integer.parseInt(userURL[userURL.length - 2]);
                ownerData.putInt(Utils.USER_ID, userID);

                activity.launchMainActivity(ownerData);
            } catch (JSONException e) {
                Toast.makeText(activity, "Error with request", Toast.LENGTH_LONG);
                e.printStackTrace();
            }
        }

        private JSONObject makeOwnerJSON() {
            JSONObject ownerJSON = new JSONObject();

            try {
                ownerJSON.put("user", "http://nestegg.herokuapp.com/api/users/" + data.getInt("userID") + "/");
                ownerJSON.put("pet", "http://nestegg.herokuapp.com/api/pets/" + petid + "/");
                ownerJSON.put("goal", data.getInt(Utils.GOAL));
                ownerJSON.put("checkNum", data.getInt(Utils.CHECKING_ACCT));
                ownerJSON.put("saveNum", data.getInt(Utils.SAVINGS_ACCT));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return ownerJSON;
        }
    }

    private class PetPost extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... params) {
            // Issue post request
            try {
                JSONObject json = makePetJSON();
                if (json == null) return "";
                Log.d(TAG, "Issuing pet post request... " + json.toString());

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
                JSONObject responseJSON = new JSONObject(response);
                petid = responseJSON.getInt("id");
                (new OwnerPost()).execute("http://nestegg.herokuapp.com/api/owners/");
            } catch (JSONException e) {
                Toast.makeText(activity, "Error with request", Toast.LENGTH_LONG);
                e.printStackTrace();
            }
        }

        private JSONObject makePetJSON() {
            JSONObject petJSON = new JSONObject();

            try {
                petJSON.put("name", data.get(Utils.PETNAME));
                petJSON.put("active", true);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return petJSON;
        }
    }
}
