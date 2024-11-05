package com.elececo.umoney.data.repository;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.elececo.umoney.data.model.Transaction;
import com.elececo.umoney.data.model.User;

public class FirestoreRepository {
    private final FirebaseFirestore db;
    private final String currentUserId;

    public FirestoreRepository(String userId) {
        this.db = FirebaseFirestore.getInstance();
        this.currentUserId = userId;
    }

    // User operations
    public DocumentReference getUserProfile() {
        return db.collection("users")
                 .document(currentUserId)
                 .collection("profile")
                 .document("userProfile");
    }

    // Transaction operations
    public CollectionReference getTransactions() {
        return db.collection("users")
                 .document(currentUserId)
                 .collection("transactions");
    }

    // Categories operations
    public CollectionReference getCategories() {
        return db.collection("users")
                 .document(currentUserId)
                 .collection("categories");
    }

    // Recipients operations
    public CollectionReference getRecipients() {
        return db.collection("users")
                 .document(currentUserId)
                 .collection("recipients");
    }
} 