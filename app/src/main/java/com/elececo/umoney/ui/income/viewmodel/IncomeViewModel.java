package com.elececo.umoney.ui.income.viewmodel;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import com.elececo.umoney.data.model.Transaction;
import com.elececo.umoney.data.model.UserPreferences;
import com.elececo.umoney.ui.base.BaseViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class IncomeViewModel extends BaseViewModel {
    public IncomeViewModel() {
        super();
    }

    @Override
    protected String getTransactionType() {
        return "INCOME";
    }

    public LiveData<UserPreferences> getUserPreferences() {
        return preferencesRepository.getUserPreferences();
    }

    public void createDistributedTransactions(Transaction transaction) {
        repository.saveTransaction(transaction)
            .addOnSuccessListener(aVoid -> {
                Log.d("IncomeViewModel", "Parent transaction saved with ID: " + transaction.getId());
                getUserPreferences().observeForever(new Observer<UserPreferences>() {
                    @Override
                    public void onChanged(UserPreferences prefs) {
                        if (transaction.getId() == null) {
                            Log.e("IncomeViewModel", "Parent transaction ID is null");
                            return;
                        }
                        List<Transaction> distributedTransactions = createDistributionTransactions(transaction, prefs);
                        Log.d("IncomeViewModel", "Created " + distributedTransactions.size() + " distributed transactions");
                        repository.saveDistributedTransactions(distributedTransactions);
                        getUserPreferences().removeObserver(this);
                    }
                });
            })
            .addOnFailureListener(e -> {
                Log.e("IncomeViewModel", "Failed to save parent transaction", e);
            });
    }

    public void updateDistributedTransactions(Transaction transaction) {
        getUserPreferences().observeForever(new Observer<UserPreferences>() {
            @Override
            public void onChanged(UserPreferences prefs) {
                repository.updateDistributedTransaction(transaction, prefs);
                getUserPreferences().removeObserver(this);
            }
        });
    }

    private List<Transaction> createDistributionTransactions(Transaction parent, UserPreferences prefs) {
        double amount = Math.abs(parent.getAmount());
        
        Transaction needs = createDistributedTransaction(
            (amount * prefs.getNeedsPercentage()) / 100,
            parent,
            "NEEDS"
        );
        
        Transaction wants = createDistributedTransaction(
            (amount * prefs.getWantsPercentage()) / 100,
            parent,
            "WANTS"
        );
        
        Transaction savings = createDistributedTransaction(
            (amount * prefs.getSavingsPercentage()) / 100,
            parent,
            "SAVINGS"
        );
        
        needs.setParentTransactionId(parent.getId());
        wants.setParentTransactionId(parent.getId());
        savings.setParentTransactionId(parent.getId());
        
        return Arrays.asList(needs, wants, savings);
    }

    private Transaction createDistributedTransaction(double amount, Transaction parent, String type) {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        String parentAmountStr = format.format(Math.abs(parent.getAmount()));
        
        Transaction transaction = new Transaction(
            amount,
            parent.getTimestamp(),
            "From Income: " + parentAmountStr,
            "Income Distribution",
            type
        );
        transaction.setParentTransactionId(parent.getId());
        transaction.setNotes("Distributed from income: " + parent.getCategory());
        
        Log.d("IncomeViewModel", "Created distributed transaction - Type: " + type + 
            ", Amount: " + amount + 
            ", ParentId: " + parent.getId());
        
        return transaction;
    }

    public void refreshTransactionType(String type) {
        repository.setupRealtimeUpdates(type);
    }
}
