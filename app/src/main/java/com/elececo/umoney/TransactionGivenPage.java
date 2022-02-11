package com.elececo.umoney;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class TransactionGivenPage extends AppCompatActivity {
    EditText Amount, Description;
    Button Done;
    String from, curDate;
    CalendarView Date;
    DatabaseReference db;
    Calendar defaultDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_given_page);


        Amount = findViewById(R.id.amountGiven);
        Description = findViewById(R.id.descriptionGiven);




        Date = findViewById(R.id.calendarGiven);
        defaultDate = Calendar.getInstance();
        Date.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {
                curDate = String.valueOf(dayOfMonth + "-" + month + "-" + year);
            }

        });

        Intent intent = getIntent();
        from = intent.getStringExtra("From");

        getSupportActionBar().setTitle(from);


        Done = findViewById(R.id.doneGiven);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat ss = new SimpleDateFormat("dd-"+"MM"+"-yyyy");
        java.util.Date date = new Date();
        curDate= ss.format(date);
        Log.e("date", curDate);

        Done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db = FirebaseDatabase.getInstance("https://umoney-elececo-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

                String amount = Amount.getText().toString();
                String date = curDate;
                String description = Description.getText().toString();

                HashMap <String, String> givendata = new HashMap<>();
                givendata.put("amount" , amount);
                givendata.put("date" , date);
                givendata.put("description" , description);

                db.child("given"+from).push().setValue(givendata);

                Log.e("amount", amount);
            }
        });



    }

}

