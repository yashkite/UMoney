package com.elececo.umoney.ui.income.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.elececo.umoney.data.model.Transaction;
import com.elececo.umoney.data.model.UserPreferences;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class IncomeViewModel extends ViewModel {
    private final FirebaseFirestore db;
    private final FirebaseAuth auth;
    private final MutableLiveData<List<Transaction>> transactions;
    private final MutableLiveData<UserPreferences> userPreferences;

    public IncomeViewModel() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        transactions = new MutableLiveData<>(new ArrayList<>());
        userPreferences = new MutableLiveData<>();
        loadTransactions();
        loadUserPreferences();
    }

    public LiveData<List<Transaction>> getTransactions() {
        return transactions;
    }

    public LiveData<UserPreferences> getUserPreferences() {
        return userPreferences;
    }

    private void loadTransactions() {
        String userId = auth.getCurrentUser().getUid();
        db.collection("users")
            .document(userId)
            .collection("transactions")
            .whereEqualTo("type", "INCOME")
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<Transaction> transactionList = new ArrayList<>();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Transaction transaction = document.toObject(Transaction.class);
                    transactionList.add(transaction);
                }
                transactions.setValue(transactionList);
            });
    }

    private void loadUserPreferences() {
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

    public void distributeIncome(double totalIncome, UserPreferences preferences, String parentTransactionId) {
        String userId = auth.getCurrentUser().getUid();
        
        // Calculate amounts for each category
        double needsAmount = totalIncome * (preferences.getNeedsPercentage() / 100.0);
        double wantsAmount = totalIncome * (preferences.getWantsPercentage() / 100.0);
        double savingsAmount = totalIncome * (preferences.getSavingsPercentage() / 100.0);

        Date currentTime = new Date();

        // Create distributed transactions
        Transaction needsTransaction = new Transaction();
        needsTransaction.setAmount(needsAmount);
        needsTransaction.setNotes("Auto-distributed from Income");
        needsTransaction.setCategory("NEEDS");
        needsTransaction.setType("NEEDS");
        needsTransaction.setTimestamp(currentTime);
        needsTransaction.setParentTransactionId(parentTransactionId);
        needsTransaction.setUserId(userId);

        Transaction wantsTransaction = new Transaction();
        wantsTransaction.setAmount(wantsAmount);
        wantsTransaction.setNotes("Auto-distributed from Income");
        wantsTransaction.setCategory("WANTS");
        wantsTransaction.setType("WANTS");
        wantsTransaction.setTimestamp(currentTime);
        wantsTransaction.setParentTransactionId(parentTransactionId);
        wantsTransaction.setUserId(userId);

        Transaction savingsTransaction = new Transaction();
        savingsTransaction.setAmount(savingsAmount);
        savingsTransaction.setNotes("Auto-distributed from Income");
        savingsTransaction.setCategory("SAVINGS");
        savingsTransaction.setType("SAVINGS");
        savingsTransaction.setTimestamp(currentTime);
        savingsTransaction.setParentTransactionId(parentTransactionId);
        savingsTransaction.setUserId(userId);

        // Save all transactions
        db.collection("users")
            .document(userId)
            .collection("transactions")
            .add(needsTransaction);

        db.collection("users")
            .document(userId)
            .collection("transactions")
            .add(wantsTransaction);

        db.collection("users")
            .document(userId)
            .collection("transactions")
            .add(savingsTransaction);
    }

    public void saveTransaction(Transaction transaction) {
        String userId = auth.getCurrentUser().getUid();
        db.collection("users")
            .document(userId)
            .collection("transactions")
            .add(transaction)
            .addOnSuccessListener(documentReference -> {
                String transactionId = documentReference.getId();
                transaction.setId(transactionId);
                loadTransactions();
                // After saving income transaction, distribute it
                if (transaction.getType().equals("INCOME")) {
                    getUserPreferences().observeForever(preferences -> {
                        distributeIncome(transaction.getAmount(), preferences, transactionId);
                    });
                }
            });
    }

    public void updateDistributedTransactions(String parentTransactionId, double newAmount, UserPreferences preferences) {
        String userId = auth.getCurrentUser().getUid();
        
        db.collection("users")
            .document(userId)
            .collection("transactions")
            .whereEqualTo("parentTransactionId", parentTransactionId)
            .get()
            .addOnSuccessListener(querySnapshot -> {
                double needsAmount = newAmount * (preferences.getNeedsPercentage() / 100.0);
                double wantsAmount = newAmount * (preferences.getWantsPercentage() / 100.0);
                double savingsAmount = newAmount * (preferences.getSavingsPercentage() / 100.0);

                for (QueryDocumentSnapshot document : querySnapshot) {
                    Transaction transaction = document.toObject(Transaction.class);
                    switch (transaction.getType()) {
                        case "NEEDS":
                            transaction.setAmount(needsAmount);
                            break;
                        case "WANTS":
                            transaction.setAmount(wantsAmount);
                            break;
                        case "SAVINGS":
                            transaction.setAmount(savingsAmount);
                            break;
                    }
                    document.getReference().set(transaction);
                }
            });
    }
}
