package com.elececo.umoney;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;

public class TransactionGivenPage extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_given_page);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
}