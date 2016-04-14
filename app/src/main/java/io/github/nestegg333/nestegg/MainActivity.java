package io.github.nestegg333.nestegg;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final static String TAG = "NestEgg";
    private PetState[] states;
    private DrawerLayout drawer;
    private int goalTotal, goalProgress, eggsRaised, baselineCost, transactionsMade;
    private String username, petname, token, interactionSequence, lastPaymentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Activity and view init:
        super.onCreate(savedInstanceState);
        Utils.setDefaultFont(this, "MONOSPACE", "fonts/Arciform.ttf"); // OVERRIDE activity-wide font to custom font:
        setContentView(R.layout.activity_main);
        Utils.hideActionBar(this);

        // Setup navigation drawer
        initNavigationDrawer();

        // Receive the account information:
        parseIntent(getIntent());

        // Initialize the pet states:
        states = new PetState[] {
                new PetState("Hi " + username + "!", "Neato!", R.drawable.restingdragon, "Resting"),
                new PetState("Oh no! " + petname + " is hungry!", "Feed " + petname + "! - $%d", R.drawable.hungrydragon, "Hungry"),
                new PetState("Uh oh, " + petname + " looks bored...", "Give " + petname + " a toy! - $%d", R.drawable.boreddragon, "Bored"),
                new PetState("Ahh! " + petname + " is sick!", "Take " + petname + " to the vet! - $%d", R.drawable.sickdragon, "Sick"),
        };

        // TODO: compare with lastPaymentDate to make sure its time for an update
        stateChange(interactionSequence.charAt(transactionsMade), baselineCost);
    }

    private void initNavigationDrawer() {
        // Setup navigation drawer
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
                    ((TextView) findViewById(R.id.drawer_progress_status)).setText("$" + goalProgress + " saved");
                    ((TextView) findViewById(R.id.drawer_goal_status)).setText("$" + goalTotal + " goal");
                    ((ProgressBar) findViewById(R.id.drawer_progress_bar)).setMax(goalTotal);
                    ((ProgressBar) findViewById(R.id.drawer_progress_bar)).setProgress(goalProgress);
                }
            }
        });
    }

    private void stateChange(char c, final int cost) {
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

        ((TextView) findViewById(R.id.pet_state_title)).setText(newState.getTitle());
        ((ImageView) findViewById(R.id.pet_state_image)).setImageDrawable(getDrawable(newState.getImageId()));

        // Set up the action button:
        Button actionButton = (Button) findViewById(R.id.pet_state_action);
        String actionString = newState.getAction().replace("%d", Utils.amountToString(cost * costFactor));
        actionButton.setText(actionString);
        actionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    issuePayment(cost);
                    //TODO stateChange('R', 0);
                    stateChange(interactionSequence.charAt(++transactionsMade), baselineCost);
                }
        });
    }

    private void issuePayment(int amount) {
        // TODO: hit api. Note: amount will be 100x value expected
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        // TODO: Handle new views/settings menus in each case
        if (id == R.id.payment_history) {

        } else if (id == R.id.user_settings_option) {

        } else if (id == R.id.logout_option) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
