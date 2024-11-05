package com.elececo.umoney.ui.wants.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.elececo.umoney.data.model.Transaction;
import com.elececo.umoney.data.repository.TransactionRepository;
import java.util.List;

public class WantsViewModel extends ViewModel {
    private final TransactionRepository repository;
    private final LiveData<List<Transaction>> transactions;

    public WantsViewModel() {
        repository = new TransactionRepository();
        transactions = repository.getTransactionsByType("WANTS");
    }

    public void saveTransaction(Transaction transaction) {
        repository.saveTransaction(transaction);
    }

    public LiveData<List<Transaction>> getTransactions() {
        return transactions;
    }
}
