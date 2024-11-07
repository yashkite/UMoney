package com.elececo.umoney.ui.needs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.elececo.umoney.R;
import com.elececo.umoney.ui.base.BaseFragment;
import com.elececo.umoney.ui.needs.viewmodel.NeedsViewModel;

public class NeedsFragment extends BaseFragment<NeedsViewModel> {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_needs, container, false);
    }

    @Override
    protected Class<NeedsViewModel> getViewModelClass() {
        return NeedsViewModel.class;
    }

    @Override
    protected String getTransactionType() {
        return "NEEDS";
    }

    @Override
    protected String getCardTitle() {
        return "Needs Expenses";
    }

    @Override
    protected boolean isExpenseType() {
        return true;
    }
} 