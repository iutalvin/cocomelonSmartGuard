package com.cocomelon.smartguard;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseUtils {

    private static FirebaseAuth auth;
    private static DatabaseReference databaseReference;

    // Get Firebase Auth instance
    public static FirebaseAuth getAuthInstance() {
        if (auth == null) {
            auth = FirebaseAuth.getInstance();
        }
        return auth;
    }

    // Get Firebase Realtime Database reference
    public static DatabaseReference getDatabaseReference() {
        if (databaseReference == null) {
            databaseReference = FirebaseDatabase.getInstance().getReference();
        }
        return databaseReference;
    }

    // Get reference to 'users' node
    public static DatabaseReference getUsersReference() {
        return getDatabaseReference().child("users");
    }
}
