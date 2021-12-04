package com.elececo.umoney;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Needs extends Fragment {

    public Needs() {
        // require a empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_needs, container, false);

    }
/*
    public void GivenButtonClick(View view) {
        Intent intent = new  Intent(getActivity(), TransactionGivenPage.class);
        startActivity(intent);
    }
    public void TakenButtonClick(View view) {
        Intent intent = new Intent(getActivity(), TransactionTakenPage.class);
        startActivity(intent);
    } */
}