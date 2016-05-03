package io.github.nestegg333.nestegg;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.FileOutputStream;
import java.util.Date;

import io.github.nestegg333.nestegg.auth.Login;

/**
 * Created by aqeelp on 3/29/16.
 */
public class NewPet extends AppCompatActivity {
    private final static String TAG = "NestEgg";
    private EditText petNameEntry, goalEntry;
    private String petName;
    private int goal;
    private NewPet context;
    private Intent intent;

    // TODO input validation throughout

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // OVERRIDE activity-wide font to custom font:
        Utils.setDefaultFont(this, "MONOSPACE", "fonts/Arciform.ttf");
        setContentView(R.layout.egg_success);
        context = this;
        intent = getIntent();

        // TODO: at this point it would be a good idea to update the user with a null goal,
        // and then come back to this screen if they reopn the app before declaring a new goal

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET}, 0);
        }

        findViewById(R.id.egg_success_continue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goalEntry();
            }
        });
    }

    private void goalEntry() {
        // Do something with the values of checkingAccountEntry and savingsAccountEntry
        setContentView(R.layout.activity_set_first_goal);
        //Utils.hideActionBar(this);
        ((TextView) findViewById(R.id.set_goal_header)).setText("NEW GOAL!");
        goalEntry = (EditText) findViewById(R.id.new_goal);
        petNameEntry = (EditText) findViewById(R.id.new_pet_name);

        findViewById(R.id.set_first_goal_continue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goal = Integer.parseInt(goalEntry.getText().toString());
                petName = petNameEntry.getText().toString();

                updateUser();
            }
        });
    }

    private void updateUser() {
        // Faux last payment date for notification
        try {
            String writeString = (new Date()).toString();
            Log.d(TAG, "Writing recent payment: " + writeString);
            FileOutputStream outputStream = context.openFileOutput("lastPayment", Context.MODE_PRIVATE);
            outputStream.write(writeString.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // TODO: PUT request update new user
        Bundle b = intent.getExtras();
        b.putInt(Utils.PROGRESS, 0);
        b.putInt(Utils.GOAL, goal * 100);
        b.putInt(Utils.COST, (int) (goal * 100 / 47.169811321));
        b.putInt(Utils.TRANSACTIONS, 0);
        b.putInt(Utils.PETS, b.getInt(Utils.PETS) + 1);
        b.putString(Utils.PETNAME, petName);

        launchMainActivity(b);
    }

    public void launchMainActivity(Bundle bundle) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        // do nothing
    }
}
