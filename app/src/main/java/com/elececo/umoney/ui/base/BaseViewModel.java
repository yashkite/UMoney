package com.elececo.umoney.ui.base;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.elececo.umoney.data.model.Transaction;
import com.elececo.umoney.data.repository.TransactionRepository;
import java.util.List;
import com.google.firebase.firestore.FirebaseFirestore;
import com.elececo.umoney.data.repository.UserPreferencesRepository;
import com.elececo.umoney.data.model.UserPreferences;
import android.util.Log;
import com.google.android.gms.tasks.Task;

public abstract class BaseViewModel extends ViewModel {
    protected final TransactionRepository repository;
    protected final UserPreferencesRepository preferencesRepository;
    private final LiveData<List<Transaction>> transactions;

    public BaseViewModel() {
        repository = new TransactionRepository();
        preferencesRepository = new UserPreferencesRepository();
        transactions = repository.getTransactionsByType(getTransactionType());
    }

    public LiveData<List<Transaction>> getTransactions() {
        return transactions;
    }

    public Task<Void> saveTransaction(Transaction transaction) {
        return repository.saveTransaction(transaction);
    }

    public void deleteTransaction(Transaction transaction) {
        repository.deleteTransaction(transaction);
    }

    protected abstract String getTransactionType();

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.cleanup();
    }

    public FirebaseFirestore getFirestore() {
        return repository.getFirestore();
    }

    public LiveData<UserPreferences> getUserPreferences() {
        return preferencesRepository.getUserPreferences();
    }
} 