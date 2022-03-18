package com.elececo.umoney;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class adapterTransaction extends FirebaseRecyclerAdapter<model, adapterTransaction.viewholder> {
    public adapterTransaction(@NonNull FirebaseRecyclerOptions<model> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull viewholder holder, int position, @NonNull model model) {
        holder.amountCard.setText(model.getAmount());
        holder.descriptionCard.setText(model.getDescription());
        holder.dateCard.setText(model.getDate());

    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_transaction, parent, false);
        return new viewholder(view);
    }

    class viewholder extends RecyclerView.ViewHolder {
        TextView amountCard, descriptionCard, dateCard;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            amountCard = (TextView) itemView.findViewById(R.id.amountCard);
            descriptionCard = (TextView) itemView.findViewById(R.id.dateCard);
            dateCard = (TextView) itemView.findViewById(R.id.dateCard);


        }
    }

}
