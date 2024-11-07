package com.elececo.umoney.ui.base;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.elececo.umoney.R;
import com.elececo.umoney.data.model.Transaction;
import com.elececo.umoney.ui.common.TransactionAdapter;
import com.elececo.umoney.ui.common.TransactionEntryDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public abstract class BaseFragment<VM extends BaseViewModel> extends Fragment 
    implements TransactionEntryDialog.TransactionEntryListener {
    
    protected VM viewModel;
    private static final int PICK_FILE_REQUEST = 1;
    protected TransactionEntryDialog dialog;
    protected TransactionAdapter adapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(getViewModelClass());
        
        if (hasTransactionList()) {
            setupRecyclerView();
            setupFab();
            setupCardTitle();
            setupObservers();
        }
    }

    protected boolean hasTransactionList() {
        return getView().findViewById(R.id.transactions_list) != null;
    }

    private void setupRecyclerView() {
        RecyclerView transactionsList = requireView().findViewById(R.id.transactions_list);
        adapter = new TransactionAdapter();
        transactionsList.setLayoutManager(new LinearLayoutManager(requireContext()));
        transactionsList.setAdapter(adapter);
    }

    private void setupFab() {
        FloatingActionButton fab = requireView().findViewById(R.id.fab_action);
        if (fab != null) {
            fab.setImageResource(isExpenseType() ? R.drawable.ic_remove : R.drawable.ic_add);
            fab.setOnClickListener(v -> showTransactionDialog());
        }
    }

    protected void setupCardTitle() {
        TextView cardTitle = getView().findViewById(R.id.card_title);
        if (cardTitle != null) {
            cardTitle.setText(getCardTitle());
        }
    }

    protected void setupObservers() {
        viewModel.getTransactions().observe(getViewLifecycleOwner(), transactions -> {
            adapter.setTransactions(transactions);
            updateSummaryCard(transactions);
        });
    }

    protected void updateSummaryCard(List<Transaction> transactions) {
        double inAmount = calculateInAmount(transactions);
        double outAmount = calculateOutAmount(transactions);
        
        TextView inAmountView = requireView().findViewById(R.id.in_amount);
        TextView outAmountView = requireView().findViewById(R.id.out_amount);
        TextView holdAmountView = requireView().findViewById(R.id.hold_amount);
        
        inAmountView.setText(String.format("In: ₹%.2f", inAmount));
        outAmountView.setText(String.format("Out: ₹%.2f", outAmount));
        holdAmountView.setText(String.format("Hold: ₹%.2f", inAmount - outAmount));
    }

    protected double calculateInAmount(List<Transaction> transactions) {
        return transactions.stream()
            .filter(transaction -> transaction.getAmount() > 0)
            .mapToDouble(Transaction::getAmount)
            .sum();
    }

    protected double calculateOutAmount(List<Transaction> transactions) {
        return transactions.stream()
            .filter(transaction -> transaction.getAmount() < 0)
            .mapToDouble(transaction -> Math.abs(transaction.getAmount()))
            .sum();
    }

    protected void showTransactionDialog() {
        dialog = new TransactionEntryDialog(
            requireContext(),
            getTransactionType(),
            this
        );
        dialog.show();
    }

    @Override
    public void onTransactionSaved(Transaction transaction) {
        if (isExpenseType()) {
            transaction.setAmount(-Math.abs(transaction.getAmount()));
        }
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

    protected abstract Class<VM> getViewModelClass();
    protected abstract String getTransactionType();
    protected abstract String getCardTitle();
    protected abstract boolean isExpenseType();
} 