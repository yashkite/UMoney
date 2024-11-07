package com.elececo.umoney.ui.dashboard.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.elececo.umoney.R;
import com.elececo.umoney.ui.base.BaseFragment;
import com.elececo.umoney.ui.dashboard.viewmodel.DashboardViewModel;

public class DashboardFragment extends BaseFragment<DashboardViewModel> {
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
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
        return false; // Dashboard doesn't have transaction list
    }
} 