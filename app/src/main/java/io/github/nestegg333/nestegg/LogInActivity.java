package io.github.nestegg333.nestegg;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.io.FileOutputStream;
import java.util.Date;

import io.github.nestegg333.nestegg.auth.Login;

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

    private final static int LOGIN = 0,
                                BANK = 1,
                                GOAL = 2;
    private int currentScreen;

    // TODO input validation throughout

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // OVERRIDE activity-wide font to custom font:
        Utils.setDefaultFont(this, "MONOSPACE", "fonts/Arciform.ttf");
        context = this;

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET}, 0);
        }

        initAccountInfoEntry();
    }

    private void initAccountInfoEntry() {
        setContentView(R.layout.activity_account);
        currentScreen = LOGIN;
        usernameAddressEntry = (EditText) findViewById(R.id.new_username_address_input);
        passwordEntry = (EditText) findViewById(R.id.new_password_input);

        findViewById(R.id.account_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = usernameAddressEntry.getText().toString();
                password = passwordEntry.getText().toString();
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
        currentScreen = BANK;
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
        currentScreen = GOAL;
        firstGoalEntry = (EditText) findViewById(R.id.new_goal);
        petNameEntry = (EditText) findViewById(R.id.new_pet_name);

        findViewById(R.id.set_first_goal_continue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goal = Integer.parseInt(firstGoalEntry.getText().toString());
                petName = petNameEntry.getText().toString();

                registerUser();
            }
        });
    }

    private void registerUser() {
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

        // TODO: register a new user

        launchMainActivity(fakeBundle());
    }

    private void validate() {
        // FetchUser fetcher = new FetchUser(username, password, this);
        Bundle newUserInfo = new Bundle();
        newUserInfo.putString(Utils.USERNAME, username);
        newUserInfo.putString(Utils.PASSWORD, password);
        newUserInfo.putString(Utils.PETNAME, petName);
        newUserInfo.putInt(Utils.CHECKING_ACCT, checkingAcctNum);
        newUserInfo.putInt(Utils.SAVINGS_ACCT, savingsAcctNum);
        newUserInfo.putInt(Utils.GOAL, goal);
        new Login(newUserInfo, this);
    }

    public void launchMainActivity(Bundle bundle) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private Bundle fakeBundle() {
        Bundle bundle = new Bundle();
        bundle.putString(Utils.TOKEN, "1a2b3c");
        bundle.putString(Utils.USERNAME, username);
        bundle.putString(Utils.PETNAME, petName);
        bundle.putString(Utils.INTERACTIONS, "FFFFFFTFFFFFTFFTVFFFFFFFTFFFFF");
        bundle.putInt(Utils.COST, (int) (goal * 100 / 47.169811321));
        bundle.putInt(Utils.TRANSACTIONS, 0);
        bundle.putString(Utils.LAST_PAYMENT, "");
        bundle.putInt(Utils.PROGRESS, 0);
        bundle.putInt(Utils.GOAL, goal * 100);
        bundle.putInt(Utils.PETS, 0);
        return bundle;
    }

    @Override
    public void onBackPressed() {
        switch(currentScreen) {
            case BANK:
                initAccountInfoEntry();
                return;
            case GOAL:
                initBankInfoEntry();
                return;
            default:
                super.onBackPressed();
        }
    }
}
