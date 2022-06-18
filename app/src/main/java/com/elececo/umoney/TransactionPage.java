package com.elececo.umoney;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class TransactionPage extends AppCompatActivity {

    // variables for spinners
    EditText with, amount, description;
    Button done, cancel;
    private Spinner Category, Tags;
    private ArrayList category;
    private String selectedCategory, selectedTag, path;
    private ArrayAdapter<CharSequence> CategoryAdapter, TagAdapter;
    FirebaseFirestore dbroot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_page);
        Intent i = getIntent();
        String GivenOrTaken = i.getStringExtra("WhichButton");
        dbroot = FirebaseFirestore.getInstance();
        with = (EditText) findViewById(R.id.with);
        amount = (EditText) findViewById(R.id.amountGiven);
        description = (EditText) findViewById(R.id.descriptionGiven);


        done = (Button) findViewById(R.id.doneGiven);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertdata();
            }

            private void insertdata() {
                Map<String, String> i = new HashMap<>();
                i.put("With", with.getText().toString().trim());
                i.put("Amount", amount.getText().toString().trim());
                i.put("Description", description.getText().toString().trim());


                path = "/Users/AfJoInkbyLlN3a55EPYh/TransactionList/" + selectedCategory + "/Tags/" + selectedTag + "/" + GivenOrTaken;

                String checkwith = with.getText().toString();
                String checkamount = amount.getText().toString();
                String checkdescription = description.getText().toString();


                if(checkwith.matches("")||checkamount.matches("")){
                    Toast.makeText(getApplicationContext(),"With and Amount cannot be empty",Toast.LENGTH_SHORT).show();
                }else {
                    dbroot.collection(path).add(i);
                }



            }
        });


        cancel = (Button) findViewById(R.id.cancelGiven);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        Category = findViewById(R.id.Categories);
        CategoryAdapter = ArrayAdapter.createFromResource(this, R.array.TransactionCategoryList, android.R.layout.simple_spinner_item);
        CategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Category.setAdapter(CategoryAdapter);

        // Created dependant spinner logic using switch case
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

                    Tags.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            selectedTag = Tags.getSelectedItem().toString();

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }


}

