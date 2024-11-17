package com.elececo.umoney.ui.profile.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.elececo.umoney.data.model.User;
import com.elececo.umoney.data.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

public class ProfileViewModel extends AndroidViewModel {
    
    private final UserRepository userRepository;
    private final MutableLiveData<User> userData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> updateResult = new MutableLiveData<>();

    public ProfileViewModel(Application application) {
        super(application);
        userRepository = new UserRepository();
    }

    public void loadUserData() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRepository.getUserData(userId)
            .addOnSuccessListener(documentSnapshot -> {
                User user = documentSnapshot.toObject(User.class);
                userData.setValue(user);
            })
            .addOnFailureListener(e -> {
                updateResult.setValue(false);
            });
    }

    public void updateProfile(String name, double monthlyIncome, String employmentType) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        User currentUser = userData.getValue();
        
        if (currentUser != null) {
            currentUser.setDisplayName(name);
            currentUser.setMonthlyIncome(monthlyIncome);
            currentUser.setEmploymentType(employmentType);

            userRepository.updateUser(userId, currentUser)
                .addOnSuccessListener(aVoid -> updateResult.setValue(true))
                .addOnFailureListener(e -> updateResult.setValue(false));
        }
    }

    public LiveData<User> getUserData() {
        return userData;
    }

    public LiveData<Boolean> getUpdateResult() {
        return updateResult;
    }
} 