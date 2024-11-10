package com.elececo.umoney.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.elececo.umoney.data.model.Transaction;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class TransactionRepository {
    private static final String COLLECTION_USERS = "users";
    private static final String COLLECTION_TRANSACTIONS = "transactions";
    private final FirebaseFirestore db;
    private final FirebaseAuth auth;
    private final Map<String, MutableLiveData<List<Transaction>>> transactionListMap;
    private final Map<String, ListenerRegistration> listeners = new HashMap<>();

    public TransactionRepository() {
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
        this.transactionListMap = new HashMap<>();
    }

    public LiveData<List<Transaction>> getTransactionsByType(String type) {
        if (!transactionListMap.containsKey(type)) {
            transactionListMap.put(type, new MutableLiveData<>(new ArrayList<>()));
            setupRealtimeUpdates(type);
        }
        return transactionListMap.get(type);
    }

    private void setupRealtimeUpdates(String type) {
        if (auth.getCurrentUser() != null) {
            String userId = auth.getCurrentUser().getUid();
            db.collection(COLLECTION_USERS)
                .document(userId)
                .collection(COLLECTION_TRANSACTIONS)
                .whereEqualTo("type", type)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null || snapshots == null) {
                        return;
                    }
                    List<Transaction> transactions = snapshots.toObjects(Transaction.class);
                    if (transactionListMap.containsKey(type)) {
                        transactionListMap.get(type).postValue(transactions);
                    }
                });
        }
    }

    public void saveDistributedTransactions(List<Transaction> transactions) {
        if (auth.getCurrentUser() == null) return;

        String userId = auth.getCurrentUser().getUid();
        WriteBatch batch = db.batch();
        Set<String> typesToUpdate = new HashSet<>();

        for (Transaction transaction : transactions) {
            transaction.setUserId(userId);
            DocumentReference docRef = db.collection(COLLECTION_USERS)
                .document(userId)
                .collection(COLLECTION_TRANSACTIONS)
                .document();
            transaction.setId(docRef.getId());
            batch.set(docRef, transaction);
            typesToUpdate.add(transaction.getType());
        }

        batch.commit()
            .addOnSuccessListener(aVoid -> {
                // Force a refresh for all affected transaction types
                for (String type : typesToUpdate) {
                    setupRealtimeUpdates(type);
                }
            });
    }

    public void saveTransaction(Transaction transaction) {
        if (auth.getCurrentUser() != null) {
            String userId = auth.getCurrentUser().getUid();
            transaction.setUserId(userId);
            
            db.collection(COLLECTION_USERS)
                .document(userId)
                .collection(COLLECTION_TRANSACTIONS)
                .add(transaction)
                .addOnSuccessListener(documentReference -> {
                    String id = documentReference.getId();
                    transaction.setId(id);
                    documentReference.update("id", id)
                        .addOnSuccessListener(aVoid -> {
                            // Force a refresh of the transaction list
                            setupRealtimeUpdates(transaction.getType());
                        });
                });
        }
    }

    public void cleanup() {
        for (ListenerRegistration registration : listeners.values()) {
            registration.remove();
        }
        listeners.clear();
    }
} 