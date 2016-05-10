package io.github.nestegg333.nestegg.auth;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.Date;

import io.github.nestegg333.nestegg.HttpRequest;
import io.github.nestegg333.nestegg.LogInActivity;
import io.github.nestegg333.nestegg.NestEgg;
import io.github.nestegg333.nestegg.Utils;

/**
 * Created by aqeelp on 4/18/16.
 */
public class Register extends AsyncTask<String, Void, String> {
    private final static String TAG = "NestEgg";
    private LogInActivity activity;
    private Bundle userData;

    public Register(Bundle b, LogInActivity l) {
        Log.d(TAG, "Registering new user");
        activity = l;
        userData = b;

        this.execute("http://nestegg.herokuapp.com/auth/register/");
    }

    @Override
    protected String doInBackground(String... params) {
        Log.d(TAG, "Do in background - Attempting to register new user");
        try {
            JSONObject json = makeRegisterJSON();
            if (json == null) {
                Toast.makeText(activity, "Error with registration", Toast.LENGTH_LONG).show();
                activity.killSpinner(false, true);
                return null;
            }

            // Register user
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            HttpRequest.post(params[0])
                    .contentType(HttpRequest.CONTENT_TYPE_JSON)
                    .send(json.toString())
                    .receive(result);

            Log.d(TAG, "Reponse from registering user: " + result);
            JSONObject resultJSON = new JSONObject(result.toString());
            int userID = resultJSON.getInt("id");
            userData.putInt(Utils.USER_ID, userID);

            // Login
            result = new ByteArrayOutputStream();
            HttpRequest.post("http://nestegg.herokuapp.com/auth/login/")
                    .contentType(HttpRequest.CONTENT_TYPE_JSON)
                    .send(makeLoginJSON().toString())
                    .receive(result);
            Log.d(TAG, "Reponse from logging in: " + result);
            JSONObject loginJSON = new JSONObject(result.toString());
            NestEgg app = (NestEgg) activity.getApplicationContext();
            app.setToken(loginJSON.getString("auth_token"));

            // Register pet
            result = new ByteArrayOutputStream();
            HttpRequest.post("http://nestegg.herokuapp.com/api/pets/")
                    .contentType(HttpRequest.CONTENT_TYPE_JSON)
                    .send(makePetJSON().toString())
                    .receive(result);
            Log.d(TAG, "Reponse from creating pet: " + result);
            JSONObject petJSON = new JSONObject(result.toString());
            int petID = petJSON.getInt("id");
            userData.putInt(Utils.PET_ID, petID);

            // Get owner URL
            result = new ByteArrayOutputStream();
            HttpRequest.get("http://nestegg.herokuapp.com/api/users/" + userID + "/")
                    .receive(result);
            Log.d(TAG, "Reponse from getting user: " + result);
            JSONObject userJSON = new JSONObject(result.toString());
            String ownerURL = userJSON.getString("owner");

            // Update owner object
            result = new ByteArrayOutputStream();
            HttpRequest.put(ownerURL)
                    .contentType(HttpRequest.CONTENT_TYPE_JSON)
                    .send(makeOwnerJSON().toString())
                    .receive(result);
            Log.d(TAG, "Reponse from updating owner: " + result);

            return result.toString();
        } catch (Exception e) {
            Log.d(TAG, "Do in background - Failed to register properly " + e.toString());
            e.printStackTrace();
            return null;
        }
    }

    protected void onPostExecute(String data) {
        Log.d(TAG, "On Post Execute - Parsing new user's data");

        if (data == null) {
            Toast.makeText(activity, "Register Failed", Toast.LENGTH_LONG).show();
            activity.killSpinner(false, true);
            return;
        }

        try {
            JSONObject received = new JSONObject(data);
            userData.putInt(Utils.OWNER_ID, received.getInt("id"));
            userData.putString(Utils.INTERACTIONS, received.getString("interactionOrder"));
            userData.putInt(Utils.COST, received.getInt("baseCost"));
            userData.putString(Utils.LAST_PAYMENT, received.getString("lastPay"));

            activity.launchMainActivity(userData);
        } catch (JSONException e) {
            Toast.makeText(activity, "Register Failed", Toast.LENGTH_LONG).show();
            activity.killSpinner(false, true);
            e.printStackTrace();
        }
    }

    private JSONObject makeOwnerJSON() {
        JSONObject ownerJSON = new JSONObject();

        try {
            ownerJSON.put("user", "http://nestegg.herokuapp.com/api/users/" + userData.getInt(Utils.USER_ID) + "/");
            ownerJSON.put("pet", "http://nestegg.herokuapp.com/api/pets/" + userData.getInt(Utils.PET_ID) + "/");
            ownerJSON.put("goal", userData.getInt(Utils.GOAL));
            ownerJSON.put("checkNum", userData.getString(Utils.CHECKING_ACCT));
            ownerJSON.put("saveNum", userData.getString(Utils.SAVINGS_ACCT));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return ownerJSON;
    }

    private JSONObject makeLoginJSON() {
        JSONObject json = new JSONObject();

        try {
            NestEgg app = (NestEgg) activity.getApplicationContext();
            json.put("username", app.getUsername());
            json.put("password", app.getPassword());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    private JSONObject makePetJSON() {
        JSONObject json = new JSONObject();

        try {
            json.put("name", userData.getString(Utils.PETNAME));
            json.put("active", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    private JSONObject makeRegisterJSON() {
        JSONObject registerJSON = new JSONObject();

        try {
            NestEgg app = (NestEgg) activity.getApplicationContext();
            registerJSON.put("username", app.getUsername());
            registerJSON.put("password", app.getPassword());
            registerJSON.put("email", app.getUsername() + "@gmail.com");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return registerJSON;
    }
}
