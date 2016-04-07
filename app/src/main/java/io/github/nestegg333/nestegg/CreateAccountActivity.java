package io.github.nestegg333.nestegg;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.io.IOException;

/**
 * Created by aqeelp on 3/29/16.
 */
public class CreateAccountActivity extends AppCompatActivity {
    private final static String TAG = "NestEgg";
    private EditText usernameAddressEntry, passwordEntry, checkingAccountEntry,
            savingsAccountEntry, firstGoalEntry;
    private String username;
    private int goal, progress, eggs_raised;
    private Context context;

    // TODO input validation throughout

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // OVERRIDE activity-wide font to custom font:
        FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/Arciform.ttf");
        setContentView(R.layout.activity_account);
        context = this;

        usernameAddressEntry = (EditText) findViewById(R.id.new_username_address_input);
        passwordEntry = (EditText) findViewById(R.id.new_password_input);

        findViewById(R.id.account_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = usernameAddressEntry.getText().toString();
                initBankInfoEntry();
            }
        });

        findViewById(R.id.account_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = usernameAddressEntry.getText().toString();
                validate(passwordEntry.getText().toString());
            }
        });
    }

    private void initBankInfoEntry() {
        // Do something with the values of @emailAddressEntry and @passwordEntry
        setContentView(R.layout.activity_enter_bank_info);
        checkingAccountEntry = (EditText) findViewById(R.id.new_checking_account_number);
        savingsAccountEntry = (EditText) findViewById(R.id.new_savings_account_number);

        findViewById(R.id.enter_bank_info_continue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initFirstGoalEntry();
            }
        });
    }

    private void initFirstGoalEntry() {
        // Do something with the values of @checkingAccountEntry and @savingsAccountEntry
        setContentView(R.layout.activity_set_first_goal);
        firstGoalEntry = (EditText) findViewById(R.id.new_goal);

        findViewById(R.id.set_first_goal_continue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Do something with the value of @firstGoalEntry
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("USERNAME", username);
                intent.putExtra("PETNAME", "Jethro");
                intent.putExtra("PROGRESS", 0);
                intent.putExtra("GOAL", Integer.parseInt(firstGoalEntry.getText().toString()));
                intent.putExtra("EGGS_RAISED", 0);
                startActivity(intent);
            }
        });
    }

    private void validate(String password) {
        // TODO: Issue some request to log in
        // TODO: Request should return USERNAME, PROGRESS, GOAL, EGGS RAISED

        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("USERNAME", username);
        intent.putExtra("PETNAME", "Jethro");
        intent.putExtra("PROGRESS", 50);
        intent.putExtra("GOAL", 100);
        intent.putExtra("EGGS_RAISED", 5);
        startActivity(intent);
    }
}
