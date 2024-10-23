package com.elececo.umoney.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.elececo.umoney.model.Transaction;
import com.elececo.umoney.repository.TransactionRepository;

import java.util.List;

public class TransactionViewModel extends ViewModel {
    private TransactionRepository repository;
    private LiveData<List<Transaction>> transactions;

    public TransactionViewModel() {
        repository = new TransactionRepository();
    }

    public LiveData<List<Transaction>> getTransactions() {
        return transactions;
    }

    public void loadTransactionsByCategory(String category) {
        transactions = repository.getTransactionsByCategory(category);
    }

    public void addTransaction(Transaction transaction) {
        repository.addTransaction(transaction);
    }
}
