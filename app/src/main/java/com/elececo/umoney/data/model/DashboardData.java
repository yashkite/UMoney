package com.elececo.umoney.data.model;

public class DashboardData {
    private double monthlyIncome;
    private double needsHoldAmount;
    private double wantsHoldAmount;
    private double savingsHoldAmount;

    public DashboardData(double monthlyIncome, double needsHoldAmount, 
                        double wantsHoldAmount, double savingsHoldAmount) {
        this.monthlyIncome = monthlyIncome;
        this.needsHoldAmount = needsHoldAmount;
        this.wantsHoldAmount = wantsHoldAmount;
        this.savingsHoldAmount = savingsHoldAmount;
    }

    public double getMonthlyIncome() { return monthlyIncome; }
    public double getNeedsHoldAmount() { return needsHoldAmount; }
    public double getWantsHoldAmount() { return wantsHoldAmount; }
    public double getSavingsHoldAmount() { return savingsHoldAmount; }
} 