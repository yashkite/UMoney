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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class TransactionPage extends AppCompatActivity {

    // variables for spinners
    EditText with, amount, description;
    Button done, cancel;
    FirebaseFirestore dbroot;
    private Spinner Category, Tags;
    private ArrayList category;
    private String selectedCategory, selectedTag;
    private ArrayAdapter<CharSequence> CategoryAdapter, TagAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_page);

//      Grab the value send by intent from fragment to use while making path
        Intent i = getIntent();
        String GivenOrTaken = i.getStringExtra("WhichButton");


        dbroot = FirebaseFirestore.getInstance();
        with = findViewById(R.id.with);
        amount = findViewById(R.id.amountGiven);
        description = findViewById(R.id.descriptionGiven);


        done = findViewById(R.id.doneGiven);
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
                i.put("Tag", selectedTag.trim());
                i.put("GivenOrTaken", GivenOrTaken.trim());


                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String userEmail = user.getEmail();
//              path = "/Users/" + userEmail + selectedCategory + "/Tags/" + selectedTag + "/" + GivenOrTaken;

//              checking the value is not empty before making entry for required values only
                String checkwith = with.getText().toString();
                String checkamount = amount.getText().toString();
                if (checkwith.matches("") || checkamount.matches("")) {
                    Toast.makeText(getApplicationContext(), "With and Amount cannot be empty", Toast.LENGTH_SHORT).show();
                }

//              if all things is ok then write operation will be executed and activity will be closed
                else {
                    dbroot.collection("Users").document(userEmail).collection(selectedCategory).add(i);
                    finish();
                }


            }
        });

//      cancel button will finish the activity and comeback to the previous activity
        cancel = findViewById(R.id.cancelGiven);
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

        Category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long Id) {
                Tags = findViewById(R.id.Tags);
                //      getting from which category its comes from and saving it inside selectedCategory veriable
                selectedCategory = Category.getSelectedItem().toString();
                int parentId = parent.getId();


                // Created dependant spinner logic using switch case
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

