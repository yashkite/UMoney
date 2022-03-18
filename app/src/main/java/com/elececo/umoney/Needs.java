package com.elececo.umoney;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;


public class Needs extends Fragment {
    Context context;
    RecyclerView recyclerView;
    adapterTransaction adapTransaction;

    public Needs() {
        // require a empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.activity_needs, container, false);
        Button needsGivenButton = (Button) rootView.findViewById(R.id.needsGivenButton);
        Button needsTakenButton = (Button) rootView.findViewById(R.id.needsTakenButton);
        String Needs = "needs";

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerNeeds);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        FirebaseRecyclerOptions<model> options =
                new FirebaseRecyclerOptions.Builder<model>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("givenneeds"), model.class)
                        .build();
        adapTransaction = new adapterTransaction(options);
        recyclerView.setAdapter(adapTransaction);

        needsGivenButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), TransactionGivenPage.class);
                intent.putExtra("From", Needs);
                startActivity(intent);
            }

        });
        needsTakenButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TransactionTakenPage.class);
                intent.putExtra("From", Needs);
                startActivity(intent);
            }

        });
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapTransaction.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapTransaction.stopListening();
    }

}


