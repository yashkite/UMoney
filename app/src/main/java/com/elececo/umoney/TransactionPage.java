package com.elececo.umoney;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class TransactionPage extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {


    int day, month, year, hour, minute;
    int myday, myMonth, myYear, myHour, myMinute;

    // variables for spinners
    TextView datetext, timetext;
    EditText with, amount, description;
    Button done, cancel, datentime;
    FirebaseFirestore dbroot;
    private Spinner Category, Tags;
    private ArrayList category;
    private String selectedCategory, selectedTag;
    private ArrayAdapter<CharSequence> CategoryAdapter, TagAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_page);
        datetext = findViewById(R.id.dateTextView);
        timetext = findViewById(R.id.timeTextView);

        Calendar calendar = Calendar.getInstance();
        myYear = calendar.get(Calendar.YEAR);
        myMonth = calendar.get(Calendar.MONTH);
        myday = calendar.get(Calendar.DAY_OF_MONTH);
        myHour = calendar.get(Calendar.HOUR);
        myMinute = calendar.get(Calendar.MINUTE);
datetext.setText(myday+"/"+myMonth+"/"+myYear);
timetext.setText(myHour+":"+myMinute);

        datentime = findViewById(R.id.dateAndTimePicker);
        datentime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(TransactionPage.this, TransactionPage.this, year, month, day);
                datePickerDialog.show();

            }
        });


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
                String datentime = myYear+"-"+myMonth+"-"+myday+"T"+myHour+":"+myMinute+":00Z";

                Map<String, Object> i = new HashMap<>();
                i.put("With", with.getText().toString().trim());
                i.put("Amount", amount.getText().toString().trim());
                i.put("Description", description.getText().toString().trim());
                i.put("Tag", selectedTag.trim());
                i.put("GivenOrTaken", GivenOrTaken.trim());
                i.put("Timestamp", getDateFromString(datentime));



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

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        Calendar c = Calendar.getInstance();

        myYear = year;
        myday = dayOfMonth;
        myMonth = month;
        hour = c.get(Calendar.HOUR);
        minute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(TransactionPage.this, TransactionPage.this, hour, minute, DateFormat.is24HourFormat(this));
        timePickerDialog.show();



    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        myHour = hourOfDay;
        myMinute = minute;
        datetext.setText(myday+"/"+myMonth+"/"+myYear);
        timetext.setText(myHour+":"+myMinute);

    }

    static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    public Date getDateFromString(String datetoSaved) {

        try {
            Date date = format.parse(datetoSaved);
            return date;
        } catch (ParseException e) {
            return null;
        }

    }


}

