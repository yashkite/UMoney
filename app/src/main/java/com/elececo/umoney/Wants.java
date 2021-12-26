package com.elececo.umoney;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class Wants extends Fragment {

    public Wants(){
        // require a empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.activity_wants, container,    false);
        Button wantsGivenButton = (Button) rootView.findViewById(R.id.wantsGivenButton);
        Button wantsTakenButton = (Button) rootView.findViewById(R.id.wantsTakenButton);
        wantsGivenButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new  Intent(getActivity(), TransactionGivenPage.class);
                int savings = 2;
                intent.putExtra("From", savings);
                startActivity(intent);
            }

        });
        wantsTakenButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new  Intent(getActivity(), TransactionTakenPage.class);
                int savings = 2;
                intent.putExtra("From", savings);
                startActivity(intent);
            }

        });

        return rootView;
    }
}