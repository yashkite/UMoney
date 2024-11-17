package com.elececo.umoney.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.elececo.umoney.data.model.UserPreferences;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Map;
import java.util.HashMap;

public class UserPreferencesRepository {
    private final FirebaseFirestore db;
    private final FirebaseAuth auth;
    private final MutableLiveData<UserPreferences> userPreferences;

    public UserPreferencesRepository() {
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
        this.userPreferences = new MutableLiveData<>();
        loadUserPreferences();
    }

    public LiveData<UserPreferences> getUserPreferences() {
        return userPreferences;
    }

    private void loadUserPreferences() {
        if (auth.getCurrentUser() == null) {
            userPreferences.setValue(new UserPreferences(50, 30, 20));
            return;
        }

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
                    
                    userPreferences.setValue(new UserPreferences(needs, wants, savings));
                } else {
                    userPreferences.setValue(new UserPreferences(50, 30, 20));
                }
            })
            .addOnFailureListener(e -> {
                userPreferences.setValue(new UserPreferences(50, 30, 20));
            });
    }

    public void saveUserPreferences(int needs, int wants, int savings) {
        if (auth.getCurrentUser() == null) return;

        String userId = auth.getCurrentUser().getUid();
        Map<String, Object> updates = new HashMap<>();
        updates.put("needsPercentage", needs);
        updates.put("wantsPercentage", wants);
        updates.put("savingsPercentage", savings);

        db.collection("users")
            .document(userId)
            .update(updates)
            .addOnSuccessListener(aVoid -> {
                userPreferences.setValue(new UserPreferences(needs, wants, savings));
            });
    }
} 