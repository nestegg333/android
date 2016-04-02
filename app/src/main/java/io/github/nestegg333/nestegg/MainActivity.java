package io.github.nestegg333.nestegg;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final static String TAG = "NestEgg";
    private PetState[] states;
    private int currentState;
    private DrawerLayout drawer;
    private int goalTotal, goalProgress, eggsRaised;
    private String username, petname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Activity and view init:
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().hide();

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

        // Receive the account information:
        Intent intent = getIntent();
        username = intent.getStringExtra("USERNAME");
        petname = intent.getStringExtra("PETNAME");
        goalTotal = intent.getIntExtra("GOAL", 100);
        goalProgress = intent.getIntExtra("PROGRESS", 0);
        eggsRaised = intent.getIntExtra("EGGS_RAISED", 0);

        // Initialize the pet states:
        states = new PetState[] {
                new PetState("Hi " + username + "!", "Neato!", R.drawable.restingdragon, "Resting"),
                new PetState("Oh no! " + petname + " is hungry!", "Feed " + petname + "! - $3", R.drawable.hungrydragon, "Hungry"),
                new PetState("Uh oh, " + petname + " looks bored...", "Give " + petname + " a toy! - $9", R.drawable.boreddragon, "Bored"),
                new PetState("Ahh! " + petname + " is sick!", "Take " + petname + " to the vet! - $18", R.drawable.sickdragon, "Sick"),
        };

        // TODO: Will need to read today's activity and cost from storage:
        currentState = 0;
        stateChange();
    }

    private void stateChange() {
        // TODO: Just for the purposes of demo:
        PetState newState = states[++currentState % 4];

        ((TextView) findViewById(R.id.pet_state_title)).setText(newState.getTitle());
        ((ImageView) findViewById(R.id.pet_state_image)).setImageDrawable(getDrawable(newState.getImageId()));

        if (newState.getLabel().equals("Resting")) {
            findViewById(R.id.no_action).setVisibility(View.VISIBLE);
            findViewById(R.id.action_container).setVisibility(View.GONE);
        } else {
            findViewById(R.id.no_action).setVisibility(View.GONE);
            findViewById(R.id.action_container).setVisibility(View.VISIBLE);
            Button actionButton = (Button) findViewById(R.id.pet_state_action);
            actionButton.setText(newState.getAction());
            actionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stateChange();
                }
            });
        }
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
