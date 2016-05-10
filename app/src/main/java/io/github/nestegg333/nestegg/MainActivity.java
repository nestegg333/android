package io.github.nestegg333.nestegg;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Date;

import io.github.nestegg333.nestegg.auth.Logout;
import io.github.nestegg333.nestegg.post.NewPaymentPost;
import io.github.nestegg333.nestegg.services.Alarm;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final static String TAG = "NestEgg";
    private PetState[] states;
    private DrawerLayout drawer;
    private Bundle data;
    private Context CONTEXT;
    private char currentState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Activity and view init:
        super.onCreate(savedInstanceState);
        Utils.setDefaultFont(this, "MONOSPACE", "fonts/Arciform.ttf");
        setContentView(R.layout.activity_main);
        Utils.hideActionBar(this);
        CONTEXT = this;
        // Receive the account information:
        Intent intent = getIntent();
        data = intent.getExtras();

        Alarm.scheduleAlarms(this);

        // Setup navigation drawer
        initNavigationDrawer();

        // Initialize the pet states:
        // TODO displaying the "HI!" no matter what
        NestEgg app = (NestEgg) getApplicationContext();
        states = new PetState[]{
                new PetState("Hi " + app.getUsername() + "!", "Neato!", R.drawable.restinganimate, "Resting"),
                new PetState("Oh no! " + data.getString(Utils.PETNAME) + " is hungry!",
                        "Feed " + data.getString(Utils.PETNAME) + "! - $%m", R.drawable.hungryanimate, "Hungry"),
                new PetState("Uh oh, " + data.getString(Utils.PETNAME) + " looks bored...",
                        "Give " + data.getString(Utils.PETNAME) + " a toy! - $%m", R.drawable.boredanimate, "Bored"),
                new PetState("Ahh! " + data.getString(Utils.PETNAME) + " is sick!",
                        "Take " + data.getString(Utils.PETNAME) + " to the vet! - $%m", R.drawable.sickanimate, "Sick"),
        };

        // Check if its time for an update:
        String lastPaymentString = data.getString(Utils.LAST_PAYMENT);
        if (lastPaymentString == null || lastPaymentString.equals("null")) {
            stateChange(data.getString(Utils.INTERACTIONS).charAt(0));
        } else {
            long lastPay = Date.parse(lastPaymentString);
            long now = (new Date()).getTime();
            if (now - lastPay > 8.28e7) // triggers if haven't made a payment in 23 hours
                stateChange(data.getString(Utils.INTERACTIONS).charAt(data.getInt(Utils.TRANSACTIONS)));
        }
    }

    // Setup navigation drawer
    private void initNavigationDrawer() {
        // Set up menu item click listener:
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Restrict opening/closing actions and fill contents:
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        findViewById(R.id.drawer_opener).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                    ((TextView) findViewById(R.id.drawer_username)).setText(((NestEgg) getApplicationContext()).getUsername());
                    ((TextView) findViewById(R.id.drawer_eggs_raised)).setText("x " + (data.getInt(Utils.PETS) - 1));

                    ImageView eggWiggle = (ImageView) findViewById(R.id.egg_menu);
                    AnimationDrawable egg = ((AnimationDrawable) eggWiggle.getBackground());
                    egg.start();

                    ((TextView) findViewById(R.id.drawer_progress_status)).setText("$"
                            + Utils.amountToString(data.getInt(Utils.PROGRESS)) + " saved");
                    ((TextView) findViewById(R.id.drawer_goal_status)).setText("$"
                            + Utils.amountToString(data.getInt(Utils.GOAL) * 100) + " goal");
                    ((ProgressBar) findViewById(R.id.drawer_progress_bar)).setMax(data.getInt(Utils.GOAL) * 100);
                    ((ProgressBar) findViewById(R.id.drawer_progress_bar)).setProgress(data.getInt(Utils.PROGRESS));
                }
            }
        });
    }

    private void stateChange(char c) {
        currentState = c;
        PetState newState;
        int costFactor = 1;
        switch (c) {
            case 'F':
                findViewById(R.id.no_action).setVisibility(View.GONE);
                findViewById(R.id.action_container).setVisibility(View.VISIBLE);
                newState = states[1];
                break;
            case 'T':
                findViewById(R.id.no_action).setVisibility(View.GONE);
                findViewById(R.id.action_container).setVisibility(View.VISIBLE);
                newState = states[2];
                costFactor = 3;
                break;
            case 'V':
                findViewById(R.id.no_action).setVisibility(View.GONE);
                findViewById(R.id.action_container).setVisibility(View.VISIBLE);
                newState = states[3];
                costFactor = 10;
                break;
            default:
                findViewById(R.id.no_action).setVisibility(View.VISIBLE);
                findViewById(R.id.action_container).setVisibility(View.GONE);
                newState = states[0];
                String usernameString = newState.getTitle().replace("%u", ((NestEgg) getApplicationContext()).getUsername());
                ((TextView) findViewById(R.id.pet_state_title)).setText(usernameString);
                ImageView dragon = (ImageView) findViewById(R.id.pet_state_image);
                dragon.setBackground(getDrawable(newState.getImageId()));
                ((AnimationDrawable) dragon.getBackground()).start();
                return;
        }

        int cost = costFactor * data.getInt(Utils.COST);
        if (data.getInt(Utils.TRANSACTIONS) == 29)
            cost = data.getInt(Utils.GOAL) * 100 - data.getInt(Utils.PROGRESS);
        final int COST = cost;

        ((TextView) findViewById(R.id.pet_state_title)).setText(newState.getTitle());
        ImageView dragon = (ImageView) findViewById(R.id.pet_state_image);
        dragon.setBackground(getDrawable(newState.getImageId()));
        ((AnimationDrawable) dragon.getBackground()).start();

        // Set up the action button:
        Button actionButton = (Button) findViewById(R.id.pet_state_action);
        String actionString = newState.getAction().replace("%m", Utils.amountToString(COST));
        actionButton.setText(actionString);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                issuePayment(COST);
            }
        });
    }

    private void issuePayment(int amount) {
        new NewPaymentPost(amount, data, this);
    }

    public void paymentSuccess(Bundle newData) {
        data.putInt(Utils.TRANSACTIONS, newData.getInt(Utils.TRANSACTIONS));
        data.putInt(Utils.PROGRESS, newData.getInt(Utils.PROGRESS));
        data.putString(Utils.LAST_PAYMENT, newData.getString(Utils.LAST_PAYMENT));

        if (data.getInt(Utils.TRANSACTIONS) >= 30) {
            Intent intent = new Intent(CONTEXT, NewPet.class);
            intent.putExtras(data);
            CONTEXT.startActivity(intent);
        } else
            stateChange('R');
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            // super.onBackPressed(); // this is so we don't go back to the account creation screen
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent;

        if (id == R.id.payment_history) {
            intent = new Intent(this, FullPaymentActivity.class);
            intent.putExtras(data);
            startActivity(intent);
        } else if (id == R.id.user_settings_option) {
            intent = new Intent(this, UserSettingsActivity.class);
            intent.putExtras(data);
            startActivity(intent);
        } else if (id == R.id.logout_option) {
            new Logout(this);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        NestEgg app = (NestEgg) getApplication();
        String usernameString = "Hi " + app.getUsername() + "!";
        if (currentState == 'R')
            ((TextView) findViewById(R.id.pet_state_title)).setText(usernameString);
    }

}
