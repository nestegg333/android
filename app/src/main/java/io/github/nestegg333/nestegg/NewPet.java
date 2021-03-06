package io.github.nestegg333.nestegg;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.util.Date;

import io.github.nestegg333.nestegg.put.UpdateOwner;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // OVERRIDE activity-wide font to custom font:
        Utils.setDefaultFont(this, "MONOSPACE", "fonts/Arciform.ttf");
        setContentView(R.layout.egg_success);

        ImageView celebrateDragon = (ImageView) findViewById(R.id.celebdragon);
        AnimationDrawable animate = ((AnimationDrawable) celebrateDragon.getBackground());
        animate.start();

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
                String goalField = goalEntry.getText().toString();
                String petNameField = petNameEntry.getText().toString();
                if (goalField.equals(""))
                    Toast.makeText(context, "Please enter a valid goal value", Toast.LENGTH_LONG).show();
                else if (petNameField.equals(""))
                    Toast.makeText(context, "Please enter a valid pet name", Toast.LENGTH_LONG).show();
                else if (petNameField.length() > 30)
                    Toast.makeText(context, "Please choose a shorter pet name", Toast.LENGTH_LONG).show();
                else if (!petNameField.matches("^[a-zA-Z0-9]*$"))
                    Toast.makeText(context, "Please only use alphanumeric characters in your pet name", Toast.LENGTH_LONG).show();
                else {
                    goal = Integer.parseInt(goalField);
                    petName = petNameField;
                    updateUser();
                }
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

        Bundle b = intent.getExtras();
        String oldPetName = b.getString(Utils.PETNAME);
        b.putInt(Utils.PROGRESS, 0);
        b.putInt(Utils.GOAL, goal * 100);
        b.putInt(Utils.COST, (int) (goal * 100 / 47.169811321));
        b.putInt(Utils.TRANSACTIONS, 0);
        b.putString(Utils.INTERACTIONS, "R");
        b.putInt(Utils.PETS, b.getInt(Utils.PETS) + 1);
        b.putString(Utils.PETNAME, petName);

        new UpdateOwner(b, null, this, oldPetName);
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
