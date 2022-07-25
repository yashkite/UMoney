package com.elececo.umoney;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    Context context;
    ArrayList<TransactionCard> transactionCardArrayList;

    public TransactionAdapter(Context context, ArrayList<TransactionCard> transactionArrayList) {
        this.context = context;
        this.transactionCardArrayList = transactionArrayList;
    }

    @NonNull
    @Override
    public TransactionAdapter.TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.activity_transaction_card, parent, false);
        return new TransactionViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionAdapter.TransactionViewHolder holder, int position) {
        TransactionCard transactionCard = transactionCardArrayList.get(position);


        long datentime = transactionCard.Timestamp.getSeconds();

        Date timeD = new Date(datentime * 1000);
        SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat time = new SimpleDateFormat("hh:mm a");
        String showDate = date.format(timeD);
        String showTime = time.format(timeD);

        holder.date.setText(showDate);
        holder.time.setText(showTime);



        if (transactionCard.GivenOrTaken.matches("Given")) {
            holder.amountGiven.setText(transactionCard.Amount);
        } else {
            holder.amountTaken.setText(transactionCard.Amount);
        }
        holder.with.setText(transactionCard.With);
        holder.tag.setText(transactionCard.Tag);
    }

    @Override
    public int getItemCount() {
        return transactionCardArrayList.size();
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView amountGiven,amountTaken, with, tag, date, time;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);

            amountGiven = itemView.findViewById(R.id.T_Amount_Given);
            amountTaken = itemView.findViewById(R.id.T_Amount_Taken);
            with = itemView.findViewById(R.id.T_with);
            tag = itemView.findViewById(R.id.Show_tag);
            date = itemView.findViewById(R.id.TC_Date);
            time = itemView.findViewById(R.id.TC_Time);

        }
    }
}
