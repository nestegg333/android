package io.github.nestegg333.nestegg;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private PetState[] states;
    private int currentState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        states = new PetState[] {
            new PetState("Hi username123!", "Neato!", R.drawable.restingdragon),
            new PetState("Oh no! Jimmy is hungry!", "Feed Jimmy! - $3", R.drawable.hungrydragon),
            new PetState("Uh oh, Jimmy looks bored...", "Give Jimmy a toy! - $9", R.drawable.boreddragon),
            new PetState("Ahh! Jimmy is sick!", "Take Jimmy to the vet! - $18", R.drawable.sickdragon),
        };

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        currentState = -1;
        stateChange();
    }

    private void stateChange() {
        PetState newState = states[++currentState % 4];

        ((TextView) findViewById(R.id.pet_state_title)).setText(newState.getTitle());
        ((ImageView) findViewById(R.id.pet_state_image)).setImageDrawable(getDrawable(newState.getImageId()));

        Button actionButton = (Button) findViewById(R.id.pet_state_action);
        actionButton.setText(newState.getAction());
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stateChange();
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            // super.onBackPressed(); // TODO: this is so we don't go back to the account creation screen
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
