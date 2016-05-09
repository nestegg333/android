package io.github.nestegg333.nestegg.put;

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
import io.github.nestegg333.nestegg.NewPet;
import io.github.nestegg333.nestegg.Utils;

/**
 * Created by aqeelp on 12/28/15.
 */
public class UpdateOwner {
    private final static String TAG = "NestEgg";
    private JSONObject json;
    private Bundle data;
    private String newPetName;
    private LogInActivity logInActivity;
    private NewPet newPetActivity;

    public UpdateOwner(Bundle b, LogInActivity l, NewPet np, String n) {
        Log.d(TAG, "Updating owner");
        data = b;
        logInActivity = l;
        newPetActivity = np;
        newPetName = n;

        (new PetPut()).execute("http://nestegg.herokuapp.com/api/pets/" + data.getInt(Utils.PET_ID) + "/");
        (new PetPost()).execute("http://nestegg.herokuapp.com/api/pets/");
    }

    private class OwnerPut extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... params) {
            // Issue post request
            try {
                JSONObject json = makeOwnerJSON();
                Log.d(TAG, "Issuing owner put request...");

                ByteArrayOutputStream result = new ByteArrayOutputStream();
                HttpRequest.put(params[0])
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

            if (logInActivity != null)
                logInActivity.launchMainActivity(data);
            else
                newPetActivity.launchMainActivity(data);
        }

        private JSONObject makeOwnerJSON() {
            JSONObject ownerJSON = new JSONObject();

            try {
                ownerJSON.put("user", "http://nestegg.herokuapp.com/api/users/" + data.getInt(Utils.USER_ID) + "/");
                ownerJSON.put("pet", "http://nestegg.herokuapp.com/api/pets/" + data.getInt(Utils.PET_ID) + "/");
                ownerJSON.put("goal", data.getInt(Utils.GOAL));
                ownerJSON.put("progress", data.getInt(Utils.PROGRESS));
                ownerJSON.put("pets", data.getInt(Utils.PETS));
                ownerJSON.put("baseCost", data.getInt(Utils.COST));

                return ownerJSON;
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
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
                data.putInt(Utils.PET_ID, responseJSON.getInt("id"));
                (new OwnerPut()).execute("http://nestegg.herokuapp.com/api/owners/"
                        + data.getInt(Utils.OWNER_ID) + "/");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private JSONObject makePetJSON() {
            JSONObject petJSON = new JSONObject();

            try {
                petJSON.put("name", newPetName);
                petJSON.put("active", true);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return petJSON;
        }
    }

    private class PetPut extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... params) {
            // Issue post request
            try {
                JSONObject json = makePetJSON();
                if (json == null) return "";
                Log.d(TAG, "Issuing pet put request... " + json.toString());

                ByteArrayOutputStream result = new ByteArrayOutputStream();
                HttpRequest.put(params[0])
                        .contentType(HttpRequest.CONTENT_TYPE_JSON)
                        .send(json.toString());

                return result.toString();

            } catch (HttpRequest.HttpRequestException e) {
                Log.d(TAG, "Pet put - exception: " + e.toString());
            }
            return null;
        }

        private JSONObject makePetJSON() {
            JSONObject petJSON = new JSONObject();

            try {
                petJSON.put("name", data.get(Utils.PETNAME));
                petJSON.put("active", false);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return petJSON;
        }
    }
}
