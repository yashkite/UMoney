package com.elececo.umoney.ui.common;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import androidx.annotation.NonNull;
import com.elececo.umoney.R;
import com.elececo.umoney.data.model.Transaction;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TransactionEntryDialog extends BottomSheetDialog {
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
        this.calendar = Calendar.getInstance();
        this.dateTimeFormatter = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_transaction_entry);
        
        // Make dialog full height
        Window window = getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, 
                           WindowManager.LayoutParams.WRAP_CONTENT);
        }

        setupViews();
        setupListeners();
        updateDateTime();
    }

    private void setupViews() {
        // Initialize views with Material Design components
        amountLayout = findViewById(R.id.amount_layout);
        amountInput = findViewById(R.id.amount_input);
        dateTimeLayout = findViewById(R.id.datetime_layout);
        dateTimeInput = findViewById(R.id.datetime_input);
        recipientLayout = findViewById(R.id.recipient_layout);
        recipientInput = findViewById(R.id.recipient_input);
        categoryLayout = findViewById(R.id.category_layout);
        categoryInput = findViewById(R.id.category_input);
        notesLayout = findViewById(R.id.notes_layout);
        notesInput = findViewById(R.id.notes_input);
        attachmentButton = findViewById(R.id.attachment_button);

        // Set up category adapter with Material styling
        String[] categories = CATEGORIES.get(type);
        if (categories != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_dropdown_item_1line,
                categories
            );
            categoryInput.setAdapter(adapter);
        }

        // Set hints based on transaction type
        amountLayout.setHint(type.equals("INCOME") ? "Income Amount" : "Expense Amount");
        recipientLayout.setHint(type.equals("INCOME") ? "Received From" : "Paid To");
    }

    private void setupListeners() {
        MaterialButton saveButton = findViewById(R.id.save_button);
        MaterialButton cancelButton = findViewById(R.id.cancel_button);

        dateTimeInput.setOnClickListener(v -> showMaterialDateTimePicker());
        attachmentButton.setOnClickListener(v -> showFilePicker());
        saveButton.setOnClickListener(v -> saveTransaction());
        cancelButton.setOnClickListener(v -> dismiss());
    }

    private void showMaterialDateTimePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .setSelection(calendar.getTimeInMillis())
            .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            calendar.setTimeInMillis(selection);
            showMaterialTimePicker();
        });

        datePicker.show(((androidx.fragment.app.FragmentActivity) getContext()).getSupportFragmentManager(), "DATE_PICKER");
    }

    private void showMaterialTimePicker() {
        MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
            .setTitleText("Select time")
            .setHour(calendar.get(Calendar.HOUR_OF_DAY))
            .setMinute(calendar.get(Calendar.MINUTE))
            .build();

        timePicker.addOnPositiveButtonClickListener(v -> {
            calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
            calendar.set(Calendar.MINUTE, timePicker.getMinute());
            updateDateTime();
        });

        timePicker.show(((androidx.fragment.app.FragmentActivity) getContext()).getSupportFragmentManager(), "TIME_PICKER");
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
            attachmentButton.setText(fileName);
            attachmentButton.setVisibility(View.VISIBLE);
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