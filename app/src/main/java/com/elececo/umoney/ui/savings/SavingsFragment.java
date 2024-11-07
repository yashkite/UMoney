package com.elececo.umoney.ui.savings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.elececo.umoney.R;
import com.elececo.umoney.ui.base.BaseFragment;
import com.elececo.umoney.ui.savings.viewmodel.SavingsViewModel;

public class SavingsFragment extends BaseFragment<SavingsViewModel> {
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_savings, container, false);
    }

    @Override
    protected Class<SavingsViewModel> getViewModelClass() {
        return SavingsViewModel.class;
    }

    @Override
    protected String getTransactionType() {
        return "SAVINGS";
    }

    @Override
    protected String getCardTitle() {
        return "Savings Summary";
    }

    @Override
    protected boolean isExpenseType() {
        return false;
    }
}
