package com.elececo.umoney.ui.categories;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.elececo.umoney.R;
import java.util.ArrayList;
import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {
    private List<String> categories = new ArrayList<>();
    private final CategoryClickListener listener;
    private final CategoriesViewModel viewModel;

    public interface CategoryClickListener {
        void onDeleteCategory(String category);
    }

    public CategoriesAdapter(CategoryClickListener listener, CategoriesViewModel viewModel) {
        this.listener = listener;
        this.viewModel = viewModel;
    }

    public void setCategories(List<String> newCategories) {
        this.categories = new ArrayList<>(newCategories);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String category = categories.get(position);
        boolean isDefault = viewModel.isDefaultCategory(category);
        holder.bind(category, listener, isDefault);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView categoryName;
        private final ImageButton deleteButton;

        ViewHolder(View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.category_name);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }

        void bind(String category, CategoryClickListener listener, boolean isDefault) {
            categoryName.setText(category);
            if (isDefault) {
                deleteButton.setVisibility(View.GONE);
            } else {
                deleteButton.setVisibility(View.VISIBLE);
                deleteButton.setOnClickListener(v -> listener.onDeleteCategory(category));
            }
        }
    }
} 