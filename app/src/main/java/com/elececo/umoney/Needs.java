package com.elececo.umoney;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;



public class Needs extends Fragment {

    public Needs() {
        // require a empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.activity_needs, container,    false);
        Button needsGivenButton = (Button) rootView.findViewById(R.id.needsGivenButton);
        Button needsTakenButton = (Button) rootView.findViewById(R.id.needsTakenButton);
        needsGivenButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                // TODO Auto-generated method stub

                Intent intent = new  Intent(getActivity(), TransactionGivenPage.class);
                startActivity(intent);
            }

        });
        needsTakenButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new  Intent(getActivity(), TransactionTakenPage.class);
                startActivity(intent);
            }

        });

        return rootView;

    }


}