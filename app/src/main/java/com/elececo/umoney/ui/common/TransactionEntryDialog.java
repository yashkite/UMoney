package com.elececo.umoney.ui.common;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import com.elececo.umoney.R;
import com.elececo.umoney.data.model.Transaction;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TransactionEntryDialog extends Dialog {
    private static final int PICK_FILE_REQUEST = 1;
    private final TransactionEntryListener listener;
    private final String type;
    private TextInputLayout amountLayout;
    private TextInputEditText amountInput;
    private TextInputLayout dateTimeLayout;
    private TextInputEditText dateTimeInput;
    private TextInputLayout recipientLayout;
    private AutoCompleteTextView recipientInput;
    private TextInputLayout categoryLayout;
    private AutoCompleteTextView categoryInput;
    private TextInputLayout notesLayout;
    private TextInputEditText notesInput;
    private MaterialButton attachmentButton;
    private TextView titleView;
    private Uri attachmentUri;
    private Calendar calendar;
    private SimpleDateFormat dateTimeFormatter;

    private static final Map<String, String[]> CATEGORIES = new HashMap<String, String[]>() {{
        put("NEEDS", new String[]{"Food", "Transportation", "Housing", "Utilities", "Healthcare", "Education"});
        put("WANTS", new String[]{"Entertainment", "Shopping", "Dining", "Travel", "Hobbies", "Gadgets"});
        put("SAVINGS", new String[]{"Emergency Fund", "Retirement", "Investment", "Goals", "Insurance"});
        put("INCOME", new String[]{"Salary", "Freelance", "Business", "Investment", "Rental", "Other"});
    }};

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
    }

    private void setupViews() {
        titleView = findViewById(R.id.dialog_title);
        amountLayout = findViewById(R.id.amount_layout);
        amountInput = findViewById(R.id.amount_input);
        dateTimeLayout = findViewById(R.id.date_time_layout);
        dateTimeInput = findViewById(R.id.date_time_input);
        recipientLayout = findViewById(R.id.recipient_layout);
        recipientInput = findViewById(R.id.recipient_input);
        categoryLayout = findViewById(R.id.category_layout);
        categoryInput = findViewById(R.id.category_input);
        notesLayout = findViewById(R.id.notes_layout);
        notesInput = findViewById(R.id.notes_input);
        attachmentButton = findViewById(R.id.attachment_button);

        // Set dialog title and hints based on transaction type
        switch (type) {
            case "INCOME":
                titleView.setText("Add Income");
                amountLayout.setHint("Income Amount");
                recipientLayout.setHint("Received From");
                break;
            case "NEEDS":
                titleView.setText("Add Needs Expense");
                amountLayout.setHint("Expense Amount");
                recipientLayout.setHint("Paid To");
                break;
            case "WANTS":
                titleView.setText("Add Wants Expense");
                amountLayout.setHint("Expense Amount");
                recipientLayout.setHint("Paid To");
                break;
            case "SAVINGS":
                titleView.setText("Add Savings Transaction");
                amountLayout.setHint("Amount");
                recipientLayout.setHint("Account/Investment");
                break;
        }

        // Set up category adapter based on type
        if (CATEGORIES.containsKey(type)) {
            ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_dropdown_item_1line,
                CATEGORIES.get(type)
            );
            categoryInput.setAdapter(categoryAdapter);
        }

        // Initialize date/time formatter
        calendar = Calendar.getInstance();
        dateTimeFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        dateTimeInput.setText(dateTimeFormatter.format(calendar.getTime()));

        // Set up click listeners
        attachmentButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.launchFilePicker();
            }
        });

        findViewById(R.id.save_button).setOnClickListener(v -> saveTransaction());
        findViewById(R.id.cancel_button).setOnClickListener(v -> dismiss());
    }

    private void saveTransaction() {
        String amount = amountInput.getText().toString();
        String recipient = recipientInput.getText().toString();
        String category = categoryInput.getText().toString();
        String notes = notesInput.getText().toString();

        if (validateInputs(amount, recipient, category)) {
            Transaction transaction = new Transaction(
                Double.parseDouble(amount),
                calendar.getTime(),
                recipient,
                category,
                type
            );
            transaction.setNotes(notes);
            if (attachmentUri != null) {
                transaction.setAttachmentUri(attachmentUri.toString());
            }
            listener.onTransactionSaved(transaction);
            dismiss();
        }
    }

    private boolean validateInputs(String amount, String recipient, String category) {
        boolean isValid = true;

        if (amount.isEmpty()) {
            amountInput.setError("Amount is required");
            isValid = false;
        }

        if (recipientLayout.getVisibility() == View.VISIBLE && recipient.isEmpty()) {
            recipientInput.setError("Recipient is required");
            isValid = false;
        }

        if (category.isEmpty()) {
            categoryInput.setError("Category is required");
            isValid = false;
        }

        return isValid;
    }

    public void handleFilePickerResult(Uri uri) {
        if (uri != null) {
            attachmentUri = uri;
            String fileName = uri.getLastPathSegment();
            attachmentButton.setText(fileName);
            attachmentButton.setVisibility(View.VISIBLE);
        }
    }

    public interface TransactionEntryListener {
        void onTransactionSaved(Transaction transaction);
        void launchFilePicker();
    }
} 