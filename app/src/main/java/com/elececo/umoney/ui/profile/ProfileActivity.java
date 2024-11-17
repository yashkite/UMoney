package com.elececo.umoney.ui.profile;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.elececo.umoney.R;
import com.elececo.umoney.ui.profile.viewmodel.ProfileViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.button.MaterialButton;

public class ProfileActivity extends AppCompatActivity {

    private ProfileViewModel viewModel;
    private ImageView profileImage;
    private TextInputEditText nameInput;
    private TextInputEditText emailInput;
    private TextInputEditText monthlyIncomeInput;
    private AutoCompleteTextView employmentTypeInput;
    private MaterialButton saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        setupViews();
        setupToolbar();
        setupEmploymentTypeDropdown();
        setupObservers();
        loadUserData();
    }

    private void setupViews() {
        profileImage = findViewById(R.id.profile_image);
        nameInput = findViewById(R.id.name_input);
        emailInput = findViewById(R.id.email_input);
        monthlyIncomeInput = findViewById(R.id.monthly_income_input);
        employmentTypeInput = findViewById(R.id.employment_type_input);
        saveButton = findViewById(R.id.save_button);

        saveButton.setOnClickListener(v -> saveProfile());
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Profile");
    }

    private void setupEmploymentTypeDropdown() {
        String[] employmentTypes = {"Salaried", "Self-Employed", "Business", "Student", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_dropdown_item_1line,
            employmentTypes
        );
        employmentTypeInput.setAdapter(adapter);
    }

    private void setupObservers() {
        viewModel.getUserData().observe(this, user -> {
            if (user != null) {
                nameInput.setText(user.getDisplayName());
                emailInput.setText(user.getEmail());
                monthlyIncomeInput.setText(String.valueOf(user.getMonthlyIncome()));
                employmentTypeInput.setText(user.getEmploymentType(), false);

                if (user.getPhotoUrl() != null) {
                    Glide.with(this)
                        .load(user.getPhotoUrl())
                        .placeholder(R.drawable.default_profile_24)
                        .into(profileImage);
                }
            }
        });

        viewModel.getUpdateResult().observe(this, success -> {
            if (success) {
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUserData() {
        viewModel.loadUserData();
    }

    private void saveProfile() {
        String name = nameInput.getText().toString();
        double monthlyIncome = Double.parseDouble(monthlyIncomeInput.getText().toString());
        String employmentType = employmentTypeInput.getText().toString();

        viewModel.updateProfile(name, monthlyIncome, employmentType);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 