package com.elececo.umoney;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    GoogleSignInClient mGoogleSignInClient;
    BottomNavigationView bottomNavigationView;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Dashboard Dashboard = new Dashboard();
    Needs Needs = new Needs();
    Wants Wants = new Wants();
    Savings Savings = new Savings();
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);


        // Set up the ActionBar for navigation drawer toggle
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_menu_24); // Set your navigation icon
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Create an instance of ActionBarDrawerToggle and tie it to the DrawerLayout
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,                  // your activity
                drawerLayout,          // the DrawerLayout to link
                R.string.open_drawer,  // "open drawer" description
                R.string.close_drawer  // "close drawer" description
        );

        // Set the ActionBarDrawerToggle as the DrawerListener
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        // Sync the toggle state. This ensures the correct icon is shown when the drawer is open or closed.
        actionBarDrawerToggle.syncState();
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.Dashboard);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Toggle the drawer when the navigation icon is clicked
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void msignOut() {

        mAuth.signOut();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);


    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser == null) {
            startActivity(new Intent(this, Google_Login.class));
            finish();
        } else {
            Toast.makeText(this, "User not getting signout", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.Dashboard:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, Dashboard).commit();
                return true;

            case R.id.Needs:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, Needs).commit();
                return true;

            case R.id.Wants:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, Wants).commit();
                return true;

            case R.id.Savings:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, Savings).commit();
                return true;
        }
        return false;
    }


}