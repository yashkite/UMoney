package com.elececo.umoney.ui.settings.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.elececo.umoney.data.model.UserPreferences;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class SettingsViewModel extends ViewModel {
    private final FirebaseFirestore db;
    private final FirebaseAuth auth;
    private final MutableLiveData<UserPreferences> distributionSettings;
    private final MutableLiveData<Boolean> saveResult;

    public SettingsViewModel() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        distributionSettings = new MutableLiveData<>();
        saveResult = new MutableLiveData<>();
        loadDistributionSettings();
    }

    public LiveData<UserPreferences> getDistributionSettings() {
        return distributionSettings;
    }

    public LiveData<Boolean> getSaveResult() {
        return saveResult;
    }

    private void loadDistributionSettings() {
        String userId = auth.getCurrentUser().getUid();
        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener(document -> {
                if (document.exists()) {
                    Map<String, Object> data = document.getData();
                    int needs = data.containsKey("needsPercentage") ? 
                        ((Long) data.get("needsPercentage")).intValue() : 50;
                    int wants = data.containsKey("wantsPercentage") ? 
                        ((Long) data.get("wantsPercentage")).intValue() : 30;
                    int savings = data.containsKey("savingsPercentage") ? 
                        ((Long) data.get("savingsPercentage")).intValue() : 20;
                    
                    distributionSettings.setValue(new UserPreferences(needs, wants, savings));
                } else {
                    // Set default values if no settings exist
                    distributionSettings.setValue(new UserPreferences(50, 30, 20));
                }
            })
            .addOnFailureListener(e -> {
                // Handle error
                distributionSettings.setValue(new UserPreferences(50, 30, 20));
            });
    }

    public void saveDistributionSettings(int needs, int wants, int savings) {
        String userId = auth.getCurrentUser().getUid();
        Map<String, Object> updates = new HashMap<>();
        updates.put("needsPercentage", needs);
        updates.put("wantsPercentage", wants);
        updates.put("savingsPercentage", savings);

        db.collection("users")
            .document(userId)
            .update(updates)
            .addOnSuccessListener(aVoid -> {
                distributionSettings.setValue(new UserPreferences(needs, wants, savings));
                saveResult.setValue(true);
            })
            .addOnFailureListener(e -> saveResult.setValue(false));
    }
} 