package com.elececo.umoney;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class TransactionTakenPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_taken_page);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}