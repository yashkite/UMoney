package com.elececo.umoney.ui.auth.viewmodel;

import android.app.Application;
import android.content.Intent;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.elececo.umoney.data.model.User;
import com.elececo.umoney.data.model.AuthResult;
import com.elececo.umoney.data.repository.AuthRepository;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class AuthViewModel extends AndroidViewModel {
    private final AuthRepository authRepository;
    private final MutableLiveData<User> userLiveData;
    private final MutableLiveData<Boolean> isFirstTimeUser;
    private final MutableLiveData<AuthResult> authResult;
    
    public AuthViewModel(Application application) {
        super(application);
        authRepository = new AuthRepository(application);
        userLiveData = new MutableLiveData<>();
        isFirstTimeUser = new MutableLiveData<>();
        authResult = new MutableLiveData<>();
    }
    
    public Intent getSignInIntent() {
        return authRepository.getSignInIntent();
    }
    
    public void handleSignInResult(Intent data) {
        authRepository.handleSignInResult(data)
            .addOnSuccessListener(account -> {
                User user = new User(
                    account.getId(),
                    account.getDisplayName(),
                    account.getEmail(),
                    account.getPhotoUrl() != null ? account.getPhotoUrl().toString() : null
                );
                userLiveData.setValue(user);
                checkOrCreateUser(user);
            })
            .addOnFailureListener(e -> {
                authResult.setValue(new AuthResult(false, e.getMessage()));
            });
    }
    
    public GoogleSignInAccount getCurrentUser() {
        return authRepository.getCurrentUser(getApplication());
    }
    
    public void checkUserStatus() {
        GoogleSignInAccount account = getCurrentUser();
        if (account != null) {
            authRepository.getUserData(account.getId())
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        isFirstTimeUser.setValue(user != null && user.isFirstTimeUser());
                    } else {
                        // If document doesn't exist, treat as first time user
                        isFirstTimeUser.setValue(true);
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure case
                    authResult.setValue(new AuthResult(false, e.getMessage()));
                });
        }
    }
    
    private void checkOrCreateUser(User user) {
        authRepository.getUserData(user.getUserId())
            .addOnSuccessListener(documentSnapshot -> {
                if (!documentSnapshot.exists()) {
                    // New user
                    authRepository.createNewUser(user)
                        .addOnSuccessListener(aVoid -> {
                            isFirstTimeUser.setValue(true);
                            authResult.setValue(new AuthResult(true, null));
                        });
                } else {
                    User existingUser = documentSnapshot.toObject(User.class);
                    isFirstTimeUser.setValue(existingUser != null && existingUser.isFirstTimeUser());
                    authResult.setValue(new AuthResult(true, null));
                }
            });
    }
    
    public LiveData<User> getUserLiveData() {
        return userLiveData;
    }
    
    public LiveData<Boolean> getIsFirstTimeUser() {
        return isFirstTimeUser;
    }
    
    public LiveData<AuthResult> getAuthResult() {
        return authResult;
    }
} 