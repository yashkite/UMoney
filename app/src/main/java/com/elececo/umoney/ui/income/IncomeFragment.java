package com.elececo.umoney.ui.income;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.elececo.umoney.R;
import com.elececo.umoney.ui.base.BaseFragment;
import com.elececo.umoney.ui.income.viewmodel.IncomeViewModel;

public class IncomeFragment extends BaseFragment<IncomeViewModel> {
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_income, container, false);
    }
    
    @Override
    protected Class<IncomeViewModel> getViewModelClass() {
        return IncomeViewModel.class;
    }
    
    @Override
    protected void setupObservers() {
        // TODO: Setup observers for income data
    }
}
