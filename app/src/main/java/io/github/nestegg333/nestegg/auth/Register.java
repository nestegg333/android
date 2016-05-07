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
import io.github.nestegg333.nestegg.post.NewOwnerPost;

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
            if (json == null) activity.finish(); // TODO: crash...

            ByteArrayOutputStream result = new ByteArrayOutputStream();
            HttpRequest.post(params[0])
                    .contentType(HttpRequest.CONTENT_TYPE_JSON)
                    .send(json.toString())
                    .receive(result);

            return result.toString();
        } catch (Exception e) {
            Log.d(TAG, "Do in background - Failed to register properly" + e.toString());
            return null;
        }
    }

    protected void onPostExecute(String data) {
        Log.d(TAG, "On Post Execute - Parsing new user's data: " + data);
        try {
            JSONObject loginReceived = new JSONObject(data);

            userData.putInt(Utils.USER_ID, loginReceived.getInt("id"));
            new NewOwnerPost(userData, activity);
            // todo: register owner and pet objects
        } catch (JSONException e) {
            Toast.makeText(activity, "Register Failed", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private JSONObject makeRegisterJSON() {
        JSONObject registerJSON = new JSONObject();

        try {
            NestEgg app = (NestEgg) activity.getApplicationContext();
            registerJSON.put("username", app.getUsername());
            registerJSON.put("password", app.getPassword());
            registerJSON.put("email", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return registerJSON;
    }
}
