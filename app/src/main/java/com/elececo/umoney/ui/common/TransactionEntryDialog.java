package com.elececo.umoney.ui.common;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;

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
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.LinkedHashSet;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import com.elececo.umoney.ui.categories.CategoriesViewModel;

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
    private Transaction existingTransaction;
    private Transaction pendingTransaction;

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
        
        // Apply pending transaction if it exists
        if (pendingTransaction != null) {
            applyTransactionToViews(pendingTransaction);
            pendingTransaction = null;
        }
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
            case "WANTS":
            case "SAVINGS":
                titleView.setText("Add " + type.charAt(0) + type.substring(1).toLowerCase() + " Expense");
                amountLayout.setHint("Expense Amount");
                recipientLayout.setHint("Paid To");
                break;
        }

        // Load categories for the dropdown
        loadCategories();

        // Initialize calendar and formatter
        calendar = Calendar.getInstance();
        dateTimeFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        // Setup date time picker
        setupDateTimePicker();

        // Set initial date time
        TextInputEditText dateTimeInput = findViewById(R.id.date_time_input);
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
            double transactionAmount = Double.parseDouble(amount);
            if (!type.equals("INCOME")) {
                transactionAmount = -transactionAmount;
            }

            Transaction transaction;
            if (existingTransaction != null) {
                // Update existing transaction
                transaction = existingTransaction;
                transaction.setAmount(transactionAmount);
                // Only update timestamp if user has changed it
                if (!dateTimeFormatter.format(calendar.getTime())
                        .equals(dateTimeFormatter.format(existingTransaction.getTimestamp()))) {
                    transaction.setTimestamp(calendar.getTime());
                }
                transaction.setRecipient(recipient);
                transaction.setCategory(category);
                transaction.setNotes(notes);
                if (attachmentUri != null) {
                    transaction.setAttachmentUri(attachmentUri.toString());
                }
            } else {
                // Create new transaction
                transaction = new Transaction(
                    transactionAmount,
                    calendar.getTime(),
                    recipient,
                    category,
                    type
                );
                transaction.setNotes(notes);
                if (attachmentUri != null) {
                    transaction.setAttachmentUri(attachmentUri.toString());
                }
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

    private void setupDateTimePicker() {
        dateTimeInput.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    
                    TimePickerDialog timePickerDialog = new TimePickerDialog(
                        getContext(),
                        (timeView, hourOfDay, minute) -> {
                            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            calendar.set(Calendar.MINUTE, minute);
                            dateTimeInput.setText(dateTimeFormatter.format(calendar.getTime()));
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        false
                    );
                    timePickerDialog.show();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });
    }

    private void loadCategories() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(userId)
            .collection("categories")
            .document(type)
            .get()
            .addOnSuccessListener(document -> {
                Set<String> uniqueCategories = new LinkedHashSet<>();
                
                if (document.exists() && document.get("items") != null) {
                    // Load saved categories (including both custom and default)
                    uniqueCategories.addAll((List<String>) document.get("items"));
                } else {
                    // If no document exists, load default categories from CategoriesViewModel
                    uniqueCategories.addAll(Arrays.asList(CategoriesViewModel.CATEGORIES.get(type)));
                }
                
                List<String> allCategories = new ArrayList<>(uniqueCategories);
                
                ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                    getContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    allCategories
                );
                categoryInput.setAdapter(categoryAdapter);
                categoryInput.setThreshold(1);
            })
            .addOnFailureListener(e -> {
                // Fallback to default categories if loading fails
                ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                    getContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    CategoriesViewModel.CATEGORIES.get(type)
                );
                categoryInput.setAdapter(categoryAdapter);
            });
    }

    public void setTransaction(Transaction transaction) {
        if (!isShowing()) {
            // Store transaction to be applied after dialog creation
            this.pendingTransaction = transaction;
            return;
        }
        applyTransactionToViews(transaction);
    }

    private void applyTransactionToViews(Transaction transaction) {
        this.existingTransaction = transaction;
        
        if (amountInput != null) {
            amountInput.setText(String.format(Locale.getDefault(), "%.2f", 
                Math.abs(transaction.getAmount())));
        }
        if (dateTimeInput != null && dateTimeFormatter != null) {
            // Set the calendar to the transaction's timestamp
            calendar.setTime(transaction.getTimestamp());
            dateTimeInput.setText(dateTimeFormatter.format(transaction.getTimestamp()));
        }
        if (categoryInput != null) {
            categoryInput.setText(transaction.getCategory());
        }
        if (recipientInput != null) {
            recipientInput.setText(transaction.getRecipient());
        }
        if (notesInput != null) {
            notesInput.setText(transaction.getNotes());
        }
        
        if (transaction.getAttachmentUri() != null && attachmentButton != null) {
            attachmentUri = Uri.parse(transaction.getAttachmentUri());
            attachmentButton.setText(attachmentUri.getLastPathSegment());
        }
        
        titleView.setText("Edit " + type);
    }

    public interface TransactionEntryListener {
        void onTransactionSaved(Transaction transaction);
        void launchFilePicker();
    }
} 