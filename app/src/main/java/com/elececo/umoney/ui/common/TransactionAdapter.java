package com.elececo.umoney.ui.common;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.elececo.umoney.R;
import com.elececo.umoney.data.model.Transaction;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {
    private List<Transaction> transactions = new ArrayList<>();
    private final SimpleDateFormat dateFormat;
    private final TransactionActionListener actionListener;

    public interface TransactionActionListener {
        void onEditTransaction(Transaction transaction);
        void onDeleteTransaction(Transaction transaction);
    }

    public TransactionAdapter(TransactionActionListener listener) {
        this.dateFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
        this.actionListener = listener;
    }

    public void setTransactions(List<Transaction> newTransactions) {
        List<Transaction> oldList = new ArrayList<>(this.transactions != null ? this.transactions : new ArrayList<>());
        List<Transaction> newList = newTransactions != null ? new ArrayList<>(newTransactions) : new ArrayList<>();
        
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new TransactionDiffCallback(oldList, newList));
        this.transactions = newList;
        diffResult.dispatchUpdatesTo(this);
        
        notifyDataSetChanged();
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
        holder.bind(transaction, dateFormat, actionListener);
    }

    @Override
    public int getItemCount() {
        return transactions != null ? transactions.size() : 0;
    }

    private static class TransactionDiffCallback extends DiffUtil.Callback {
        private final List<Transaction> oldList;
        private final List<Transaction> newList;

        TransactionDiffCallback(List<Transaction> oldList, List<Transaction> newList) {
            this.oldList = oldList != null ? oldList : new ArrayList<>();
            this.newList = newList != null ? newList : new ArrayList<>();
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            Transaction oldItem = oldList.get(oldItemPosition);
            Transaction newItem = newList.get(newItemPosition);
            
            if (oldItem == null || newItem == null) {
                return false;
            }
            
            String oldId = oldItem.getId();
            String newId = newItem.getId();
            
            return (oldId != null && newId != null && oldId.equals(newId));
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            Transaction oldItem = oldList.get(oldItemPosition);
            Transaction newItem = newList.get(newItemPosition);
            
            if (oldItem == null || newItem == null) {
                return false;
            }
            
            return oldItem.equals(newItem);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView amountText;
        TextView recipientText;
        TextView categoryText;
        TextView dateText;
        ImageButton optionsButton;

        ViewHolder(View view) {
            super(view);
            amountText = view.findViewById(R.id.transaction_amount);
            recipientText = view.findViewById(R.id.transaction_recipient);
            categoryText = view.findViewById(R.id.transaction_category);
            dateText = view.findViewById(R.id.transaction_date);
            optionsButton = view.findViewById(R.id.options_button);
        }

        void bind(Transaction transaction, SimpleDateFormat dateFormat, TransactionActionListener listener) {
            amountText.setText(String.format("₹%.2f", transaction.getAmount()));
            recipientText.setText(transaction.getRecipient());
            categoryText.setText(transaction.getCategory());
            dateText.setText(dateFormat.format(transaction.getTimestamp()));

            optionsButton.setOnClickListener(v -> {
                PopupMenu popup = new PopupMenu(v.getContext(), v);
                popup.inflate(R.menu.menu_transaction_options);
                popup.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.action_edit) {
                        listener.onEditTransaction(transaction);
                        return true;
                    } else if (item.getItemId() == R.id.action_delete) {
                        listener.onDeleteTransaction(transaction);
                        return true;
                    }
                    return false;
                });
                popup.show();
            });
        }
    }
} 