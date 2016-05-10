package io.github.nestegg333.nestegg.put;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

import io.github.nestegg333.nestegg.HttpRequest;
import io.github.nestegg333.nestegg.LogInActivity;
import io.github.nestegg333.nestegg.NestEgg;
import io.github.nestegg333.nestegg.NewPet;
import io.github.nestegg333.nestegg.Utils;

/**
 * Created by aqeelp on 12/28/15.
 */
public class AccountUpdate extends AsyncTask<String, Void, String> {
    private final static String TAG = "NestEgg";
    private JSONObject json;
    private Bundle data;
    private Activity context;
    private String username;
    private String updating, checkingNo, savingsNo;

    public AccountUpdate(Activity c, Bundle b, String u, String cn, String sn) {
        Log.d(TAG, "Updating owner");
        data = b;
        context = c;

        if (cn == null && sn == null && u != null) {
            updating = "username";
            if (u == null) return;
            username = u;
            json = makeUserJSON();
            this.execute("http://nestegg.herokuapp.com/api/users/" + data.getInt(Utils.USER_ID) + "/");
        } else if (u == null && sn == null && cn != null) {
            updating = "checkingNo";
            checkingNo = cn;
            json = makeOwnerJSON();
            this.execute("http://nestegg.herokuapp.com/api/owners/" + data.getInt(Utils.OWNER_ID) + "/");
        } else if (cn == null && u == null && sn != null) {
            updating = "savingsNo";
            savingsNo = sn;
            json = makeOwnerJSON();
            this.execute("http://nestegg.herokuapp.com/api/owners/" + data.getInt(Utils.OWNER_ID) + "/");
        }
    }

    protected String doInBackground(String... params) {
        // Issue post request
        try {

            JSONObject json;
            if (params[0].contains("users")) {
                json = makeUserJSON();
            } else {
                json = makeOwnerJSON();
            }
            Log.d(TAG, "Issuing put request...");

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

        if (updating.equals("username")) {
            try {
                JSONObject responseJSON = new JSONObject(response);
                if (responseJSON.has("email")) {
                    NestEgg app = (NestEgg) context.getApplicationContext();
                    Log.d(TAG, "username: " + username);
                    Toast.makeText(context, "Username updated to " + username, Toast.LENGTH_SHORT).show();
                    app.setUsername(username);
                } else {
                    Toast.makeText(context, "Username change failed: " + responseJSON.get("username"), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                JSONObject responseJSON = new JSONObject(response);
                if (responseJSON.has("id"))
                    Toast.makeText(context, "Successfully updated", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private JSONObject makeUserJSON() {
        JSONObject userJSON = new JSONObject();

        try {
            NestEgg app = (NestEgg) context.getApplicationContext();
            userJSON.put("username", username);
            userJSON.put("email", username + "@gmail.com");
            userJSON.put("password", app.getPassword());
            userJSON.put("owner", "http://nestegg.herokuapp.com/api/owners/" + data.getInt(Utils.OWNER_ID) + "/");

            return userJSON;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    private JSONObject makeOwnerJSON() {
        JSONObject ownerJSON = new JSONObject();

        try {
            ownerJSON.put("user", "http://nestegg.herokuapp.com/api/users/" + data.getInt(Utils.USER_ID) + "/");
            ownerJSON.put("pet", "http://nestegg.herokuapp.com/api/pets/" + data.getInt(Utils.PET_ID) + "/");
            if (updating.equals("checkingNo")) {
                ownerJSON.put("checkNum", checkingNo);
            } else if (updating.equals("savingsNo")) {
                ownerJSON.put("saveNum", savingsNo);
            }

            return ownerJSON;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
