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
import com.elececo.umoney.data.model.UserPreferences;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import com.elececo.umoney.ui.common.TransactionEntryDialog;
import java.util.List;

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
            
            final double finalInAmount = calculateInAmount(transactions);
            final double finalOutAmount = calculateOutAmount(transactions);
            final double finalTotalIncome = finalInAmount - finalOutAmount;
            
            TextView inAmountView = requireView().findViewById(R.id.in_amount);
            TextView outAmountView = requireView().findViewById(R.id.out_amount);
            TextView holdAmountView = requireView().findViewById(R.id.hold_amount);
            
            inAmountView.setText(String.format("In: ₹%.2f", finalInAmount));
            outAmountView.setText(String.format("Out: ₹%.2f", finalOutAmount));
            holdAmountView.setText(String.format("Hold: ₹%.2f", finalTotalIncome));

            viewModel.getUserPreferences().observe(getViewLifecycleOwner(), preferences -> {
                double needsPercentage = preferences.getNeedsPercentage() / 100.0;
                double wantsPercentage = preferences.getWantsPercentage() / 100.0;
                double savingsPercentage = preferences.getSavingsPercentage() / 100.0;

                double needsAmount = finalTotalIncome * needsPercentage;
                double wantsAmount = finalTotalIncome * wantsPercentage;
                double savingsAmount = finalTotalIncome * savingsPercentage;

                TextView needsPercentageView = requireView().findViewById(R.id.needs_percentage);
                TextView wantsPercentageView = requireView().findViewById(R.id.wants_percentage);
                TextView savingsPercentageView = requireView().findViewById(R.id.savings_percentage);

                needsPercentageView.setText(String.format("Needs (%d%%): ₹%.2f", 
                    preferences.getNeedsPercentage(), needsAmount));
                wantsPercentageView.setText(String.format("Wants (%d%%): ₹%.2f", 
                    preferences.getWantsPercentage(), wantsAmount));
                savingsPercentageView.setText(String.format("Savings (%d%%): ₹%.2f", 
                    preferences.getSavingsPercentage(), savingsAmount));
            });
        });
    }
    
    private double calculateInAmount(List<Transaction> transactions) {
        return transactions.stream()
            .filter(transaction -> transaction.getAmount() > 0)
            .mapToDouble(Transaction::getAmount)
            .sum();
    }
    
    private double calculateOutAmount(List<Transaction> transactions) {
        return transactions.stream()
            .filter(transaction -> transaction.getAmount() < 0)
            .mapToDouble(transaction -> Math.abs(transaction.getAmount()))
            .sum();
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        FloatingActionButton fab = view.findViewById(R.id.fab_add_income);
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
