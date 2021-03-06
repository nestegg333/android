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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.util.Date;

import io.github.nestegg333.nestegg.auth.Login;
import io.github.nestegg333.nestegg.auth.Register;
import io.github.nestegg333.nestegg.put.UpdateOwner;

/**
 * Created by aqeelp on 3/29/16.
 */
public class LogInActivity extends AppCompatActivity {
    private final static String TAG = "NestEgg";
    private EditText usernameAddressEntry, passwordEntry, checkingAccountEntry,
            savingsAccountEntry, goalEntry, petNameEntry;
    private String username, password, petName, checkingAcctNum, savingsAcctNum;
    private int goal;
    private LogInActivity context;

    private final static int LOGIN = 0,
                                BANK = 1,
                                HATCH_NEW = 2,
                                HATCH_FAIL = 3,
                                GOAL = 4,
                                FAIL = 5;
    private int currentScreen;

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
        usernameAddressEntry.setText("");
        passwordEntry = (EditText) findViewById(R.id.new_password_input);
        passwordEntry.setText("");

        // dragon head slides up!
        ImageView dHead = (ImageView) findViewById(R.id.dragonhead);
        Animation animSlide = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slideup);
        dHead.startAnimation(animSlide);

        // textbubble slides up!
        ImageView bubble = (ImageView) findViewById(R.id.speechbubble);
        Animation animSlideBubble = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slideupslow);
        bubble.startAnimation(animSlideBubble);

        // text slides up!
        TextView nesteggText = (TextView)findViewById(R.id.nesteggtext);
        Animation animSlideTxt = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slideupslow);
        nesteggText.startAnimation(animSlideTxt);

        findViewById(R.id.account_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = usernameAddressEntry.getText().toString();
                password = passwordEntry.getText().toString();
                // TODO should really make sure that this username doesn't exist already
                if (username.equals(""))
                    Toast.makeText(context, "Please enter a username", Toast.LENGTH_LONG).show();
                else if (password.equals(""))
                    Toast.makeText(context, "Please enter a password", Toast.LENGTH_LONG).show();
                else if (username.length() > 30)
                    Toast.makeText(context, "Please choose a shorter username", Toast.LENGTH_LONG).show();
                else if (!username.matches("^[a-zA-Z0-9]*$"))
                    Toast.makeText(context, "Please only use alphanumeric characters in your username", Toast.LENGTH_LONG).show();
                else
                    initBankInfoEntry();
            }
        });

        findViewById(R.id.account_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = usernameAddressEntry.getText().toString();
                password = passwordEntry.getText().toString();
                if (username.equals(""))
                    Toast.makeText(context, "Please enter a username", Toast.LENGTH_LONG).show();
                else if (password.equals(""))
                    Toast.makeText(context, "Please enter a password", Toast.LENGTH_LONG).show();
                else if (username.length() > 30)
                    Toast.makeText(context, "Your username must be shorter", Toast.LENGTH_LONG).show();
                else if (!username.matches("^[a-zA-Z0-9]*$"))
                    Toast.makeText(context, "Your username should only contain alphanumeric characters", Toast.LENGTH_LONG).show();
                else {
                    findViewById(R.id.login_buttons).setVisibility(View.GONE);
                    findViewById(R.id.login_spinner).setVisibility(View.VISIBLE);
                    validate();
                }
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
                String checkingAcctField = checkingAccountEntry.getText().toString();
                String savingsAcctField = savingsAccountEntry.getText().toString();
                if (checkingAcctField.equals("") || savingsAcctField.equals(""))
                    Toast.makeText(context, "Please enter a bank account number", Toast.LENGTH_LONG).show();
                else if (!checkingAcctField.matches("^[0-9]*$") || !savingsAcctField.matches("^[0-9]*$"))
                    Toast.makeText(context, "Please enter a valid bank account number", Toast.LENGTH_LONG).show();
                else {
                    checkingAcctNum = checkingAcctField;
                    savingsAcctNum = savingsAcctField;
                    hatchNewEgg();
                }
            }
        });
    }

    private void hatchNewEgg() {
        // Do something with the values of @emailAddressEntry and @passwordEntry
        setContentView(R.layout.egg_hatch);
        currentScreen = HATCH_NEW;

        ImageView eggRock = (ImageView) findViewById(R.id.egg_hatch_image);
        AnimationDrawable animate = ((AnimationDrawable) eggRock.getBackground());
        animate.start();

        findViewById(R.id.egg_hatch_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initFirstGoalEntry();
            }
        });
    }

    private void hatchEggAfterFailure(final Bundle bundle) {
        // Do something with the values of @emailAddressEntry and @passwordEntry
        setContentView(R.layout.egg_hatch);
        currentScreen = HATCH_FAIL;

        findViewById(R.id.egg_hatch_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newGoalEntry(bundle);
            }
        });
    }

    private void initFirstGoalEntry() {
        // Do something with the values of checkingAccountEntry and savingsAccountEntry
        setContentView(R.layout.activity_set_first_goal);
        currentScreen = GOAL;
        goalEntry = (EditText) findViewById(R.id.new_goal);
        petNameEntry = (EditText) findViewById(R.id.new_pet_name);

        findViewById(R.id.set_first_goal_continue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String goalField = goalEntry.getText().toString();
                String petNameField = petNameEntry.getText().toString();
                if (goalField.equals(""))
                    Toast.makeText(context, "Please enter a goal value", Toast.LENGTH_LONG).show();
                else if (!goalField.matches("^[0-9]*$"))
                    Toast.makeText(context, "Please enter a valid goal value (in whole dollars)", Toast.LENGTH_LONG).show();
                else if (petNameField.equals(""))
                    Toast.makeText(context, "Please enter a pet name", Toast.LENGTH_LONG).show();
                else if (petNameField.length() > 30)
                    Toast.makeText(context, "Please choose a shorter pet name", Toast.LENGTH_LONG).show();
                else if (!petNameField.matches("^[a-zA-Z0-9]*$"))
                    Toast.makeText(context, "Please only use alphanumeric characters in your pet name", Toast.LENGTH_LONG).show();
                else {
                    goal = Integer.parseInt(goalEntry.getText().toString());
                    petName = petNameEntry.getText().toString();
                    findViewById(R.id.register_button_layout).setVisibility(View.GONE);
                    findViewById(R.id.register_spinner).setVisibility(View.VISIBLE);
                    registerUser();
                }
            }
        });
    }

    private void newGoalEntry(final Bundle bundle) {
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
                    Toast.makeText(context, "Please enter a goal value", Toast.LENGTH_LONG).show();
                else if (!goalField.matches("^[0-9.]*$"))
                    Toast.makeText(context, "Please enter a valid goal value", Toast.LENGTH_LONG).show();
                else if (petNameField.equals(""))
                    Toast.makeText(context, "Please enter a pet name", Toast.LENGTH_LONG).show();
                else if (petNameField.length() > 30)
                    Toast.makeText(context, "Please choose a shorter pet name", Toast.LENGTH_LONG).show();
                else if (!petNameField.matches("^[a-zA-Z0-9]*$"))
                    Toast.makeText(context, "Please only use alphanumeric characters in your pet name", Toast.LENGTH_LONG).show();
                else {
                    goal = Integer.parseInt(goalField);
                    petName = petNameField;
                    findViewById(R.id.register_button_layout).setVisibility(View.GONE);
                    findViewById(R.id.register_spinner).setVisibility(View.VISIBLE);
                    updateUser(bundle);
                }
            }
        });
    }

    public void petFailure(final Bundle bundle) {
        setContentView(R.layout.pet_failure);
        ((TextView) findViewById(R.id.pet_failure_header)).setText("No!! " + bundle.getString(Utils.PETNAME) + " ran away!!");

        ImageView runningAway = (ImageView) findViewById(R.id.runningawaydragon);
        AnimationDrawable animate = (AnimationDrawable) runningAway.getBackground();
        animate.start();

        findViewById(R.id.pet_failure_continue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newGoalEntry(bundle);
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
        } finally {
            new Register(makeBundle(), this);
        }
    }

    private void updateUser(Bundle b) {
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

        String oldPetName = b.getString(Utils.PETNAME);
        b.putInt(Utils.PROGRESS, 0);
        b.putInt(Utils.GOAL, goal);
        b.putInt(Utils.COST, (int) (goal * 100 / 47.169811321));
        b.putInt(Utils.TRANSACTIONS, 0);
        b.putInt(Utils.PETS, b.getInt(Utils.PETS) + 1);
        b.putString(Utils.PETNAME, petName);

        new UpdateOwner(b, this, null, oldPetName);
    }

    private void validate() {
        Bundle fakeBundle = fakeBundle();

        ((NestEgg) getApplicationContext()).setUsername(username);
        ((NestEgg) getApplicationContext()).setPassword(password);
        ((NestEgg) getApplicationContext()).setToken("1a2b3c");

        new Login(this);
    }

    public void launchMainActivity(Bundle bundle) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
        this.finish();
    }

    private Bundle fakeBundle() {
        Bundle bundle = new Bundle();
        bundle.putString(Utils.PETNAME, "petname");
        bundle.putString(Utils.INTERACTIONS, "RTVRFFFFFFFFFFTFFTFFFFFFFFTFFFFF");
        bundle.putInt(Utils.COST, (int) (goal * 100 / 47.169811321));
        bundle.putInt(Utils.TRANSACTIONS, 0);
        bundle.putString(Utils.LAST_PAYMENT, "");
        bundle.putInt(Utils.PROGRESS, 0);
        bundle.putInt(Utils.GOAL, goal * 100);
        bundle.putInt(Utils.PETS, 0);
        return bundle;
    }

    private Bundle makeBundle() {
        Bundle userInfo = new Bundle();
        userInfo.putString(Utils.PETNAME, petName);
        userInfo.putString(Utils.CHECKING_ACCT, checkingAcctNum);
        userInfo.putString(Utils.SAVINGS_ACCT, savingsAcctNum);
        userInfo.putInt(Utils.GOAL, goal);

        ((NestEgg) getApplication()).setUsername(username);
        ((NestEgg) getApplication()).setPassword(password);

        return userInfo;
    }

    public void killSpinner(boolean onLoginScreen, boolean onRegisterScreen) {
        if (onLoginScreen) {
            findViewById(R.id.login_buttons).setVisibility(View.VISIBLE);
            findViewById(R.id.login_spinner).setVisibility(View.GONE);
        }

        if (onRegisterScreen) {
            findViewById(R.id.register_button_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.register_spinner).setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        switch(currentScreen) {
            case BANK:
                initAccountInfoEntry();
                return;
            case GOAL:
                hatchNewEgg();
                return;
            case FAIL:
                return;
            case HATCH_NEW:
                initBankInfoEntry();
                return;
            case HATCH_FAIL:
                return;
            default:
                super.onBackPressed();
        }
    }
}
