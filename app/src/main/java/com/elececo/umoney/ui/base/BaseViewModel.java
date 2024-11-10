package com.elececo.umoney.ui.base;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.elececo.umoney.data.model.Transaction;
import com.elececo.umoney.data.repository.TransactionRepository;
import java.util.List;

public abstract class BaseViewModel extends ViewModel {
    protected final TransactionRepository repository;
    private final LiveData<List<Transaction>> transactions;

    public BaseViewModel() {
        repository = new TransactionRepository();
        transactions = repository.getTransactionsByType(getTransactionType());
    }

    public LiveData<List<Transaction>> getTransactions() {
        return transactions;
    }

    public void saveTransaction(Transaction transaction) {
        repository.saveTransaction(transaction);
    }

    protected abstract String getTransactionType();

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.cleanup();
    }
} 