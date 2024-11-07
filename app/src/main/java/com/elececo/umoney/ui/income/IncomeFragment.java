package com.elececo.umoney.ui.income;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.google.android.material.textfield.TextInputLayout;
import com.elececo.umoney.R;
import com.elececo.umoney.ui.base.BaseFragment;
import com.elececo.umoney.ui.common.TransactionEntryDialog;
import com.elececo.umoney.ui.income.viewmodel.IncomeViewModel;
import com.elececo.umoney.data.model.Transaction;
import java.util.List;

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
        // Setup transaction list observer
        viewModel.getTransactions().observe(getViewLifecycleOwner(), transactions -> {
            adapter.setTransactions(transactions);
            updateSummaryCard(transactions);
        });
    }

    @Override
    protected String getTransactionType() {
        return "INCOME";
    }

    @Override
    protected String getCardTitle() {
        return "Income Summary";
    }

    @Override
    protected boolean isExpenseType() {
        return false;
    }

    @Override
    protected void showTransactionDialog() {
        dialog = new TransactionEntryDialog(
            requireContext(),
            getTransactionType(),
            this
        );
        
        // Wait for dialog to be created and then customize it
        dialog.setOnShowListener(dialogInterface -> {
            // Hide recipient field since it's not needed for income
            TextInputLayout recipientLayout = dialog.findViewById(R.id.recipient_layout);
            if (recipientLayout != null) {
                recipientLayout.setVisibility(View.GONE);
            }

            // Set category hint to "Source"
            TextInputLayout categoryLayout = dialog.findViewById(R.id.category_layout);
            if (categoryLayout != null) {
                categoryLayout.setHint("Source");
                AutoCompleteTextView categoryInput = dialog.findViewById(R.id.category_input);
                if (categoryInput != null) {
                    String[] categories = {"Salary", "Freelance", "Business", "Investment", "Rental", "Other"};
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        categories
                    );
                    categoryInput.setAdapter(adapter);
                }
            }

            // Set amount hint
            TextInputLayout amountLayout = dialog.findViewById(R.id.amount_layout);
            if (amountLayout != null) {
                amountLayout.setHint("Income Amount");
            }

            // Customize dialog window
            Window window = dialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams params = window.getAttributes();
                params.width = WindowManager.LayoutParams.MATCH_PARENT;
                params.height = WindowManager.LayoutParams.WRAP_CONTENT;
                window.setAttributes(params);
            }
        });
        
        dialog.show();
    }

    @Override
    public void onTransactionSaved(Transaction transaction) {
        super.onTransactionSaved(transaction);
        
        // Get user preferences for distribution percentages
        viewModel.getUserPreferences().observe(getViewLifecycleOwner(), prefs -> {
            double amount = transaction.getAmount();
            
            // Create needs transaction
            Transaction needsTransaction = new Transaction(
                (amount * prefs.getNeedsPercentage()) / 100,
                transaction.getTimestamp(),
                "Auto Distribution",
                "Income Distribution",
                "NEEDS"
            );
            needsTransaction.setNotes("Distributed from income: " + transaction.getCategory());
            needsTransaction.setParentTransactionId(transaction.getId());
            
            // Create wants transaction
            Transaction wantsTransaction = new Transaction(
                (amount * prefs.getWantsPercentage()) / 100,
                transaction.getTimestamp(),
                "Auto Distribution",
                "Income Distribution",
                "WANTS"
            );
            wantsTransaction.setNotes("Distributed from income: " + transaction.getCategory());
            wantsTransaction.setParentTransactionId(transaction.getId());
            
            // Create savings transaction
            Transaction savingsTransaction = new Transaction(
                (amount * prefs.getSavingsPercentage()) / 100,
                transaction.getTimestamp(),
                "Auto Distribution",
                "Income Distribution",
                "SAVINGS"
            );
            savingsTransaction.setNotes("Distributed from income: " + transaction.getCategory());
            savingsTransaction.setParentTransactionId(transaction.getId());
            
            // Save all distributed transactions
            viewModel.distributeIncome(needsTransaction, wantsTransaction, savingsTransaction);
        });
    }
}
