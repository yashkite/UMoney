package com.elececo.umoney.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.elececo.umoney.model.Transaction;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.List;

public class TransactionRepository {
    private FirebaseFirestore db;

    public TransactionRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public void addTransaction(Transaction transaction) {
        db.collection("transactions").add(transaction);
    }

    public LiveData<List<Transaction>> getTransactionsByCategory(String category) {
        MutableLiveData<List<Transaction>> transactionsLiveData = new MutableLiveData<>();

        Query query = db.collection("transactions")
                .whereEqualTo("category", category)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        ListenerRegistration listener = query.addSnapshotListener((value, error) -> {
            if (error != null) {
                // Handle error
                return;
            }
            if (value != null) {
                List<Transaction> transactions = value.toObjects(Transaction.class);
                transactionsLiveData.setValue(transactions);
            }
        });

        return transactionsLiveData;
    }
}
