package com.elececo.umoney.data.model;

public class DashboardData {
    private double monthlyIncome;
    private double totalNeeds;
    private double totalWants;
    private double totalSavings;

    public DashboardData(double monthlyIncome, double totalNeeds, double totalWants, double totalSavings) {
        this.monthlyIncome = monthlyIncome;
        this.totalNeeds = totalNeeds;
        this.totalWants = totalWants;
        this.totalSavings = totalSavings;
    }

    public double getMonthlyIncome() { return monthlyIncome; }
    public double getTotalNeeds() { return totalNeeds; }
    public double getTotalWants() { return totalWants; }
    public double getTotalSavings() { return totalSavings; }
} 