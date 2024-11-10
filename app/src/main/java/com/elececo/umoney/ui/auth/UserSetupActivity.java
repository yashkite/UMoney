package com.elececo.umoney.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.elececo.umoney.R;
import com.elececo.umoney.ui.dashboard.DashboardActivity;
import com.elececo.umoney.ui.auth.viewmodel.AuthViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserSetupActivity extends AppCompatActivity {
    private TextInputEditText displayNameInput;
    private TextInputEditText monthlyIncomeInput;
    private AutoCompleteTextView employmentTypeInput;
    private AuthViewModel authViewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setup);
        
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        
        setupViews();
        setupEmploymentTypeDropdown();
        setupObservers();
        loadUserData();
    }
    
    private void setupViews() {
        displayNameInput = findViewById(R.id.display_name_input);
        monthlyIncomeInput = findViewById(R.id.monthly_income_input);
        employmentTypeInput = findViewById(R.id.employment_type_input);
        MaterialButton continueButton = findViewById(R.id.continue_button);
        
        continueButton.setOnClickListener(v -> validateAndSaveUserSetup());
    }
    
    private void setupEmploymentTypeDropdown() {
        String[] employmentTypes = {"Salaried", "Self-Employed", "Business", "Student", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this,
            R.layout.dropdown_item,
            employmentTypes
        );
        employmentTypeInput.setAdapter(adapter);
    }
    
    private void validateAndSaveUserSetup() {
        String displayName = displayNameInput.getText().toString();
        String monthlyIncomeStr = monthlyIncomeInput.getText().toString();
        String employmentType = employmentTypeInput.getText().toString();
        
        if (displayName.isEmpty()) {
            displayNameInput.setError("Please enter your name");
            return;
        }
        
        if (monthlyIncomeStr.isEmpty()) {
            monthlyIncomeInput.setError("Please enter your monthly income");
            return;
        }
        
        if (employmentType.isEmpty()) {
            employmentTypeInput.setError("Please select your employment type");
            return;
        }
        
        try {
            double monthlyIncome = Double.parseDouble(monthlyIncomeStr);
            saveUserSetup(displayName, monthlyIncome, employmentType);
        } catch (NumberFormatException e) {
            monthlyIncomeInput.setError("Please enter a valid amount");
        }
    }
    
    private void saveUserSetup(String displayName, double monthlyIncome, String employmentType) {
        authViewModel.saveUserSetup(displayName, monthlyIncome, employmentType).observe(this, success -> {
            if (success) {
                authViewModel.setIsFirstTimeUser(false);
                startDashboardActivity();
            } else {
                Toast.makeText(this, "Failed to save setup data", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void startDashboardActivity() {
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
    
    private void setupObservers() {
        authViewModel.getUserLiveData().observe(this, user -> {
            if (user != null) {
                displayNameInput.setText(user.getDisplayName());
                if (user.getMonthlyIncome() > 0) {
                    monthlyIncomeInput.setText(String.valueOf(user.getMonthlyIncome()));
                }
                if (user.getEmploymentType() != null) {
                    employmentTypeInput.setText(user.getEmploymentType(), false);
                }
            }
        });
    }
    
    private void loadUserData() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            displayNameInput.setText(currentUser.getDisplayName());
        }
    }
} 