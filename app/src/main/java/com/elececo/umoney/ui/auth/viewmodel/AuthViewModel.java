package com.elececo.umoney.ui.auth.viewmodel;

import android.app.Application;
import android.content.Intent;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.elececo.umoney.R;
import com.elececo.umoney.data.model.User;
import com.elececo.umoney.data.model.AuthResult;
import com.elececo.umoney.data.repository.AuthRepository;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import java.util.HashMap;
import java.util.Map;

public class AuthViewModel extends AndroidViewModel {
    private final AuthRepository authRepository;
    private final MutableLiveData<User> userLiveData;
    private final MutableLiveData<Boolean> isFirstTimeUser;
    private final MutableLiveData<AuthResult> authResult;
    
    public AuthViewModel(Application application) {
        super(application);
        String webClientId = application.getString(R.string.default_web_client_id);
        authRepository = new AuthRepository(application, webClientId);
        userLiveData = new MutableLiveData<>();
        isFirstTimeUser = new MutableLiveData<>();
        authResult = new MutableLiveData<>();
    }
    
    public Intent getSignInIntent() {
        return authRepository.getSignInIntent(getApplication());
    }
    
    public void handleSignInResult(Intent data) {
        GoogleSignIn.getSignedInAccountFromIntent(data)
            .addOnSuccessListener(account -> {
                firebaseAuthWithGoogle(account.getIdToken());
            })
            .addOnFailureListener(e -> {
                authResult.setValue(new AuthResult(false, e.getMessage()));
            });
    }
    
    private void firebaseAuthWithGoogle(String idToken) {
        authRepository.firebaseAuthWithGoogle(idToken)
            .addOnSuccessListener(authResult -> {
                FirebaseUser firebaseUser = authResult.getUser();
                if (firebaseUser != null) {
                    User user = new User(
                        firebaseUser.getUid(),
                        firebaseUser.getDisplayName(),
                        firebaseUser.getEmail(),
                        firebaseUser.getPhotoUrl() != null ? firebaseUser.getPhotoUrl().toString() : null
                    );
                    userLiveData.setValue(user);
                    checkOrCreateUser(user);
                }
            })
            .addOnFailureListener(e -> {
                this.authResult.setValue(new AuthResult(false, e.getMessage()));
            });
    }
    
    public FirebaseUser getCurrentUser() {
        return authRepository.getCurrentUser();
    }
    
    public void checkUserStatus() {
        FirebaseUser user = getCurrentUser();
        if (user != null) {
            authRepository.getUserData(user.getUid())
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User userData = documentSnapshot.toObject(User.class);
                        isFirstTimeUser.setValue(userData != null && userData.isFirstTimeUser());
                    } else {
                        isFirstTimeUser.setValue(true);
                    }
                })
                .addOnFailureListener(e -> {
                    authResult.setValue(new AuthResult(false, e.getMessage()));
                });
        }
    }
    
    private void checkOrCreateUser(User user) {
        authRepository.getUserData(user.getUserId())
            .addOnSuccessListener(documentSnapshot -> {
                if (!documentSnapshot.exists()) {
                    authRepository.createNewUser(user)
                        .addOnSuccessListener(aVoid -> {
                            isFirstTimeUser.setValue(true);
                            userLiveData.setValue(user);
                            authResult.setValue(new AuthResult(true, null));
                        })
                        .addOnFailureListener(e -> {
                            authResult.setValue(new AuthResult(false, e.getMessage()));
                        });
                } else {
                    User existingUser = documentSnapshot.toObject(User.class);
                    boolean isFirstTime = existingUser == null || 
                        existingUser.isFirstTimeUser() || 
                        existingUser.getMonthlyIncome() == 0 ||
                        existingUser.getDisplayName() == null;
                    isFirstTimeUser.setValue(isFirstTime);
                    userLiveData.setValue(existingUser);
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
    
    public LiveData<Boolean> saveUserSetup(String displayName, double monthlyIncome, String employmentType) {
        MutableLiveData<Boolean> setupResult = new MutableLiveData<>();
        FirebaseUser currentUser = getCurrentUser();
        
        if (currentUser != null) {
            String userId = currentUser.getUid();
            Map<String, Object> updates = new HashMap<>();
            updates.put("displayName", displayName);
            updates.put("monthlyIncome", monthlyIncome);
            updates.put("employmentType", employmentType);
            updates.put("firstTimeUser", false);
            updates.put("needsPercentage", 50);
            updates.put("wantsPercentage", 30);
            updates.put("savingsPercentage", 20);
            
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build();
                
            currentUser.updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        authRepository.updateUser(userId, updates)
                            .addOnSuccessListener(aVoid -> setupResult.setValue(true))
                            .addOnFailureListener(e -> setupResult.setValue(false));
                    } else {
                        setupResult.setValue(false);
                    }
                });
        } else {
            setupResult.setValue(false);
        }
        
        return setupResult;
    }
    
    public void setIsFirstTimeUser(boolean isFirstTime) {
        isFirstTimeUser.setValue(isFirstTime);
        
        if (getCurrentUser() != null) {
            String userId = getCurrentUser().getUid();
            Map<String, Object> updates = new HashMap<>();
            updates.put("firstTimeUser", isFirstTime);
            
            authRepository.updateUser(userId, updates)
                .addOnFailureListener(e -> {
                    // If update fails, revert the local value
                    isFirstTimeUser.setValue(!isFirstTime);
                });
        }
    }
} 