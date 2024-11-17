package com.elececo.umoney.ui.settings;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.elececo.umoney.R;
import com.elececo.umoney.ui.settings.viewmodel.SettingsViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class SettingsActivity extends AppCompatActivity {
    private TextInputEditText needsPercentageInput;
    private TextInputEditText wantsPercentageInput;
    private TextInputEditText savingsPercentageInput;
    private MaterialButton saveButton;
    private SettingsViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        viewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        
        setupViews();
        setupToolbar();
        setupObservers();
        loadSettings();
    }

    private void setupViews() {
        needsPercentageInput = findViewById(R.id.needs_percentage_input);
        wantsPercentageInput = findViewById(R.id.wants_percentage_input);
        savingsPercentageInput = findViewById(R.id.savings_percentage_input);
        saveButton = findViewById(R.id.save_button);

        saveButton.setOnClickListener(v -> validateAndSaveSettings());
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Settings");
    }

    private void validateAndSaveSettings() {
        int needsPercentage = Integer.parseInt(needsPercentageInput.getText().toString());
        int wantsPercentage = Integer.parseInt(wantsPercentageInput.getText().toString());
        int savingsPercentage = Integer.parseInt(savingsPercentageInput.getText().toString());

        if (needsPercentage + wantsPercentage + savingsPercentage != 100) {
            Toast.makeText(this, "Percentages must sum to 100%", Toast.LENGTH_SHORT).show();
            return;
        }

        viewModel.saveDistributionSettings(needsPercentage, wantsPercentage, savingsPercentage);
    }

    private void setupObservers() {
        viewModel.getSaveResult().observe(this, success -> {
            if (success) {
                Toast.makeText(this, "Settings saved successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to save settings", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadSettings() {
        viewModel.getDistributionSettings().observe(this, settings -> {
            needsPercentageInput.setText(String.valueOf(settings.getNeedsPercentage()));
            wantsPercentageInput.setText(String.valueOf(settings.getWantsPercentage()));
            savingsPercentageInput.setText(String.valueOf(settings.getSavingsPercentage()));
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 