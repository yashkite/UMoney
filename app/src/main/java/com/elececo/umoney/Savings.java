package com.elececo.umoney;

import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class Savings extends Fragment {
    Context context;

    public Savings(){
        // require a empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_savings, container,    false);
        Button savingsGivenButton = (Button) rootView.findViewById(R.id.savingsGivenButton);
        Button savingsTakenButton = (Button) rootView.findViewById(R.id.savingsTakenButton);





        savingsGivenButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TransactionPage.class);
                String button= "Given";
                intent.putExtra("WhichButton", button );
                startActivity(intent);
            }

        });
        savingsTakenButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TransactionPage.class);
                String button= "Taken";
                intent.putExtra("WhichButton", button );
                startActivity(intent);
            }
        });
        return rootView;

    }
}