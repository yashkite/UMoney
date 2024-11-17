package com.elececo.umoney.data.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.elececo.umoney.data.model.Transaction;
import com.elececo.umoney.data.model.UserPreferences;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.WriteBatch;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.elececo.umoney.utils.DriveServiceHelper;

public class TransactionRepository {
    private static final String COLLECTION_USERS = "users";
    private static final String COLLECTION_TRANSACTIONS = "transactions";
    private final FirebaseFirestore db;
    private final FirebaseAuth auth;
    private final Map<String, MutableLiveData<List<Transaction>>> transactionListMap;
    private final Map<String, ListenerRegistration> listeners;
    private final Context context;

    public TransactionRepository(Context context) {
        this.context = context.getApplicationContext();
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
        this.transactionListMap = new HashMap<>();
        this.listeners = new HashMap<>();
    }

    public LiveData<List<Transaction>> getTransactionsByType(String type) {
        if (!transactionListMap.containsKey(type)) {
            transactionListMap.put(type, new MutableLiveData<>(new ArrayList<>()));
            setupRealtimeUpdates(type);
        }
        return transactionListMap.get(type);
    }

    public void setupRealtimeUpdates(String type) {
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
        if (auth.getCurrentUser() == null || transactions.isEmpty()) return;

        String userId = auth.getCurrentUser().getUid();
        WriteBatch batch = db.batch();
        Set<String> typesToUpdate = new HashSet<>();

        Log.d("TransactionRepository", "Saving distributed transactions: " + transactions.size());

        for (Transaction transaction : transactions) {
            DocumentReference docRef = db.collection(COLLECTION_USERS)
                .document(userId)
                .collection(COLLECTION_TRANSACTIONS)
                .document();
            transaction.setId(docRef.getId());
            batch.set(docRef, transaction);
            typesToUpdate.add(transaction.getType());
            Log.d("TransactionRepository", "Added to batch: " + transaction.getType() + 
                " Amount: " + transaction.getAmount() + 
                " ParentId: " + transaction.getParentTransactionId());
        }

        batch.commit()
            .addOnSuccessListener(aVoid -> {
                Log.d("TransactionRepository", "Successfully saved distributed transactions");
                for (String type : typesToUpdate) {
                    setupRealtimeUpdates(type);
                }
                setupRealtimeUpdates("INCOME");
            })
            .addOnFailureListener(e -> {
                Log.e("TransactionRepository", "Failed to save distributed transactions", e);
            });
    }

    public Task<Void> saveTransaction(Transaction transaction) {
        if (auth.getCurrentUser() == null) return Tasks.forException(new Exception("User not authenticated"));
        
        String userId = auth.getCurrentUser().getUid();
        Log.d("TransactionRepository", "Saving transaction with ID: " + transaction.getId());
        
        if (transaction.getId() != null) {
            return db.collection(COLLECTION_USERS)
                .document(userId)
                .collection(COLLECTION_TRANSACTIONS)
                .document(transaction.getId())
                .set(transaction)
                .addOnSuccessListener(aVoid -> {
                    Log.d("TransactionRepository", "Successfully updated transaction: " + transaction.getId());
                    setupRealtimeUpdates(transaction.getType());
                });
        } else {
            DocumentReference docRef = db.collection(COLLECTION_USERS)
                .document(userId)
                .collection(COLLECTION_TRANSACTIONS)
                .document();
            
            transaction.setId(docRef.getId());
            Log.d("TransactionRepository", "Created new transaction with ID: " + transaction.getId());
            return docRef.set(transaction)
                .addOnSuccessListener(aVoid -> {
                    Log.d("TransactionRepository", "Successfully saved new transaction: " + transaction.getId());
                    setupRealtimeUpdates(transaction.getType());
                });
        }
    }

