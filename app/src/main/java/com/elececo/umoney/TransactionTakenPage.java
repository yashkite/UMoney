package com.elececo.umoney;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class TransactionTakenPage extends AppCompatActivity {
    String from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_taken_page);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        from = intent.getStringExtra("From");
        Toast.makeText(getApplicationContext(),
                from,
                Toast.LENGTH_LONG).show();

    }
}