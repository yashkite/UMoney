package com.elececo.umoney.ui.categories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;

public class CategoriesViewModel extends ViewModel {
    private final FirebaseFirestore db;
    private final String userId;
    private final MutableLiveData<List<String>> categories;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private String[] types = {"NEEDS", "WANTS", "SAVINGS", "INCOME"};
    private int currentTypeIndex = 0;

    public static final Map<String, String[]> CATEGORIES = new HashMap<String, String[]>() {{
        put("NEEDS", new String[]{"Food", "Transportation", "Housing", "Utilities", "Healthcare", "Education"});
        put("WANTS", new String[]{"Entertainment", "Shopping", "Dining", "Travel", "Hobbies", "Gadgets"});
        put("SAVINGS", new String[]{"Emergency Fund", "Retirement", "Investment", "Goals", "Insurance"});
        put("INCOME", new String[]{"Salary", "Freelance", "Business", "Investment", "Rental", "Other"});
    }};

    public CategoriesViewModel() {
        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        categories = new MutableLiveData<>(new ArrayList<>());
        loadCategories();
    }

    public LiveData<List<String>> getCategories() {
        return categories;
    }

    public void setCurrentType(int index) {
        currentTypeIndex = index;
        loadCategories();
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    private void loadCategories() {
        isLoading.postValue(true);
        db.collection("users")
            .document(userId)
            .collection("categories")
            .document(types[currentTypeIndex])
            .get()
            .addOnSuccessListener(document -> {
                List<String> categoryList = new ArrayList<>();
                if (document.exists() && document.get("items") != null) {
                    categoryList.addAll((List<String>) document.get("items"));
                } else {
                    categoryList.addAll(Arrays.asList(CATEGORIES.get(types[currentTypeIndex])));
                    Map<String, Object> data = new HashMap<>();
                    data.put("items", categoryList);
                    db.collection("users")
                        .document(userId)
                        .collection("categories")
                        .document(types[currentTypeIndex])
                        .set(data);
                }
                categories.postValue(categoryList);
                isLoading.postValue(false);
            })
            .addOnFailureListener(e -> {
                List<String> defaultList = new ArrayList<>(Arrays.asList(CATEGORIES.get(types[currentTypeIndex])));
                categories.postValue(defaultList);
                isLoading.postValue(false);
            });
    }

    public void addCategory(String category) {
        List<String> originalList = categories.getValue();
        if (originalList == null) {
            originalList = new ArrayList<>();
            originalList.addAll(Arrays.asList(CATEGORIES.get(types[currentTypeIndex])));
        }
        
        if (!originalList.contains(category)) {
            final List<String> updatedList = new ArrayList<>(originalList);
            updatedList.add(category);
            
            Map<String, Object> data = new HashMap<>();
            data.put("items", updatedList);
            
            isLoading.postValue(true);
            db.collection("users")
                .document(userId)
                .collection("categories")
                .document(types[currentTypeIndex])
                .set(data)
                .addOnSuccessListener(aVoid -> {
                    categories.postValue(updatedList);
                    isLoading.postValue(false);
                })
                .addOnFailureListener(e -> {
                    error.postValue("Failed to add category");
                    isLoading.postValue(false);
                    loadCategories();
                });
        }
    }

    public void deleteCategory(String category) {
        List<String> originalList = categories.getValue();
        if (originalList != null && originalList.contains(category)) {
            final List<String> updatedList = new ArrayList<>(originalList);
            final List<String> previousList = new ArrayList<>(originalList);
            updatedList.remove(category);
            categories.setValue(updatedList);
            
            Map<String, Object> data = new HashMap<>();
            data.put("items", updatedList);
            
            db.collection("users")
                .document(userId)
                .collection("categories")
                .document(types[currentTypeIndex])
                .set(data)
                .addOnSuccessListener(aVoid -> {
                    categories.setValue(updatedList);
                })
                .addOnFailureListener(e -> {
                    categories.setValue(previousList);
                });
        }
    }

    public boolean isDefaultCategory(String category) {
        String[] defaultCategories = CATEGORIES.get(types[currentTypeIndex]);
        return defaultCategories != null && Arrays.asList(defaultCategories).contains(category);
    }
} 