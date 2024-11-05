package com.elececo.umoney.ui.savings.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.elececo.umoney.data.model.Transaction;
import com.elececo.umoney.data.repository.TransactionRepository;
import java.util.List;

public class SavingsViewModel extends ViewModel {
    private final TransactionRepository repository;
    private final LiveData<List<Transaction>> transactions;

    public SavingsViewModel() {
        repository = new TransactionRepository();
        transactions = repository.getTransactionsByType("SAVINGS");
    }

    public void saveTransaction(Transaction transaction) {
        repository.saveTransaction(transaction);
    }

    public LiveData<List<Transaction>> getTransactions() {
        return transactions;
    }
}
