package com.elececo.umoney.data.repository;

import android.content.Context;
import android.content.Intent;
import com.elececo.umoney.data.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class AuthRepository {
    private final FirebaseFirestore firestore;
    private final GoogleSignInClient googleSignInClient;
    
    public AuthRepository(Context context) {
        firestore = FirebaseFirestore.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();
        googleSignInClient = GoogleSignIn.getClient(context, gso);
    }
    
    public Intent getSignInIntent() {
        return googleSignInClient.getSignInIntent();
    }
    
    public Task<GoogleSignInAccount> handleSignInResult(Intent data) {
        return GoogleSignIn.getSignedInAccountFromIntent(data);
    }
    
    public Task<DocumentSnapshot> getUserData(String uid) {
        return firestore.collection("users").document(uid).get();
    }
    
    public Task<Void> createNewUser(User user) {
        return firestore.collection("users").document(user.getUserId()).set(user);
    }
    
    public GoogleSignInAccount getCurrentUser(Context context) {
        return GoogleSignIn.getLastSignedInAccount(context);
    }
    
    public void signOut() {
        googleSignInClient.signOut();
    }
} 