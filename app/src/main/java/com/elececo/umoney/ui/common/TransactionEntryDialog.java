package com.elececo.umoney.ui.common;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.elececo.umoney.R;
import com.elececo.umoney.data.model.Transaction;
import com.elececo.umoney.ui.categories.CategoriesViewModel;
import com.elececo.umoney.utils.DriveServiceHelper;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

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
    private String driveFileId;
    private String driveFileName;
    private DriveServiceHelper driveHelper;
    private Uri pendingAttachmentUri;
    private String pendingAttachmentName;

    public TransactionEntryDialog(@NonNull Context context, String type, TransactionEntryListener listener) {
        super(context);
        this.type = type;
        this.listener = listener;
        
        // Initialize DriveServiceHelper
        initializeDriveHelper(context);
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
            if (driveFileId != null) {
                showAttachmentOptions();
            } else {
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

        if (!validateInputs(amount, recipient, category)) {
            return;
        }

        // Show loading indicator
        findViewById(R.id.save_button).setEnabled(false);
        
        // Handle file upload first if there's a pending attachment
        if (pendingAttachmentUri != null) {
            uploadAttachmentAndSave(amount, recipient, category, notes);
        } else {
            saveTransactionToFirestore(amount, recipient, category, notes);
        }
    }

    private void uploadAttachmentAndSave(String amount, String recipient, String category, String notes) {
        if (driveHelper == null) {
            Toast.makeText(getContext(), "Drive service not initialized", Toast.LENGTH_SHORT).show();
            findViewById(R.id.save_button).setEnabled(true);
            return;
        }

        driveHelper.uploadFile(pendingAttachmentUri, pendingAttachmentName, type)
            .addOnSuccessListener(result -> {
                driveFileId = result[0];
                driveFileName = result[1];
                saveTransactionToFirestore(amount, recipient, category, notes);
            })
            .addOnFailureListener(e -> {
                Toast.makeText(getContext(), 
                    "Failed to upload file: " + e.getMessage(), 
                    Toast.LENGTH_SHORT).show();
                findViewById(R.id.save_button).setEnabled(true);
            });
    }

    private void saveTransactionToFirestore(String amount, String recipient, String category, String notes) {
        double transactionAmount = Double.parseDouble(amount);
        Transaction transaction;

        if (existingTransaction != null) {
            // If there's a new attachment and an existing one, delete the old one first
            if ((pendingAttachmentUri != null || driveFileId == null) && 
                existingTransaction.getDriveFileId() != null) {
                driveHelper.deleteFile(existingTransaction.getDriveFileId())
                    .addOnSuccessListener(aVoid -> {
                        Log.d("TransactionEntryDialog", "Old attachment deleted successfully");
                    })
                    .addOnFailureListener(e -> {
                        Log.e("TransactionEntryDialog", "Failed to delete old attachment: " + e.getMessage());
                    });
            }
            
            // Update existing transaction
            existingTransaction.setAmount(transactionAmount);
            existingTransaction.setTimestamp(calendar.getTime());
            existingTransaction.setRecipient(recipient);
            existingTransaction.setCategory(category);
            existingTransaction.setNotes(notes);
            if (driveFileId != null) {
                existingTransaction.setDriveFileId(driveFileId);
                existingTransaction.setDriveFileName(driveFileName);
            } else {
                existingTransaction.setDriveFileId(null);
                existingTransaction.setDriveFileName(null);
            }
            transaction = existingTransaction;
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
            if (driveFileId != null) {
                transaction.setDriveFileId(driveFileId);
                transaction.setDriveFileName(driveFileName);
            }
        }

        listener.onTransactionSaved(transaction);
        dismiss();
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

    public void handleFilePickerResult(Uri fileUri) {
        if (fileUri == null) {
            Toast.makeText(getContext(), "Invalid file selected", Toast.LENGTH_SHORT).show();
            return;
        }

        pendingAttachmentUri = fileUri;
        pendingAttachmentName = getFileName(fileUri, getContext());
        attachmentButton.setText(pendingAttachmentName);
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
        
        if (transaction.getDriveFileId() != null && attachmentButton != null) {
            driveFileId = transaction.getDriveFileId();
            driveFileName = transaction.getDriveFileName();
            attachmentButton.setText(driveFileName);
        }
        
        titleView.setText("Edit " + type);
    }

    public interface TransactionEntryListener {
        void onTransactionSaved(Transaction transaction);
        void launchFilePicker();
    }

    private String getFileName(Uri uri, Context context) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void showAttachmentOptions() {
        if (pendingAttachmentUri != null) {
            new MaterialAlertDialogBuilder(getContext())
                .setTitle("Remove Attachment?")
                .setMessage("The selected file hasn't been uploaded yet. Do you want to remove it?")
                .setPositiveButton("Remove", (dialog, which) -> {
                    pendingAttachmentUri = null;
                    pendingAttachmentName = null;
                    attachmentButton.setText("Add Attachment");
                })
                .setNegativeButton("Cancel", null)
                .show();
        } else if (driveFileId != null) {
            new MaterialAlertDialogBuilder(getContext())
                .setTitle("Attachment Options")
                .setItems(new String[]{"View", "Remove"}, (dialog, which) -> {
                    if (which == 0) {
                        driveHelper.openFile(getContext(), driveFileId);
                    } else {
                        driveHelper.deleteFile(driveFileId)
                            .addOnSuccessListener(aVoid -> {
                                driveFileId = null;
                                driveFileName = null;
                                attachmentButton.setText("Add Attachment");
                            });
                    }
                })
                .show();
        }
    }

    private void initializeDriveHelper(Context context) {
        try {
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
            if (account != null) {
                this.driveHelper = new DriveServiceHelper(context, account);
            } else {
                throw new Exception("No signed in account found");
            }
        } catch (Exception e) {
            Log.e("TransactionEntryDialog", "Failed to initialize Drive: " + e.getMessage());
            Toast.makeText(context, 
                "File attachments may not be available. Please sign out and sign in again.", 
                Toast.LENGTH_LONG).show();
        }
    }
} 