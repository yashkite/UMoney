package com.elececo.umoney.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.elececo.umoney.R;
import com.elececo.umoney.ui.auth.GoogleLoginActivity;
import com.elececo.umoney.ui.auth.UserSetupActivity;
import com.elececo.umoney.ui.dashboard.DashboardActivity;
import com.elececo.umoney.ui.auth.viewmodel.AuthViewModel;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {
    private static final long SPLASH_DELAY = 2000; // 2 seconds
    private AuthViewModel authViewModel;
    private final Handler handler = new Handler();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        
        handler.postDelayed(this::checkAuthStatus, SPLASH_DELAY);
    }
    
    private void checkAuthStatus() {
        FirebaseUser currentUser = authViewModel.getCurrentUser();
        if (currentUser == null) {
            startLoginActivity();
        } else {
            checkUserStatus();
        }
    }
    
    private void checkUserStatus() {
        authViewModel.getIsFirstTimeUser().observe(this, isFirstTime -> {
            if (isFirstTime) {
                startSetupActivity();
            } else {
                startMainActivity();
            }
        });
        
        authViewModel.checkUserStatus();
    }
    
    private void startLoginActivity() {
        Intent intent = new Intent(this, GoogleLoginActivity.class);
        startActivity(intent);
        finish();
    }
    
    private void startSetupActivity() {
        Intent intent = new Intent(this, UserSetupActivity.class);
        startActivity(intent);
        finish();
    }
    
    private void startMainActivity() {
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
} 