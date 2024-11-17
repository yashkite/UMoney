package com.elececo.umoney.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.elececo.umoney.R;
import com.elececo.umoney.ui.auth.viewmodel.AuthViewModel;
import com.elececo.umoney.ui.dashboard.DashboardActivity;

public class GoogleLoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 9001;
    private AuthViewModel authViewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_login);
        
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        
        findViewById(R.id.sign_in_button).setOnClickListener(v -> signIn());
        
        authViewModel.getAuthResult().observe(this, result -> {
            if (result.isSuccess()) {
                startMainActivity();
            } else {
                // Show error message
                Toast.makeText(this, "Sign in failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void signIn() {
        Intent signInIntent = authViewModel.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            authViewModel.handleSignInResult(data);
        }
    }
    
    private void startMainActivity() {
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }
} 