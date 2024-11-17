package com.elececo.umoney.data.model;
public class UserPreferences {
    private int needsPercentage;
    private int wantsPercentage;
    private int savingsPercentage;

    public UserPreferences(int needsPercentage, int wantsPercentage, int savingsPercentage) {
        this.needsPercentage = needsPercentage;
        this.wantsPercentage = wantsPercentage;
        this.savingsPercentage = savingsPercentage;
    }

    public int getNeedsPercentage() { return needsPercentage; }
    public int getWantsPercentage() { return wantsPercentage; }
    public int getSavingsPercentage() { return savingsPercentage; }
} 