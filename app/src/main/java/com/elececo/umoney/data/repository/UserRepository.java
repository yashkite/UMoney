package com.elececo.umoney.data.repository;

import com.elececo.umoney.data.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserRepository {
    private final FirebaseFirestore firestore;
    private static final String USERS_COLLECTION = "users";

    public UserRepository() {
        this.firestore = FirebaseFirestore.getInstance();
    }

    public Task<DocumentSnapshot> getUserData(String userId) {
        return firestore.collection(USERS_COLLECTION)
                       .document(userId)
                       .get();
    }

    public Task<Void> updateUser(String userId, User user) {
        return firestore.collection(USERS_COLLECTION)
                       .document(userId)
                       .set(user);
    }

    public Task<Void> deleteUser(String userId) {
        return firestore.collection(USERS_COLLECTION)
                       .document(userId)
                       .delete();
    }

    public Task<Void> updateUserField(String userId, String field, Object value) {
        return firestore.collection(USERS_COLLECTION)
                       .document(userId)
                       .update(field, value);
    }
}
