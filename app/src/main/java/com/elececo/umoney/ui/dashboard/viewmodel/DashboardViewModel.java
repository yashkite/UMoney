package com.elececo.umoney.ui.dashboard.viewmodel;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.elececo.umoney.data.model.DashboardData;
import com.elececo.umoney.ui.base.BaseViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.Map;

public class DashboardViewModel extends BaseViewModel {
    private static final String TAG = "DashboardViewModel";
    private MutableLiveData<DashboardData> dashboardData = new MutableLiveData<>();
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    public DashboardViewModel() {
        super();
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        Log.d(TAG, "DashboardViewModel initialized");
        loadDashboardData();
    }

    public LiveData<DashboardData> getDashboardData() {
        return dashboardData;
    }
    
    private void loadDashboardData() {
        if (auth.getCurrentUser() == null) {
            Log.e(TAG, "User is not authenticated");
            return;
        }

        String userId = auth.getCurrentUser().getUid();
        Log.d(TAG, "Loading dashboard data for user: " + userId);
        
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener(document -> {
                if (document.exists()) {
                    double monthlyIncome = document.getDouble("monthlyIncome") != null ? 
                        document.getDouble("monthlyIncome") : 0.0;
                    Log.d(TAG, "Monthly income loaded: " + monthlyIncome);
                    calculateTotals(userId, monthlyIncome);
                } else {
                    Log.e(TAG, "Document does not exist");
                    dashboardData.setValue(new DashboardData(0.0, 0.0, 0.0, 0.0));
                }
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error loading dashboard data", e);
                dashboardData.setValue(new DashboardData(0.0, 0.0, 0.0, 0.0));
            });
    }
    
    private void calculateTotals(String userId, double monthlyIncome) {
        db.collection("users")
            .document(userId)
            .collection("transactions")
            .get()
            .addOnSuccessListener(querySnapshot -> {
                double totalNeedsIn = 0;
                double totalNeedsOut = 0;
                double totalWantsIn = 0;
                double totalWantsOut = 0;
                double totalSavingsIn = 0;
                double totalSavingsOut = 0;
                
                for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                    String type = doc.getString("type");
                    Double amount = doc.getDouble("amount");
                    
                    if (type == null || amount == null) {
                        Log.w(TAG, "Skipping transaction with null type or amount, docId: " + doc.getId());
                        continue;
                    }
                    
                    Log.d(TAG, "Processing transaction - Type: " + type + ", Amount: " + amount);
                    
                    switch (type) {
                        case "NEEDS":
                            if (amount > 0) totalNeedsIn += amount;
                            else totalNeedsOut += Math.abs(amount);
                            break;
                        case "WANTS":
                            if (amount > 0) totalWantsIn += amount;
                            else totalWantsOut += Math.abs(amount);
                            break;
                        case "SAVINGS":
                            if (amount > 0) totalSavingsIn += amount;
                            else totalSavingsOut += Math.abs(amount);
                            break;
                        case "INCOME":
                            // Handle income type if needed
                            break;
                        default:
                            Log.w(TAG, "Unknown transaction type: " + type);
                    }
                }
                
                double needsHold = totalNeedsIn - totalNeedsOut;
                double wantsHold = totalWantsIn - totalWantsOut;
                double savingsHold = totalSavingsIn - totalSavingsOut;
                
                dashboardData.setValue(new DashboardData(monthlyIncome, needsHold, wantsHold, savingsHold));
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error calculating totals", e);
                dashboardData.setValue(new DashboardData(monthlyIncome, 0.0, 0.0, 0.0));
            });
    }
    
    @Override
    protected String getTransactionType() {
        return "DASHBOARD";
    }
} 