package com.elececo.umoney;

import android.content.Context;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class Needs extends Fragment {
    Context context;
    RecyclerView recyclerView;
    ArrayList<TransactionCard> transactionCardArrayList;
    TransactionAdapter transactionAdapter;
    FirebaseFirestore db;

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
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        db = FirebaseFirestore.getInstance();
        transactionCardArrayList = new ArrayList<TransactionCard>();

        transactionAdapter = new TransactionAdapter(getActivity(), transactionCardArrayList);
        recyclerView.setAdapter(transactionAdapter);
        EventChangeListener();

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

    private void EventChangeListener() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userEmail = user.getEmail();
        db.collection("Users")
                .document(userEmail)
//                .collection("TransactionList")
                .collection("Needs")
//                .collection("Tags")
//                .document("Food")
//                .collection("Given")
                .orderBy("Timestamp", Query.Direction.DESCENDING)
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


