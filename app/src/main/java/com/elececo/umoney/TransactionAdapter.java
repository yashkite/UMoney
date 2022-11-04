package com.elececo.umoney;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TransactionAdapter extends FirestoreRecyclerAdapter<TransactionCard ,TransactionAdapter.TransactionViewHolder> {
    ArrayList<TransactionCard> transactionCardArrayList;
    Context context;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore dbroot = FirebaseFirestore.getInstance();

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public TransactionAdapter(@NonNull FirestoreRecyclerOptions<TransactionCard> options) {
        super(options);
    }


    @NonNull
    @Override
    public TransactionAdapter.TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_transaction_card, parent, false);
        return new TransactionViewHolder(v);
    }



    @Override
    protected void onBindViewHolder(@NonNull TransactionViewHolder holder, int position, @NonNull TransactionCard transactionCard) {
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

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.with.getContext());
                builder.setTitle("Are you sure?");
                builder.setMessage("Deletion Can't be Undo!!!");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String userEmail = user.getEmail();
                        dbroot.collection("Users").document(userEmail).collection("Transactions").document(transactionCard.getDocID()).delete();

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.show();
            }
        });
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent myIntent = new Intent(view.getContext(), TransactionPage.class);
                myIntent.putExtra("DocId", transactionCard.getDocID()); //Optional parameters
                view.getContext().startActivity(myIntent);




            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (holder.edit_delete_toggle.getVisibility() == GONE) {
                    holder.edit_delete_toggle.setVisibility(VISIBLE);
                } else {
                    holder.edit_delete_toggle.setVisibility(GONE);
                }
            }
        });

    }




    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView amountGiven, amountTaken, with, tag, date, time;
        TextView TP_with, TP_amount, TP_description, TP_categories, TP_tags, TP_date, TP_time, TP_dateAndTimePicker, TP_cancel, TP_done;
        LinearLayout edit_delete_toggle;
        Button delete, edit;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);

            amountGiven = itemView.findViewById(R.id.T_Amount_Given);
            amountTaken = itemView.findViewById(R.id.T_Amount_Taken);
            with = itemView.findViewById(R.id.T_with);
            tag = itemView.findViewById(R.id.Show_tag);
            date = itemView.findViewById(R.id.TC_Date);
            time = itemView.findViewById(R.id.TC_Time);
            edit_delete_toggle = itemView.findViewById(R.id.edit_delete_toggle);
            edit = itemView.findViewById(R.id.Edit);
            delete = itemView.findViewById(R.id.Delete);


            TP_amount = itemView.findViewById(R.id.TP_amount);


        }
    }
}
