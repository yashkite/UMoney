package com.elececo.umoney.ui.savings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.elececo.umoney.R;
import com.elececo.umoney.ui.base.BaseFragment;
import com.elececo.umoney.ui.savings.viewmodel.SavingsViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class SavingsFragment extends BaseFragment<SavingsViewModel> {
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_savings, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Set card title
        TextView cardTitle = view.findViewById(R.id.card_title);
        cardTitle.setText("Savings Summary");
        
        // Setup FAB
        FloatingActionButton fab = view.findViewById(R.id.fab_action);
        fab.setImageResource(R.drawable.ic_add);
        fab.setOnClickListener(v -> showAddTransactionDialog());
    }
    
    @Override
    protected Class<SavingsViewModel> getViewModelClass() {
        return SavingsViewModel.class;
    }
    
    @Override
    protected void setupObservers() {
        // TODO: Setup observers for savings data
    }
    
    private void showAddTransactionDialog() {
        // TODO: Implement add transaction dialog according to CoreLogic.md
    }
}