    public void deleteTransaction(Transaction transaction) {
        if (auth.getCurrentUser() == null) return;
        
        String userId = auth.getCurrentUser().getUid();
        
        // Delete attachment from Drive if exists
        if (transaction.getDriveFileId() != null) {
            try {
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
                if (account != null) {
                    DriveServiceHelper driveHelper = new DriveServiceHelper(context, account);
                    driveHelper.deleteFile(transaction.getDriveFileId())
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d("TransactionRepository", "Attachment deleted successfully");
                            } else {
                                Log.e("TransactionRepository", 
                                    "Failed to delete attachment: " + task.getException().getMessage());
                            }
                            // Continue with transaction deletion regardless of attachment deletion result
                            deleteTransactionFromFirestore(transaction, userId);
                        });
                } else {
                    deleteTransactionFromFirestore(transaction, userId);
                }
            } catch (Exception e) {
                Log.e("TransactionRepository", "Failed to initialize Drive: " + e.getMessage());
                deleteTransactionFromFirestore(transaction, userId);
            }
        } else {
            deleteTransactionFromFirestore(transaction, userId);
        }
    }

    private void deleteTransactionFromFirestore(Transaction transaction, String userId) {
        db.collection(COLLECTION_USERS)
            .document(userId)
            .collection(COLLECTION_TRANSACTIONS)
            .whereEqualTo("parentTransactionId", transaction.getId())
            .get()
            .addOnSuccessListener(querySnapshot -> {
                WriteBatch batch = db.batch();
                Set<String> typesToUpdate = new HashSet<>();
                
                // Add all distributed transactions to delete batch
                for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                    batch.delete(doc.getReference());
                    Transaction distributedTransaction = doc.toObject(Transaction.class);
                    if (distributedTransaction != null) {
                        typesToUpdate.add(distributedTransaction.getType());
                    }
                }
                
                // Add main transaction to delete batch
                DocumentReference mainTransactionRef = db.collection(COLLECTION_USERS)
                    .document(userId)
                    .collection(COLLECTION_TRANSACTIONS)
                    .document(transaction.getId());
                batch.delete(mainTransactionRef);
                
                // Commit the batch
                batch.commit()
                    .addOnSuccessListener(aVoid -> {
                        for (String type : typesToUpdate) {
                            setupRealtimeUpdates(type);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("TransactionRepository", "Failed to delete transactions", e);
                    });
            });
    }

    public void updateTransaction(Transaction transaction) {
        if (auth.getCurrentUser() != null) {
            String userId = auth.getCurrentUser().getUid();
            db.collection(COLLECTION_USERS)
                .document(userId)
                .collection(COLLECTION_TRANSACTIONS)
                .document(transaction.getId())
                .set(transaction)
                .addOnSuccessListener(aVoid -> {
                    setupRealtimeUpdates(transaction.getType());
                });
        }
    }

    public void updateDistributedTransactions(List<Transaction> transactions) {
        if (auth.getCurrentUser() == null || transactions.isEmpty()) return;

        String userId = auth.getCurrentUser().getUid();
        WriteBatch batch = db.batch();
        Set<String> typesToUpdate = new HashSet<>();

        for (Transaction transaction : transactions) {
            DocumentReference docRef = db.collection(COLLECTION_USERS)
                .document(userId)
                .collection(COLLECTION_TRANSACTIONS)
                .document(transaction.getId());
            batch.set(docRef, transaction);
            typesToUpdate.add(transaction.getType());
        }

        batch.commit()
            .addOnSuccessListener(aVoid -> {
                // Force a refresh for all affected transaction types
                for (String type : typesToUpdate) {
                    setupRealtimeUpdates(type);
                }
            })
            .addOnFailureListener(e -> {
                // Handle failure
                Log.e("TransactionRepository", "Failed to update distributed transactions", e);
            });
    }

    public void cleanup() {
        for (ListenerRegistration registration : listeners.values()) {
            registration.remove();
        }
        listeners.clear();
    }

    public FirebaseFirestore getFirestore() {
        return db;
    }

    public void updateDistributedTransaction(Transaction parentTransaction, UserPreferences prefs) {
        if (auth.getCurrentUser() == null) return;
        
        String userId = auth.getCurrentUser().getUid();
        Log.d("TransactionRepository", "Updating distributed transactions for parent: " + 
            parentTransaction.getId());

        db.collection(COLLECTION_USERS)
            .document(userId)
            .collection(COLLECTION_TRANSACTIONS)
            .whereEqualTo("parentTransactionId", parentTransaction.getId())
            .get()
            .addOnSuccessListener(querySnapshot -> {
                Log.d("TransactionRepository", "Found " + querySnapshot.size() + 
                    " distributed transactions to update");
                WriteBatch batch = db.batch();
                Set<String> typesToUpdate = new HashSet<>();
                
                double amount = Math.abs(parentTransaction.getAmount());
                
                for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                    Transaction distributedTransaction = doc.toObject(Transaction.class);
                    if (distributedTransaction != null) {
                        double oldAmount = distributedTransaction.getAmount();
                        distributedTransaction.setTimestamp(parentTransaction.getTimestamp());
                        updateDistributedAmount(distributedTransaction, amount, prefs);
                        Log.d("TransactionRepository", "Updating " + distributedTransaction.getType() + 
                            " from " + oldAmount + " to " + distributedTransaction.getAmount());
                        batch.set(doc.getReference(), distributedTransaction);
                        typesToUpdate.add(distributedTransaction.getType());
                    }
                }
                
                commitBatchAndRefresh(batch, typesToUpdate);
            })
            .addOnFailureListener(e -> {
                Log.e("TransactionRepository", "Failed to update distributed transactions", e);
            });
    }

    private void updateDistributedAmount(Transaction transaction, double parentAmount, UserPreferences prefs) {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        String parentAmountStr = format.format(Math.abs(parentAmount));
        transaction.setRecipient("From Income: " + parentAmountStr);

        switch (transaction.getType()) {
            case "NEEDS":
                transaction.setAmount((parentAmount * prefs.getNeedsPercentage()) / 100);
                break;
            case "WANTS":
                transaction.setAmount((parentAmount * prefs.getWantsPercentage()) / 100);
                break;
            case "SAVINGS":
                transaction.setAmount((parentAmount * prefs.getSavingsPercentage()) / 100);
                break;
        }
    }

    private void commitBatchAndRefresh(WriteBatch batch, Set<String> typesToUpdate) {
        batch.commit()
            .addOnSuccessListener(aVoid -> {
                // Force a refresh for all affected transaction types
                for (String type : typesToUpdate) {
                    setupRealtimeUpdates(type);
                }
                // Also refresh INCOME type to ensure consistency
                setupRealtimeUpdates("INCOME");
            })
            .addOnFailureListener(e -> {
                Log.e("TransactionRepository", "Failed to commit batch operation", e);
            });
    }
} 