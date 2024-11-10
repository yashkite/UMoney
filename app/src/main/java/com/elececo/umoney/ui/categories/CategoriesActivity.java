package com.elececo.umoney.ui.categories;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.lifecycle.ViewModelProvider;
import com.elececo.umoney.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import java.util.List;

public class CategoriesActivity extends AppCompatActivity {
    private CategoriesViewModel viewModel;
    private TabLayout tabLayout;
    private RecyclerView categoriesList;
    private CategoriesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        
        viewModel = new ViewModelProvider(this).get(CategoriesViewModel.class);
        
        setupViews();
        setupToolbar();
        setupTabs();
        setupObservers();
    }

    private void setupViews() {
        tabLayout = findViewById(R.id.tab_layout);
        categoriesList = findViewById(R.id.categories_list);
        FloatingActionButton addButton = findViewById(R.id.fab_add_category);
        
        adapter = new CategoriesAdapter(category -> showDeleteConfirmation(category), viewModel);
        categoriesList.setLayoutManager(new LinearLayoutManager(this));
        categoriesList.setAdapter(adapter);
        
        addButton.setOnClickListener(v -> showAddCategoryDialog());
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Categories");
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("Needs"));
        tabLayout.addTab(tabLayout.newTab().setText("Wants"));
        tabLayout.addTab(tabLayout.newTab().setText("Savings"));
        tabLayout.addTab(tabLayout.newTab().setText("Income"));
        
        // Select first tab by default
        viewModel.setCurrentType(0);
        
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewModel.setCurrentType(tab.getPosition());
            }
            
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupObservers() {
        viewModel.getCategories().observe(this, categories -> {
            if (categories != null) {
                adapter.setCategories(categories);
            }
        });

        viewModel.getError().observe(this, errorMessage -> {
            if (errorMessage != null) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading) {
                // Show loading indicator if needed
            } else {
                // Hide loading indicator if needed
            }
        });
    }

    private void showAddCategoryDialog() {
        EditText input = new EditText(this);
        input.setHint("Category Name");
        
        new MaterialAlertDialogBuilder(this)
            .setTitle("Add Category")
            .setView(input)
            .setPositiveButton("Add", (dialog, which) -> {
                String categoryName = input.getText().toString().trim();
                if (!categoryName.isEmpty()) {
                    // Check for duplicates
                    List<String> currentCategories = viewModel.getCategories().getValue();
                    if (currentCategories != null && currentCategories.contains(categoryName)) {
                        Toast.makeText(this, "Category already exists", Toast.LENGTH_SHORT).show();
                    } else {
                        viewModel.addCategory(categoryName);
                        Toast.makeText(this, "Category added successfully", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Category name cannot be empty", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void showDeleteConfirmation(String category) {
        new MaterialAlertDialogBuilder(this)
            .setTitle("Delete Category")
            .setMessage("Are you sure you want to delete this category?")
            .setPositiveButton("Delete", (dialog, which) -> {
                viewModel.deleteCategory(category);
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 