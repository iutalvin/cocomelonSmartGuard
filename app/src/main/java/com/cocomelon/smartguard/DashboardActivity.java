package com.cocomelon.smartguard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.material.button.MaterialButton;

public class DashboardActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView txtWelcome, txtEmail;
    private MaterialButton btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard); // Ensure this matches your XML file name

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Find views
        txtWelcome = findViewById(R.id.txtWelcome);
        txtEmail = findViewById(R.id.txtEmail);
        btnLogout = findViewById(R.id.btnLogout);

        // Get current user
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // Display user information
            String welcomeMessage = "Welcome, " + (currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "User");
            txtWelcome.setText(welcomeMessage);

            String email = currentUser.getEmail();
            txtEmail.setText("Email: " + (email != null ? email : "No email available"));
        } else {
            Toast.makeText(this, "No user logged in!", Toast.LENGTH_SHORT).show();
            redirectToLogin();
        }

        // Logout button listener
        btnLogout.setOnClickListener(view -> {
            mAuth.signOut();
            Toast.makeText(DashboardActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            redirectToLogin();
        });
    }

    private void redirectToLogin() {
        Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
