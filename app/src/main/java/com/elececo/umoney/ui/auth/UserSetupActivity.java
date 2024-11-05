package com.elececo.umoney.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.elececo.umoney.R;
import com.elececo.umoney.ui.dashboard.DashboardActivity;

public class UserSetupActivity extends AppCompatActivity {
    private EditText monthlyIncomeInput;
    private EditText employmentTypeInput;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setup);
        
        monthlyIncomeInput = findViewById(R.id.monthly_income_input);
        employmentTypeInput = findViewById(R.id.employment_type_input);
        Button continueButton = findViewById(R.id.continue_button);
        
        continueButton.setOnClickListener(v -> saveUserSetup());
    }
    
    private void saveUserSetup() {
        // TODO: Implement saving user setup data
        startDashboardActivity();
    }
    
    private void startDashboardActivity() {
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }
} 