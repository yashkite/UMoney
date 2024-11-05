package com.elececo.umoney.ui.common;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.elececo.umoney.R;
import com.elececo.umoney.data.model.Transaction;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {
    private List<Transaction> transactions;
    private final SimpleDateFormat dateFormat;

    public TransactionAdapter() {
        this.dateFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);
        holder.amountText.setText(String.format("₹%.2f", transaction.getAmount()));
        holder.recipientText.setText(transaction.getRecipient());
        holder.categoryText.setText(transaction.getCategory());
        holder.dateText.setText(dateFormat.format(transaction.getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return transactions != null ? transactions.size() : 0;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView amountText;
        TextView recipientText;
        TextView categoryText;
        TextView dateText;

        ViewHolder(View view) {
            super(view);
            amountText = view.findViewById(R.id.transaction_amount);
            recipientText = view.findViewById(R.id.transaction_recipient);
            categoryText = view.findViewById(R.id.transaction_category);
            dateText = view.findViewById(R.id.transaction_date);
        }
    }
} 