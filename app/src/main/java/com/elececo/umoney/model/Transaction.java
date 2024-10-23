package com.elececo.umoney.model;

import com.google.firebase.Timestamp;

public class Transaction {
    private double amount;
    private Timestamp timestamp;
    private String description;
    private String category;

    // Default constructor for Firestore
    public Transaction() {}

    public Transaction(double amount, Timestamp timestamp, String description, String category) {
        this.amount = amount;
        this.timestamp = timestamp;
        this.description = description;
        this.category = category;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
