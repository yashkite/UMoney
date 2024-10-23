package com.elececo.umoney;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.elececo.umoney.model.Transaction;
import com.elececo.umoney.viewmodel.TransactionViewModel;
import com.google.firebase.Timestamp;

public class AddTransactionActivity extends AppCompatActivity {

    private EditText amountEditText;
    private EditText descriptionEditText;
    private Button saveButton;
    private TransactionViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        amountEditText = findViewById(R.id.amountEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        saveButton = findViewById(R.id.saveButton);

        String category = getIntent().getStringExtra("category");
        String transactionType = getIntent().getStringExtra("transactionType");

        viewModel = new TransactionViewModel();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTransaction(category, transactionType);
            }
        });
    }

    private void saveTransaction(String category, String transactionType) {
        String amountStr = amountEditText.getText().toString();
        String description = descriptionEditText.getText().toString();

        if (amountStr.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        if (transactionType.equals("Taken")) {
            amount = -amount; // Make the amount negative for "Taken" transactions
        }

        Transaction transaction = new Transaction(amount, Timestamp.now(), description, category);
        viewModel.addTransaction(transaction);

        Toast.makeText(this, "Transaction saved", Toast.LENGTH_SHORT).show();
        finish();
    }
}
