package com.elececo.umoney.ui.dashboard.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
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
        Log.d("DashboardFragment", "Setting up observers");
        
        viewModel.getDashboardData().observe(getViewLifecycleOwner(), data -> {
            if (data == null) {
                Log.e("DashboardFragment", "Dashboard data is null");
                return;
            }
            
            Log.d("DashboardFragment", String.format("Updating UI with data - Monthly Income: %.2f, " +
                "Needs Hold: %.2f, Wants Hold: %.2f, Savings Hold: %.2f",
                data.getMonthlyIncome(), data.getNeedsHoldAmount(), 
                data.getWantsHoldAmount(), data.getSavingsHoldAmount()));
            
            NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
            
            updateAmount(totalNeedsAmount, data.getNeedsHoldAmount(), format, "needs");
            updateAmount(totalWantsAmount, data.getWantsHoldAmount(), format, "wants");
            updateAmount(totalSavingsAmount, data.getSavingsHoldAmount(), format, "savings");
            updateAmount(monthlyIncomeAmount, data.getMonthlyIncome(), format, "income");
        });
    }
    
    private void updateAmount(TextView textView, double amount, NumberFormat format, String field) {
        if (textView != null) {
            String formattedAmount = format.format(amount);
            textView.setText(formattedAmount);
            Log.d("DashboardFragment", "Updated " + field + " amount to: " + formattedAmount);
        } else {
            Log.e("DashboardFragment", field + " TextView is null");
        }
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
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
} 