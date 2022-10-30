package com.elececo.umoney;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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


public class Needs extends Fragment {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    RecyclerView recyclerView;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    CollectionReference collectionReference = db.collection("Users").document(user.getEmail()).collection("Transactions");
    TransactionAdapter transactionAdapter;


    public Needs() {
        // require a empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.activity_needs, container, false);
        Button needsGivenButton = (Button) rootView.findViewById(R.id.needsGivenButton);
        Button needsTakenButton = (Button) rootView.findViewById(R.id.needsTakenButton);
        recyclerView = rootView.findViewById(R.id.needsRecyclerView);

        setUpRecyclerView();

        needsGivenButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TransactionPage.class);
                String button = "Given";
                intent.putExtra("WhichButton", button);
                startActivity(intent);
            }

        });
        needsTakenButton.setOnClickListener(new View.OnClickListener() {
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
        Query query = collectionReference.whereEqualTo("Category", "Needs")
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


