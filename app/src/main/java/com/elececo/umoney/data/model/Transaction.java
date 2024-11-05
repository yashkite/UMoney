package com.elececo.umoney.data.model;

import com.google.firebase.Timestamp;

public class Transaction {
    private String id;
    private double amount;
    private String type; // INCOME, NEEDS, WANTS, SAVINGS
    private Timestamp timestamp;
    private String recipient;
    private String category;
    private String attachmentUrl;
    private String description;
    private String status; // PENDING, COMPLETED
    private String source; // MANUAL, SMS, EMAIL, API

    // Constructor, getters, and setters
    public Transaction() {
        // Required empty constructor for Firestore
    }
} 