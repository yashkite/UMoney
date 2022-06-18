package com.elececo.umoney;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;





public class Needs extends Fragment {
    Context context;
    public Needs() {
        // require a empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.activity_needs, container, false);
        Button needsGivenButton = (Button) rootView.findViewById(R.id.needsGivenButton);
        Button needsTakenButton = (Button) rootView.findViewById(R.id.needsTakenButton);





        needsGivenButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TransactionPage.class);
                String button= "Given";
               intent.putExtra("WhichButton", button );
                startActivity(intent);
            }

        });
        needsTakenButton.setOnClickListener(new View.OnClickListener() {
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


