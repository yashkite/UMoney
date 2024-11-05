package com.elececo.umoney.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.elececo.umoney.data.model.Transaction;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.List;

public class TransactionRepository {
    private static final String COLLECTION_TRANSACTIONS = "transactions";
    private final FirebaseFirestore db;
    private final FirebaseAuth auth;

    public TransactionRepository() {
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
    }

    public void saveTransaction(Transaction transaction) {
        if (auth.getCurrentUser() != null) {
            transaction.setUserId(auth.getCurrentUser().getUid());
            db.collection(COLLECTION_TRANSACTIONS)
                .add(transaction);
        }
    }

    public LiveData<List<Transaction>> getTransactionsByType(String type) {
        MutableLiveData<List<Transaction>> transactionsLiveData = new MutableLiveData<>();

        if (auth.getCurrentUser() != null) {
            db.collection(COLLECTION_TRANSACTIONS)
                .whereEqualTo("userId", auth.getCurrentUser().getUid())
                .whereEqualTo("type", type)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) {
                        return;
                    }
                    transactionsLiveData.setValue(value.toObjects(Transaction.class));
                });
        }

        return transactionsLiveData;
    }
} 