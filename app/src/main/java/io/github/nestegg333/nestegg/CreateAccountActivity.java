package io.github.nestegg333.nestegg;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

/**
 * Created by aqeelp on 3/29/16.
 */
public class CreateAccountActivity extends AppCompatActivity {
    private EditText emailAddressEntry, passwordEntry, checkingAccountEntry,
            savingsAccountEntry, firstGoalEntry;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        context = this;

        emailAddressEntry = (EditText) findViewById(R.id.new_email_address_input);
        passwordEntry = (EditText) findViewById(R.id.new_password_input);

        findViewById(R.id.create_account_continue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initBankInfoEntry();
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
                // TODO: Bundle something to the intent
                startActivity(intent);
            }
        });
    }
}
