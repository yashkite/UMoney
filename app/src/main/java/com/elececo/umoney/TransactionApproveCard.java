package com.elececo.umoney;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TransactionApproveCard extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] date;
    private final String[] paid_to;
    private final String[] amount;


    public TransactionApproveCard (Activity context, String[] date,String[] paid_to, String[] amount) {
        super(context, R.layout.activity_transaction_approve_card, date);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.date=date;
        this.paid_to=paid_to;
        this.amount=amount;

    }
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.activity_transaction_approve_card, null,true);

        TextView titleText = (TextView) rowView.findViewById(R.id.TA_Date);
        TextView imageView = (TextView) rowView.findViewById(R.id.TA_PaidTo);
        TextView subtitleText = (TextView) rowView.findViewById(R.id.TA_Amount);

        titleText.setText(date[position]);
        imageView.setText(paid_to[position]);
        subtitleText.setText(amount[position]);

        return rowView;

    };
}