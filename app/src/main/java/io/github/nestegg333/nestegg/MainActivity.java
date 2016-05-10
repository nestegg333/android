package io.github.nestegg333.nestegg;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
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

import io.github.nestegg333.nestegg.auth.Logout;
import io.github.nestegg333.nestegg.post.NewPaymentPost;
import io.github.nestegg333.nestegg.services.Alarm;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final static String TAG = "NestEgg";
    private PetState[] states;
    private DrawerLayout drawer;
    private int goalTotal, goalProgress, eggsRaised, baselineCost, transactionsMade, ownerID;
    private String petname, interactionSequence, lastPaymentDate;
    private Intent userData;
    private Context CONTEXT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Activity and view init:
        super.onCreate(savedInstanceState);
        Utils.setDefaultFont(this, "MONOSPACE", "fonts/Arciform.ttf");
        setContentView(R.layout.activity_main);
        Utils.hideActionBar(this);
        CONTEXT = this;
        // Receive the account information:
        userData = getIntent();
        parseIntent(userData);

        Alarm.scheduleAlarms(this);

        // Setup navigation drawer
        initNavigationDrawer();

        // Initialize the pet states:
        states = new PetState[] {
                new PetState("Hi %u!", "Neato!", R.drawable.restinganimate, "Resting"),
                new PetState("Oh no! " + petname + " is hungry!",
                        "Feed " + petname + "! - $%m", R.drawable.hungryanimate, "Hungry"),
                new PetState("Uh oh, " + petname + " looks bored...",
                        "Give " + petname + " a toy! - $%m", R.drawable.boredanimate, "Bored"),
                new PetState("Ahh! " + petname + " is sick!",
                        "Take " + petname + " to the vet! - $%m", R.drawable.sickanimate, "Sick"),
        };

        // TODO: compare with lastPaymentDate to make sure its time for an update

        stateChange(interactionSequence.charAt(transactionsMade));

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
                    ((TextView) findViewById(R.id.drawer_eggs_raised)).setText("x " + (eggsRaised - 1));

                    ImageView eggWiggle = (ImageView) findViewById(R.id.egg_menu);
                    AnimationDrawable egg = ((AnimationDrawable) eggWiggle.getBackground());
                    egg.start();

                    ((TextView) findViewById(R.id.drawer_progress_status)).setText("$"
                            + Utils.amountToString(goalProgress) + " saved");
                    ((TextView) findViewById(R.id.drawer_goal_status)).setText("$"
                            + Utils.amountToString(goalTotal) + " goal");
                    ((ProgressBar) findViewById(R.id.drawer_progress_bar)).setMax(goalTotal);
                    ((ProgressBar) findViewById(R.id.drawer_progress_bar)).setProgress(goalProgress);
                }
            }
        });
    }

    private void stateChange(char c) {
        Log.d(TAG, "Current state: " + c + " at index " + transactionsMade);
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
                String usernameString = newState.getTitle().replace("%u", PreferenceManager.getDefaultSharedPreferences(this).getString("username", "username"));
                ((TextView) findViewById(R.id.pet_state_title)).setText(usernameString);
                ImageView dragon = (ImageView) findViewById(R.id.pet_state_image);
                dragon.setBackground(getDrawable(newState.getImageId()));
                ((AnimationDrawable) dragon.getBackground()).start();
                return;
        }
        int cost = costFactor * baselineCost;
        if (transactionsMade == 29) cost = goalTotal - goalProgress;
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
                    goalProgress += COST;
                    // stateChange('R');
                    if (++transactionsMade >= 30) {
                        stateChange('R');

                        Intent intent = new Intent(CONTEXT, NewPet.class);
                        intent.putExtras(userData.getExtras());
                        CONTEXT.startActivity(intent);
                    } else
                        // TODO demo hack
                        stateChange(interactionSequence.charAt(transactionsMade));
                }
        });
    }

    private void issuePayment(int amount) {
        new NewPaymentPost(amount, userData.getIntExtra(Utils.OWNER_ID, 0), this);
    }

    private void parseIntent(Intent intent) {
        petname = intent.getStringExtra(Utils.PETNAME);
        goalTotal = intent.getIntExtra(Utils.GOAL, 100) * 100;
        goalProgress = intent.getIntExtra(Utils.PROGRESS, 0);
        eggsRaised = intent.getIntExtra(Utils.PETS, 0);
        interactionSequence = intent.getStringExtra(Utils.INTERACTIONS);
        baselineCost = intent.getIntExtra(Utils.COST, 0);
        transactionsMade = intent.getIntExtra(Utils.TRANSACTIONS, 0);
        lastPaymentDate = intent.getStringExtra(Utils.LAST_PAYMENT);
        ownerID = intent.getIntExtra(Utils.OWNER_ID, 0);
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
            intent.putExtras(userData);
            startActivity(intent);
        } else if (id == R.id.user_settings_option) {
            intent = new Intent(this, UserSettingsActivity.class);
            intent.putExtras(userData);
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
        String usernameString = states[0].getTitle().replace("%u", PreferenceManager.getDefaultSharedPreferences(this).getString("username", "username"));
        ((TextView) findViewById(R.id.pet_state_title)).setText(usernameString);

        NestEgg app = (NestEgg) getApplicationContext();
    }
}
