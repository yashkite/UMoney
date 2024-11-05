package com.elececo.umoney.ui.common;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.elececo.umoney.R;
import com.elececo.umoney.data.model.Transaction;
import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TransactionEntryDialog extends Dialog {
    private static final int PICK_FILE_REQUEST = 1;
    private final TransactionEntryListener listener;
    private final String type;
    private TextInputEditText amountInput;
    private TextInputEditText dateTimeInput;
    private AutoCompleteTextView recipientInput;
    private AutoCompleteTextView categoryInput;
    private TextInputEditText notesInput;
    private TextView attachmentName;
    private Uri attachmentUri;
    private Calendar calendar;
    private SimpleDateFormat dateTimeFormatter;

    private static final Map<String, String[]> CATEGORIES = new HashMap<String, String[]>() {{
        put("NEEDS", new String[]{"Rent", "Groceries", "Utilities", "Transportation"});
        put("WANTS", new String[]{"Entertainment", "Shopping", "Dining", "Travel"});
        put("SAVINGS", new String[]{"Emergency Fund", "Investment", "Goals", "Retirement"});
    }};

    public TransactionEntryDialog(@NonNull Context context, String type, TransactionEntryListener listener) {
        super(context);
        this.type = type;
        this.listener = listener;
        this.calendar = Calendar.getInstance();
        this.dateTimeFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_transaction_entry);
        setTitle("Add " + type + " Transaction");

        setupViews();
        setupListeners();
        updateDateTime();
    }

    private void setupViews() {
        amountInput = findViewById(R.id.amount_input);
        dateTimeInput = findViewById(R.id.datetime_input);
        recipientInput = findViewById(R.id.recipient_input);
        categoryInput = findViewById(R.id.category_input);
        notesInput = findViewById(R.id.notes_input);
        attachmentName = findViewById(R.id.attachment_name);

        // Set up category adapter
        String[] categories = CATEGORIES.get(type);
        if (categories != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_dropdown_item_1line,
                categories
            );
            categoryInput.setAdapter(adapter);
        }
    }

    private void setupListeners() {
        Button saveButton = findViewById(R.id.save_button);
        Button cancelButton = findViewById(R.id.cancel_button);
        Button attachmentButton = findViewById(R.id.attachment_button);

        dateTimeInput.setOnClickListener(v -> showDateTimePicker());
        attachmentButton.setOnClickListener(v -> showFilePicker());
        saveButton.setOnClickListener(v -> saveTransaction());
        cancelButton.setOnClickListener(v -> dismiss());
    }

    private void showDateTimePicker() {
        new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            
            new TimePickerDialog(getContext(), (timeView, hourOfDay, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                updateDateTime();
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
            
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 
           calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showFilePicker() {
        if (listener != null) {
            listener.launchFilePicker();
        }
    }

    private void updateDateTime() {
        dateTimeInput.setText(dateTimeFormatter.format(calendar.getTime()));
    }

    public void handleFilePickerResult(Uri uri) {
        if (uri != null) {
            attachmentUri = uri;
            String fileName = uri.getLastPathSegment();
            attachmentName.setText(fileName);
            attachmentName.setVisibility(View.VISIBLE);
        }
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

        if (recipient.isEmpty()) {
            recipientInput.setError("Recipient is required");
            isValid = false;
        }

        if (category.isEmpty()) {
            categoryInput.setError("Category is required");
            isValid = false;
        }

        return isValid;
    }

    public interface TransactionEntryListener {
        void onTransactionSaved(Transaction transaction);
        void launchFilePicker();
    }
} 