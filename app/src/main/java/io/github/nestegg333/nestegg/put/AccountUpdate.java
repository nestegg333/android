package io.github.nestegg333.nestegg.put;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

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
    private Context context;
    private String username;
    private int checkingNo, savingsNo;
    private String updating;

    public AccountUpdate(Context c, Bundle b, String u, int cn, int sn) {
        Log.d(TAG, "Updating owner");
        data = b;
        context = c;

        if (cn == -1 && sn == -1) {
            updating = "username";
            username = u;
            json = makeUserJSON();
            this.execute("http://nestegg.herokuapp.com/api/users/" + data.getInt(Utils.USER_ID) + "/");
        } else if (u == null && sn == -1) {
            updating = "checkingNo";
            checkingNo = cn;
            json = makeOwnerJSON();
            this.execute("http://nestegg.herokuapp.com/api/owners/" + data.getInt(Utils.OWNER_ID) + "/");
        } else if (cn == -1 && u == null) {
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
            SharedPreferences.Editor e = PreferenceManager.getDefaultSharedPreferences(context).edit();
            e.putString("username", username);
            e.apply();
        }
    }

    private JSONObject makeUserJSON() {
        JSONObject userJSON = new JSONObject();

        try {
            NestEgg app = (NestEgg) context.getApplicationContext();
            userJSON.put("username", username);
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
                ownerJSON.put("checkNum", "" + checkingNo);
            } else if (updating.equals("savingsNo")) {
                ownerJSON.put("saveNum", "" + savingsNo);
            }

            return ownerJSON;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
