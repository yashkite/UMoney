package com.elececo.umoney.ui.common;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.AutoCompleteTextView;
import androidx.annotation.NonNull;
import com.elececo.umoney.R;
import com.elececo.umoney.data.model.Transaction;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Date;

public class TransactionEntryDialog extends Dialog {
    private final TransactionEntryListener listener;
    private final String type; // "INCOME", "NEEDS", "WANTS", "SAVINGS"

    public TransactionEntryDialog(@NonNull Context context, String type, TransactionEntryListener listener) {
        super(context);
        this.type = type;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_transaction_entry);

        setupViews();
        setupListeners();
    }

    private void setupViews() {
        // Initialize views and set up category adapter based on type
        // TODO: Implement view setup
    }

    private void setupListeners() {
        findViewById(R.id.save_button).setOnClickListener(v -> saveTransaction());
        findViewById(R.id.cancel_button).setOnClickListener(v -> dismiss());
        // TODO: Implement other listeners
    }

    private void saveTransaction() {
        // Validate and create transaction
        // TODO: Implement transaction saving
    }

    public interface TransactionEntryListener {
        void onTransactionSaved(Transaction transaction);
    }
} 