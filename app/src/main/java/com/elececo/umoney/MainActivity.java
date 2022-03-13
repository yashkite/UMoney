package com.elececo.umoney;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.Dashboard);

    }

    Dashboard Dashboard = new Dashboard();
    Needs Needs = new Needs();
    Wants Wants = new Wants();
    Savings Savings = new Savings();

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