package com.elececo.umoney.ui.needs.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.elececo.umoney.data.model.Transaction;
import com.elececo.umoney.data.repository.TransactionRepository;
import java.util.List;

public class NeedsViewModel extends ViewModel {
    private final TransactionRepository repository;
    private final LiveData<List<Transaction>> transactions;

    public NeedsViewModel() {
        repository = new TransactionRepository();
        transactions = repository.getTransactionsByType("NEEDS");
    }

    public void saveTransaction(Transaction transaction) {
        repository.saveTransaction(transaction);
    }

    public LiveData<List<Transaction>> getTransactions() {
        return transactions;
    }
}
