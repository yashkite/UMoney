package com.elececo.umoney;

import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;


public class TransactionGivenPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_given_page);
        Spinner Category = findViewById(R.id.Categories);
        ArrayAdapter<CharSequence> CategoryAdapter=ArrayAdapter.createFromResource(this, R.array.TransactionCategoryList, android.R.layout.simple_spinner_item);
        CategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        Category.setAdapter(CategoryAdapter);

        Spinner Tags = findViewById(R.id.Tags);
        ArrayAdapter<CharSequence> CategoryTags=ArrayAdapter.createFromResource(this, R.array.NeedsTagList, android.R.layout.simple_spinner_item);
        CategoryTags.setDropDownViewResource(android.R.layout.simple_spinner_item);
        Tags.setAdapter(CategoryTags);
    }

}

