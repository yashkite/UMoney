package com.elececo.umoney.ui.wants;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.elececo.umoney.R;
import com.elececo.umoney.ui.base.BaseFragment;
import com.elececo.umoney.ui.wants.viewmodel.WantsViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class WantsFragment extends BaseFragment<WantsViewModel> {
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wants, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Set card title
        TextView cardTitle = view.findViewById(R.id.card_title);
        cardTitle.setText("Wants Summary");
        
        // Setup FAB
        FloatingActionButton fab = view.findViewById(R.id.fab_action);
        fab.setImageResource(R.drawable.ic_add);
        fab.setOnClickListener(v -> showAddTransactionDialog());
    }
    
    @Override
    protected Class<WantsViewModel> getViewModelClass() {
        return WantsViewModel.class;
    }
    
    @Override
    protected void setupObservers() {
        // TODO: Setup observers for wants data
    }
    
    private void showAddTransactionDialog() {
        // TODO: Implement add transaction dialog
    }
}
