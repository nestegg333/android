package io.github.nestegg333.nestegg;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import java.util.Date;

import io.github.nestegg333.nestegg.fetch.FetchUser;
import io.github.nestegg333.nestegg.post.NewUserPost;

/**
 * Created by aqeelp on 3/29/16.
 */
public class LogInActivity extends AppCompatActivity {
    private final static String TAG = "NestEgg";
    private EditText usernameAddressEntry, passwordEntry, checkingAccountEntry,
            savingsAccountEntry, firstGoalEntry, petNameEntry;
    private String username, password, petName;
    private int goal, checkingAcctNum, savingsAcctNum;
    private LogInActivity context;

    // TODO input validation throughout

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // OVERRIDE activity-wide font to custom font:
        Utils.setDefaultFont(this, "MONOSPACE", "fonts/Arciform.ttf");
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
                password = passwordEntry.getText().toString();
                validate();
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
                checkingAcctNum = Integer.parseInt(checkingAccountEntry.getText().toString());
                savingsAcctNum = Integer.parseInt(savingsAccountEntry.getText().toString());
                initFirstGoalEntry();
            }
        });
    }

    private void initFirstGoalEntry() {
        // Do something with the values of checkingAccountEntry and savingsAccountEntry
        setContentView(R.layout.activity_set_first_goal);
        firstGoalEntry = (EditText) findViewById(R.id.new_goal);
        petNameEntry = (EditText) findViewById(R.id.new_pet_name);

        findViewById(R.id.set_first_goal_continue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goal = Integer.parseInt(firstGoalEntry.getText().toString());
                petName = petNameEntry.getText().toString();
                // TODO: include all other data in bundle/datamap
                Bundle newUserInfo = new Bundle();
                newUserInfo.putString(Utils.USERNAME, username);
                newUserInfo.putString(Utils.PASSWORD, password);
                newUserInfo.putString(Utils.PETNAME, petName);
                newUserInfo.putInt(Utils.CHECKING_ACCT, checkingAcctNum);
                newUserInfo.putInt(Utils.SAVINGS_ACCT, savingsAcctNum);
                newUserInfo.putInt(Utils.GOAL, goal);
                new NewUserPost(context);
            }
        });
    }

    private void validate() {
        FetchUser fetcher = new FetchUser(username, password, this);
        launchMainActivity(new Bundle());
    }

    public void launchMainActivity(Bundle bundle) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtras(bundle);
        // TODO startActivity(intent);
        startActivity(fakeIntent());
    }

    private Intent fakeIntent() {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(Utils.TOKEN, "1a2b3c");
        intent.putExtra(Utils.USERNAME, username);
        intent.putExtra(Utils.PETNAME, "Jimanji");
        intent.putExtra(Utils.INTERACTIONS, "FTVFFR");
        intent.putExtra(Utils.COST, 500);
        intent.putExtra(Utils.TRANSACTIONS, 0);
        intent.putExtra(Utils.LAST_PAYMENT, (new Date()).toString());
        intent.putExtra(Utils.PROGRESS, 50);
        intent.putExtra(Utils.GOAL, 100);
        intent.putExtra(Utils.PETS, 5);
        return intent;
    }
}
