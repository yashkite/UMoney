package com.elececo.umoney;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Savings extends Fragment {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    RecyclerView recyclerView;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    CollectionReference collectionReference = db.collection("Users").document(user.getEmail()).collection("Transactions");
    ArrayList<TransactionCard> transactionCardArrayList;
    TransactionAdapter transactionAdapter;
    public Savings(){
        // require a empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_savings, container,    false);
        Button savingsGivenButton = (Button) rootView.findViewById(R.id.savingsGivenButton);
        Button savingsTakenButton = (Button) rootView.findViewById(R.id.savingsTakenButton);
        recyclerView = rootView.findViewById(R.id.savingsRecyclerView);

        setUpRecyclerView();



        savingsGivenButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TransactionPage.class);
                String button = "Given";
                intent.putExtra("WhichButton", button);
                startActivity(intent);
            }

        });
        savingsTakenButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TransactionPage.class);
                String button = "Taken";
                intent.putExtra("WhichButton", button);
                startActivity(intent);
            }
        });
        return rootView;

    }
    private void setUpRecyclerView() {
        Query query = (Query) collectionReference.whereEqualTo("Category", "Savings")
                .orderBy("Timestamp", Query.Direction.DESCENDING);


        FirestoreRecyclerOptions<TransactionCard> options = new FirestoreRecyclerOptions.Builder<TransactionCard>().setQuery(query, TransactionCard.class).build();
        transactionAdapter = new TransactionAdapter(options);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(transactionAdapter);

    }
    @Override
    public void onStart() {
        super.onStart();
        transactionAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        transactionAdapter.stopListening();
    }



}