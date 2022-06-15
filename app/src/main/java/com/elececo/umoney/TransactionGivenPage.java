package com.elececo.umoney;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;


public class TransactionGivenPage extends AppCompatActivity {
    private Spinner Category, Tags;
    private ArrayList category;
    private String selectedCategory, selectedTag;
    private ArrayAdapter<CharSequence> CategoryAdapter, TagAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_given_page);
        Category = findViewById(R.id.Categories);

//
        CategoryAdapter = ArrayAdapter.createFromResource(this, R.array.TransactionCategoryList, android.R.layout.simple_spinner_item);
        CategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Category.setAdapter(CategoryAdapter);
        Category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long Id) {
                Tags = findViewById(R.id.Tags);
                selectedCategory = Category.getSelectedItem().toString();

                int parentId = parent.getId();
                if (parentId == R.id.Categories) {
                    switch (selectedCategory) {
                        case "Needs":
                            TagAdapter = ArrayAdapter.createFromResource(parent.getContext(), R.array.Needs, R.layout.support_simple_spinner_dropdown_item);
                            break;
                        case "Wants":
                            TagAdapter = ArrayAdapter.createFromResource(parent.getContext(), R.array.Wants, R.layout.support_simple_spinner_dropdown_item);
                            break;

                        case "Savings":
                            TagAdapter = ArrayAdapter.createFromResource(parent.getContext(), R.array.Savings, R.layout.support_simple_spinner_dropdown_item);
                            break;
                        default:
                            break;
                    }
                    TagAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    Tags.setAdapter(TagAdapter);
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }


}

