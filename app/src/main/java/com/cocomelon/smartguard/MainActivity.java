package com.cocomelon.smartguard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);

        // Check if the user is logged in
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            // If the user is not logged in, redirect to the LoginActivity
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            // If the user is logged in, redirect to the DashboardActivity
            startActivity(new Intent(MainActivity.this, DashboardActivity.class));

            // Initialize FirebaseNotificationListener
            new FirebaseNotificationListener(this);
        }
        finish();  // Close the MainActivity to prevent going back to it
        retrieveFCMToken(); // Optionally retrieve and send FCM token
    }

    private void retrieveFCMToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM token failed", task.getException());
                            return;
                        }

                        String token = task.getResult();
                        Log.d(TAG, "FCM Token: " + token);
                        sendTokenToServer(token);
                    }
                });
    }

    private void sendTokenToServer(String token) {
        Log.d(TAG, "Token sent to server: " + token);
        // Add your server-side token logic here
    }
}
