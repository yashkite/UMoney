package com.elececo.umoney.ui.dashboard.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.elececo.umoney.R;
import com.elececo.umoney.ui.base.BaseFragment;
import com.elececo.umoney.ui.dashboard.viewmodel.DashboardViewModel;
import com.google.android.material.card.MaterialCardView;
import java.text.NumberFormat;
import java.util.Locale;

public class DashboardFragment extends BaseFragment<DashboardViewModel> {
    private TextView totalNeedsAmount;
    private TextView totalWantsAmount;
    private TextView totalSavingsAmount;
    private TextView monthlyIncomeAmount;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);
        setupObservers();
    }
    
    private void setupViews(View view) {
        totalNeedsAmount = view.findViewById(R.id.needs_amount);
        totalWantsAmount = view.findViewById(R.id.wants_amount);
        totalSavingsAmount = view.findViewById(R.id.savings_amount);
        monthlyIncomeAmount = view.findViewById(R.id.monthly_income);
    }
    
    @Override
    protected void setupObservers() {
        super.setupObservers();
        viewModel.getDashboardData().observe(getViewLifecycleOwner(), data -> {
            NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
            totalNeedsAmount.setText(format.format(data.getTotalNeeds()));
            totalWantsAmount.setText(format.format(data.getTotalWants()));
            totalSavingsAmount.setText(format.format(data.getTotalSavings()));
            monthlyIncomeAmount.setText(format.format(data.getMonthlyIncome()));
        });
    }
    
    @Override
    protected Class<DashboardViewModel> getViewModelClass() {
        return DashboardViewModel.class;
    }
    
    @Override
    protected String getTransactionType() {
        return "DASHBOARD";
    }
    
    @Override
    protected String getCardTitle() {
        return "Dashboard Summary";
    }
    
    @Override
    protected boolean isExpenseType() {
        return false;
    }
    
    @Override
    protected boolean hasTransactionList() {
        return false;  // Dashboard doesn't show transaction list
    }
} 