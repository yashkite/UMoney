package com.elececo.umoney.ui.dashboard.viewmodel;

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
    private MutableLiveData<DashboardData> dashboardData = new MutableLiveData<>();
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    public DashboardViewModel() {
        super();
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        loadDashboardData();
    }

    public LiveData<DashboardData> getDashboardData() {
        return dashboardData;
    }
    
    private void loadDashboardData() {
        String userId = auth.getCurrentUser().getUid();
        
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener(document -> {
                if (document.exists()) {
                    double monthlyIncome = document.getDouble("monthlyIncome") != null ? 
                        document.getDouble("monthlyIncome") : 0.0;
                    calculateTotals(userId, monthlyIncome);
                }
            });
    }
    
    private void calculateTotals(String userId, double monthlyIncome) {
        db.collection("transactions")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener(querySnapshot -> {
                double totalNeeds = 0;
                double totalWants = 0;
                double totalSavings = 0;
                
                for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                    String type = doc.getString("type");
                    double amount = doc.getDouble("amount") != null ? 
                        doc.getDouble("amount") : 0.0;
                    
                    switch (type) {
                        case "NEEDS":
                            totalNeeds += amount;
                            break;
                        case "WANTS":
                            totalWants += amount;
                            break;
                        case "SAVINGS":
                            totalSavings += amount;
                            break;
                    }
                }
                
                dashboardData.setValue(new DashboardData(
                    monthlyIncome, totalNeeds, totalWants, totalSavings));
            });
    }
    
    @Override
    protected String getTransactionType() {
        return "DASHBOARD";
    }
} 