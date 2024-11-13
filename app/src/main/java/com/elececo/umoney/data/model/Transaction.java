package com.elececo.umoney.data.model;

import java.util.Date;
import java.util.Objects;

public class Transaction {
    private String id;
    private double amount;
    private Date timestamp;
    private String recipient;
    private String category;
    private String type;
    private String notes;
    private String attachmentUri;
    private String parentTransactionId;

    // Required for Firestore
    public Transaction() {}

    public Transaction(double amount, Date timestamp, String recipient, String category, String type) {
        this.amount = amount;
        this.timestamp = timestamp;
        this.recipient = recipient;
        this.category = category;
        this.type = type;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getAttachmentUri() {
        return attachmentUri;
    }

    public void setAttachmentUri(String attachmentUri) {
        this.attachmentUri = attachmentUri;
    }

    public String getParentTransactionId() {
        return parentTransactionId;
    }

    public void setParentTransactionId(String parentTransactionId) {
        this.parentTransactionId = parentTransactionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Transaction that = (Transaction) o;
        return Double.compare(that.amount, amount) == 0 &&
               Objects.equals(id, that.id) &&
               Objects.equals(timestamp, that.timestamp) &&
               Objects.equals(recipient, that.recipient) &&
               Objects.equals(category, that.category) &&
               Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, amount, timestamp, recipient, category, type);
    }
} 