package com.elececo.umoney.data.model;

import java.util.Date;

public class Transaction {
    private String id;
    private double amount;
    private String category;
    private String recipient;
    private String notes;
    private Date timestamp;
    private String type;
    private String driveFileId;
    private String driveFileName;
    private String parentTransactionId;

    // Default constructor for Firestore
    public Transaction() {}

    // Parameterized constructor
    public Transaction(double amount, Date timestamp, String recipient, String category, String type) {
        this.amount = amount;
        this.timestamp = timestamp;
        this.recipient = recipient;
        this.category = category;
        this.type = type;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) { this.recipient = recipient; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getDriveFileId() { return driveFileId; }
    public void setDriveFileId(String driveFileId) { this.driveFileId = driveFileId; }
    
    public String getDriveFileName() { return driveFileName; }
    public void setDriveFileName(String driveFileName) { this.driveFileName = driveFileName; }
    
    public String getParentTransactionId() { return parentTransactionId; }
    public void setParentTransactionId(String parentTransactionId) { this.parentTransactionId = parentTransactionId; }
} 