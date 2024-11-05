package com.elececo.umoney.ui.income;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.elececo.umoney.R;
import com.elececo.umoney.ui.base.BaseFragment;
import com.elececo.umoney.ui.common.TransactionEntryDialog;
import com.elececo.umoney.ui.income.viewmodel.IncomeViewModel;
import com.elececo.umoney.ui.common.TransactionAdapter;
import com.elececo.umoney.data.model.Transaction;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.app.Activity;
import android.content.Intent;
import androidx.annotation.Nullable;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import com.elececo.umoney.ui.common.TransactionEntryDialog;

public class IncomeFragment extends BaseFragment<IncomeViewModel> implements TransactionEntryDialog.TransactionEntryListener {
    private static final int PICK_FILE_REQUEST = 1;
    private TransactionEntryDialog dialog;

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
        RecyclerView transactionsList = requireView().findViewById(R.id.transactions_list);
        TransactionAdapter adapter = new TransactionAdapter();
        transactionsList.setLayoutManager(new LinearLayoutManager(requireContext()));
        transactionsList.setAdapter(adapter);

        viewModel.getTransactions().observe(getViewLifecycleOwner(), transactions -> {
            adapter.setTransactions(transactions);
            
            // Update summary card
            double inAmount = 0;
            double outAmount = 0;
            for (Transaction transaction : transactions) {
                if (transaction.getAmount() > 0) {
                    inAmount += transaction.getAmount();
                } else {
                    outAmount += Math.abs(transaction.getAmount());
                }
            }
            
            TextView inAmountView = requireView().findViewById(R.id.in_amount);
            TextView outAmountView = requireView().findViewById(R.id.out_amount);
            TextView holdAmountView = requireView().findViewById(R.id.hold_amount);
            
            inAmountView.setText(String.format("In: ₹%.2f", inAmount));
            outAmountView.setText(String.format("Out: ₹%.2f", outAmount));
            holdAmountView.setText(String.format("Hold: ₹%.2f", inAmount - outAmount));
        });
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        FloatingActionButton fab = view.findViewById(R.id.fab_action);
        fab.setImageResource(R.drawable.ic_add);
        fab.setOnClickListener(v -> showAddTransactionDialog());
    }
    
    private void showAddTransactionDialog() {
        dialog = new TransactionEntryDialog(
            requireContext(),
            "INCOME",
            this
        );
        dialog.show();
    }
    
    @Override
    public void onTransactionSaved(Transaction transaction) {
        viewModel.saveTransaction(transaction);
    }
    
    @Override
    public void launchFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        String[] mimeTypes = {"image/*", "application/pdf"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(intent, PICK_FILE_REQUEST);
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            if (dialog != null) {
                dialog.handleFilePickerResult(data.getData());
            }
        }
    }
}
