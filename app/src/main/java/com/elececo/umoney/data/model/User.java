package com.elececo.umoney.data.model;

import com.google.firebase.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class User {
    private String userId;
    private String displayName;
    private String email;
    private String photoUrl;
    private Timestamp dateOfBirth;
    private String employmentType;
    private String currency;
    private double monthlyIncome;
    private Map<String, Integer> distributionRules;
    private boolean firstTimeUser = true;

    // Required empty constructor for Firestore
    public User() {
        distributionRules = new HashMap<>();
        initializeDefaultDistribution();
        this.firstTimeUser = true;
    }

    // Constructor with all required fields
    public User(String userId, String displayName, String email, String photoUrl) {
        this.userId = userId;
        this.displayName = displayName;
        this.email = email;
        this.photoUrl = photoUrl;
        this.distributionRules = new HashMap<>();
        initializeDefaultDistribution();
        this.firstTimeUser = true;
    }

    private void initializeDefaultDistribution() {
        distributionRules.put("needs", 50);
        distributionRules.put("wants", 30);
        distributionRules.put("savings", 20);
    }

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public Timestamp getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(Timestamp dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getEmploymentType() { return employmentType; }
    public void setEmploymentType(String employmentType) { this.employmentType = employmentType; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public double getMonthlyIncome() { return monthlyIncome; }
    public void setMonthlyIncome(double monthlyIncome) { this.monthlyIncome = monthlyIncome; }

    public Map<String, Integer> getDistributionRules() { return distributionRules; }
    public void setDistributionRules(Map<String, Integer> distributionRules) { 
        this.distributionRules = distributionRules; 
    }

    public boolean isFirstTimeUser() {
        return firstTimeUser;
    }

    public void setFirstTimeUser(boolean firstTimeUser) {
        this.firstTimeUser = firstTimeUser;
    }
}