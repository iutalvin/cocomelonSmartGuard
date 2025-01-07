package com.cocomelon.smartguard;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

    // Get current Firebase User
    public static FirebaseUser getCurrentUser() {
        return getAuthInstance().getCurrentUser();
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

    // Get reference to a specific user by UID
    public static DatabaseReference getUserReference(String uid) {
        return getUsersReference().child(uid);
    }

    // Get reference to 'sensors' node
    public static DatabaseReference getSensorsReference() {
        return getDatabaseReference().child("sensors");
    }

    // Get reference to a specific sensor by ID
    public static DatabaseReference getSensorReference(String sensorId) {
        return getSensorsReference().child(sensorId);
    }

    // Listen for changes in a specific database reference
    public static void listenForDataChanges(DatabaseReference reference, ValueEventListener listener) {
        reference.addValueEventListener(listener);
    }

    // Remove a listener from a specific database reference
    public static void removeDataChangeListener(DatabaseReference reference, ValueEventListener listener) {
        reference.removeEventListener(listener);
    }

    // Save data to a specific reference
    public static void saveData(DatabaseReference reference, Object data) {
        reference.setValue(data);
    }

    // Push data to a list and generate a unique ID
    public static DatabaseReference pushData(DatabaseReference reference, Object data) {
        DatabaseReference newRef = reference.push();
        newRef.setValue(data);
        return newRef;
    }

    // Get reference to 'notifications' node
    public static DatabaseReference getNotificationsReference() {
        return getDatabaseReference().child("notifications");
    }

    // Get reference to a specific user's notifications
    public static DatabaseReference getUserNotificationsReference(String uid) {
        return getNotificationsReference().child(uid);
    }
}
