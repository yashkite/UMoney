package com.elececo.umoney.data.repository;

import android.content.Context;
import android.content.Intent;
import com.elececo.umoney.data.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.DocumentSnapshot;
import java.util.Map;

public class AuthRepository {
    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore firestore;
    private final GoogleSignInOptions gso;
    
    public AuthRepository(Context context, String webClientId) {
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(webClientId)
                .requestEmail()
                .build();
    }
    
    public Intent getSignInIntent(Context context) {
        return GoogleSignIn.getClient(context, gso).getSignInIntent();
    }
    
    public Task<AuthResult> firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        return firebaseAuth.signInWithCredential(credential);
    }
    
    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }
    
    public Task<Void> createNewUser(User user) {
        return firestore.collection("users").document(user.getUserId()).set(user);
    }
    
    public void signOut(Context context) {
        firebaseAuth.signOut();
        GoogleSignIn.getClient(context, gso).signOut();
    }
    
    public Task<DocumentSnapshot> getUserData(String userId) {
        return firestore.collection("users")
                       .document(userId)
                       .get();
    }
    
    public Task<Void> updateUser(String userId, Map<String, Object> updates) {
        return FirebaseFirestore.getInstance()
            .collection("users")
            .document(userId)
            .update(updates);
    }
} 