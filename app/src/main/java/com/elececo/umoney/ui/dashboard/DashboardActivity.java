package com.elececo.umoney.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import com.elececo.umoney.R;
import com.elececo.umoney.ui.auth.GoogleLoginActivity;
import com.elececo.umoney.ui.dashboard.fragments.DashboardFragment;
import com.elececo.umoney.ui.needs.NeedsFragment;
import com.elececo.umoney.ui.settings.SettingsActivity;
import com.elececo.umoney.ui.wants.WantsFragment;
import com.elececo.umoney.ui.savings.SavingsFragment;
import com.elececo.umoney.ui.income.IncomeFragment;
import com.elececo.umoney.ui.profile.ProfileActivity;
import com.elececo.umoney.ui.about.AboutActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    
    private DrawerLayout drawerLayout;
    private Fragment dashboardFragment;
    private Fragment needsFragment;
    private Fragment wantsFragment;
    private Fragment savingsFragment;
    private Fragment incomeFragment;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        
        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        // Setup DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        
        // Setup ActionBarDrawerToggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        
        if (savedInstanceState == null) {
            dashboardFragment = new DashboardFragment();
            needsFragment = new NeedsFragment();
            wantsFragment = new WantsFragment();
            savingsFragment = new SavingsFragment();
            incomeFragment = new IncomeFragment();
            
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, dashboardFragment)
                .commit();
        }
        
        setupBottomNavigation();
        
        // Setup Navigation Header
        setupNavHeader(navigationView);
    }
    
    private void setupNavHeader(NavigationView navigationView) {
        View headerView = navigationView.getHeaderView(0);
        TextView nameTextView = headerView.findViewById(R.id.nav_header_name);
        TextView emailTextView = headerView.findViewById(R.id.nav_header_email);
        
        // TODO: Get user data from FirebaseAuth and update header
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            nameTextView.setText(auth.getCurrentUser().getDisplayName());
            emailTextView.setText(auth.getCurrentUser().getEmail());
        }
    }
    
    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_dashboard) {
                selectedFragment = dashboardFragment;
            } else if (itemId == R.id.navigation_needs) {
                selectedFragment = needsFragment;
            } else if (itemId == R.id.navigation_wants) {
                selectedFragment = wantsFragment;
            } else if (itemId == R.id.navigation_savings) {
                selectedFragment = savingsFragment;
            } else if (itemId == R.id.navigation_income) {
                selectedFragment = incomeFragment;
            }
            
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();
            }
            
            return true;
        });
    }
    
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        
        if (itemId == R.id.nav_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
        } else if (itemId == R.id.nav_about) {
            startActivity(new Intent(this, AboutActivity.class));
        } else if (itemId == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (itemId == R.id.nav_categories) {
            // TODO: Navigate to Categories
        } else if (itemId == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, GoogleLoginActivity.class));
            finish();
        }
        
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
} 