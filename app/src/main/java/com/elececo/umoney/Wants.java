package com.elececo.umoney;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class Wants extends Fragment {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    RecyclerView recyclerView;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    CollectionReference collectionReference = db.collection("Users").document(user.getEmail()).collection("Transactions");
    ArrayList<TransactionCard> transactionCardArrayList;
    TransactionAdapter transactionAdapter;
    public Wants(){
        // require a empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.activity_wants, container,    false);
        Button wantsGivenButton = (Button) rootView.findViewById(R.id.wantsGivenButton);
        Button wantsTakenButton = (Button) rootView.findViewById(R.id.wantsTakenButton);
        recyclerView = rootView.findViewById(R.id.wantsRecyclerView);
        setUpRecyclerView();

        wantsGivenButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TransactionPage.class);
                String button = "Given";
                intent.putExtra("WhichButton", button);
                startActivity(intent);
            }

        });
        wantsTakenButton.setOnClickListener(new View.OnClickListener() {
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
        Query query =  collectionReference.whereEqualTo("Category", "Wants")
                .orderBy("Timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<TransactionCard> options = new FirestoreRecyclerOptions.Builder<TransactionCard>().setLifecycleOwner(this).setQuery(query, TransactionCard.class).build();
        transactionAdapter = new TransactionAdapter(options);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(transactionAdapter);

    }




}