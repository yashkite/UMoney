package com.elececo.umoney;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class Savings extends Fragment {

    public Savings(){
        // require a empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_savings, container,    false);
        Button savingsGivenButton = (Button) rootView.findViewById(R.id.savingsGivenButton);
        Button savingsTakenButton = (Button) rootView.findViewById(R.id.savingsTakenButton);
//        String savings = "savings";
//
//        savingsGivenButton.setOnClickListener(new View.OnClickListener(){
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                Intent intent = new  Intent(getActivity(), TransactionGivenPage.class);
//                intent.putExtra("From", savings);
//                startActivity(intent);
//            }
//
//        });
//        savingsTakenButton.setOnClickListener(new View.OnClickListener(){
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                Intent intent = new  Intent(getActivity(), TransactionTakenPage.class);
//                intent.putExtra("From", savings);
//                startActivity(intent);
//            }
//
//        });

        return rootView;

    }
}