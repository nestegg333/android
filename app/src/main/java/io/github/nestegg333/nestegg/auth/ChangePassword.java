package io.github.nestegg333.nestegg.auth;

import android.content.Context;
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
import io.github.nestegg333.nestegg.Utils;

/**
 * Created by aqeelp on 4/18/16.
 */
public class ChangePassword extends AsyncTask<String, Void, String> {
    private final static String TAG = "NestEgg";
    private Context context;
    private Bundle userData;
    private String newPassword;

    public ChangePassword(Bundle b, Context c, String n) {
        Log.d(TAG, "Updating password");
        context = c;
        userData = b;
        newPassword = n;

        this.execute("http://nestegg.herokuapp.com/auth/password/");
    }

    @Override
    protected String doInBackground(String... params) {
        Log.d(TAG, "Do in background - Attempting to change password");
        try {
            JSONObject json = makeNewPassJSON();

            Log.d(TAG, userData.getString(Utils.TOKEN));

            ByteArrayOutputStream result = new ByteArrayOutputStream();
            HttpRequest.post(params[0])
                    .contentType(HttpRequest.CONTENT_TYPE_JSON)
                    .header("Authorization", "Token " + userData.getString(Utils.TOKEN))
                    .send(json.toString())
                    .receive(result);

            return result.toString();
        } catch (Exception e) {
            Log.d(TAG, "Do in background - Failed to retrieve properly" + e.toString());
            return null;
        }
    }

    protected void onPostExecute(String data) {
        Log.d(TAG, "Response: " + data);
    }

    private JSONObject makeNewPassJSON() {
        JSONObject paymentJSON = new JSONObject();

        try {
            paymentJSON.put("current_password", userData.get(Utils.PASSWORD));
            paymentJSON.put("new_password", newPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return paymentJSON;
    }
}
