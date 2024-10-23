package com.elececo.umoney;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private View transactionButtonsLayout;
    private Button givenButton, takenButton;
    private String currentFragmentTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        transactionButtonsLayout = findViewById(R.id.transactionButtonsLayout);
        
        if (transactionButtonsLayout == null) {
            Log.e("MainActivity", "transactionButtonsLayout is null");
        } else {
            givenButton = transactionButtonsLayout.findViewById(R.id.givenButton);
            takenButton = transactionButtonsLayout.findViewById(R.id.takenButton);
            
            if (givenButton == null || takenButton == null) {
                Log.e("MainActivity", "Button views are null");
            } else {
                Log.d("MainActivity", "All views found successfully");
            }
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.Dashboard) {
                replaceFragment(new DashboardFragment(), "Dashboard");
            } else if (itemId == R.id.Needs) {
                replaceFragment(new NeedsFragment(), "Needs");
            } else if (itemId == R.id.Wants) {
                replaceFragment(new WantsFragment(), "Wants");
            } else if (itemId == R.id.Savings) {
                replaceFragment(new SavingsFragment(), "Savings");
            }
            return true;
        });

        givenButton.setOnClickListener(v -> handleTransactionClick("Given"));
        takenButton.setOnClickListener(v -> handleTransactionClick("Taken"));

        // Set default fragment
        replaceFragment(new DashboardFragment(), "Dashboard");

        // Force visibility of transaction buttons
        if (transactionButtonsLayout != null) {
            transactionButtonsLayout.setVisibility(View.VISIBLE);
            Log.d("MainActivity", "Forcing transaction buttons visibility to VISIBLE");
        }

        updateTransactionButtonsVisibility();
    }

    private void replaceFragment(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();

        currentFragmentTag = tag;
        updateTransactionButtonsVisibility();
    }

    private void updateTransactionButtonsVisibility() {
        if (transactionButtonsLayout != null) {
            if (currentFragmentTag.equals("Needs") || currentFragmentTag.equals("Wants") || currentFragmentTag.equals("Savings")) {
                transactionButtonsLayout.setVisibility(View.VISIBLE);
                Log.d("MainActivity", "Setting transaction buttons to VISIBLE for " + currentFragmentTag);
            } else {
                transactionButtonsLayout.setVisibility(View.GONE);
                Log.d("MainActivity", "Setting transaction buttons to GONE for " + currentFragmentTag);
            }
            Log.d("MainActivity", "Current visibility: " + (transactionButtonsLayout.getVisibility() == View.VISIBLE ? "VISIBLE" : "GONE"));
        } else {
            Log.e("MainActivity", "transactionButtonsLayout is null");
        }
    }

    private void handleTransactionClick(String transactionType) {
        String category = currentFragmentTag; // This will be "Needs", "Wants", or "Savings"
        
        Intent intent = new Intent(this, AddTransactionActivity.class);
        intent.putExtra("category", category);
        intent.putExtra("transactionType", transactionType);
        startActivity(intent);
    }
}
