package io.github.nestegg333.nestegg;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import io.github.nestegg333.nestegg.auth.Logout;
import io.github.nestegg333.nestegg.post.NewPaymentPost;
import io.github.nestegg333.nestegg.services.Alarm;
import io.github.nestegg333.nestegg.services.NotificationService;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final static String TAG = "NestEgg";
    private PetState[] states;
    private DrawerLayout drawer;
    private int goalTotal, goalProgress, eggsRaised, baselineCost, transactionsMade;
    private String username, petname, token, interactionSequence, lastPaymentDate;
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

        Alarm.scheduleAlarms(this);

        // Setup navigation drawer
        initNavigationDrawer();

        // Receive the account information:
        userData = getIntent();
        parseIntent(userData);

        // Initialize the pet states:
        states = new PetState[] {
                new PetState("Hi " + username + "!", "Neato!", R.drawable.restingdragon, "Resting"),
                new PetState("Oh no! " + petname + " is hungry!",
                        "Feed " + petname + "! - $%m", R.drawable.hungrydragon, "Hungry"),
                new PetState("Uh oh, " + petname + " looks bored...",
                        "Give " + petname + " a toy! - $%m", R.drawable.boreddragon, "Bored"),
                new PetState("Ahh! " + petname + " is sick!",
                        "Take " + petname + " to the vet! - $%m", R.drawable.sickdragon, "Sick"),
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
                    ((TextView) findViewById(R.id.drawer_username)).setText(username);
                    ((TextView) findViewById(R.id.drawer_eggs_raised)).setText("x " + eggsRaised);
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
                ((TextView) findViewById(R.id.pet_state_title)).setText(newState.getTitle());
                ((ImageView) findViewById(R.id.pet_state_image)).setImageDrawable(getDrawable(newState.getImageId()));
                return;
        }
        int cost = costFactor * baselineCost;
        if (transactionsMade == 29) cost = goalTotal - goalProgress;
        final int COST = cost;

        ((TextView) findViewById(R.id.pet_state_title)).setText(newState.getTitle());
        ((ImageView) findViewById(R.id.pet_state_image)).setImageDrawable(getDrawable(newState.getImageId()));

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
                        Toast.makeText(CONTEXT, "Your NestEgg is raised!", Toast.LENGTH_LONG).show();
                    } else
                        // TODO demo hack
                        stateChange(interactionSequence.charAt(++transactionsMade));
                }
        });
    }

    private void issuePayment(int amount) {
        // TODO dynamic user ID
        new NewPaymentPost(amount, 1);
    }

    private void parseIntent(Intent intent) {
        token = intent.getStringExtra(Utils.TOKEN);
        username = intent.getStringExtra(Utils.USERNAME);
        petname = intent.getStringExtra(Utils.PETNAME);
        goalTotal = intent.getIntExtra(Utils.GOAL, 100);
        goalProgress = intent.getIntExtra(Utils.PROGRESS, 0);
        eggsRaised = intent.getIntExtra(Utils.PETS, 0);
        interactionSequence = intent.getStringExtra(Utils.INTERACTIONS);
        baselineCost = intent.getIntExtra(Utils.COST, 0);
        transactionsMade = intent.getIntExtra(Utils.TRANSACTIONS, 0);
        lastPaymentDate = intent.getStringExtra(Utils.LAST_PAYMENT);
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
            intent.putExtras(userData.getExtras());
            startActivity(intent);
        } else if (id == R.id.user_settings_option) {
            intent = new Intent(this, UserSettingsActivity.class);
            intent.putExtras(userData.getExtras());
            startActivity(intent);
        } else if (id == R.id.logout_option) {
            new Logout(userData.getStringExtra(Utils.TOKEN), this);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
