package com.elececo.umoney.ui.income.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.elececo.umoney.data.model.Transaction;
import com.elececo.umoney.data.model.UserPreferences;
import com.elececo.umoney.ui.base.BaseViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IncomeViewModel extends BaseViewModel {
    private final FirebaseFirestore db;
    private final FirebaseAuth auth;
    private final MutableLiveData<UserPreferences> userPreferences;

    public IncomeViewModel() {
        super();
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        userPreferences = new MutableLiveData<>();
        loadUserPreferences();
    }

    @Override
    protected String getTransactionType() {
        return "INCOME";
    }

    public LiveData<UserPreferences> getUserPreferences() {
        return userPreferences;
    }

    private void loadUserPreferences() {
        String userId = auth.getCurrentUser().getUid();
        db.collection("users").document(userId)
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

    public void distributeIncome(Transaction needs, Transaction wants, Transaction savings) {
        String userId = auth.getCurrentUser().getUid();
        
        // Save all distributed transactions to Firestore
        db.collection("users").document(userId)
            .collection("transactions")
            .add(needs)
            .addOnSuccessListener(documentReference -> {
                // Save wants after needs is saved
                db.collection("users").document(userId)
                    .collection("transactions")
                    .add(wants)
                    .addOnSuccessListener(ref -> {
                        // Save savings after wants is saved
                        db.collection("users").document(userId)
                            .collection("transactions")
                            .add(savings);
                    });
            });
    }
}
