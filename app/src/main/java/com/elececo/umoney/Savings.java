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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Savings extends Fragment {
    Context context;
    RecyclerView recyclerView;
    ArrayList<TransactionCard> transactionCardArrayList;
    TransactionAdapter transactionAdapter;
    FirebaseFirestore db;
    public Savings(){
        // require a empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_savings, container,    false);
        Button savingsGivenButton = (Button) rootView.findViewById(R.id.savingsGivenButton);
        Button savingsTakenButton = (Button) rootView.findViewById(R.id.savingsTakenButton);
        recyclerView = rootView.findViewById(R.id.savingsRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        db = FirebaseFirestore.getInstance();
        transactionCardArrayList = new ArrayList<TransactionCard>();

        transactionAdapter = new TransactionAdapter(getActivity(), transactionCardArrayList);
        recyclerView.setAdapter(transactionAdapter);
        EventChangeListener();




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
    private void EventChangeListener() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userEmail = user.getEmail();
        db.collection("Users")
                .document(userEmail)
//                .collection("TransactionList")
                .collection("Savings")
//                .collection("Tags")
//                .document("Food")
//                .collection("Given")
                .orderBy("With", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e("Firestore error", error.getMessage());
                            return;
                        }
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                transactionCardArrayList.add(dc.getDocument().toObject(TransactionCard.class));
                            }
                            transactionAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

}