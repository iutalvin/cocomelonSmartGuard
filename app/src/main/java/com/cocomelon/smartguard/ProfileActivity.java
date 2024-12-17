package com.cocomelon.smartguard;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileActivity extends AppCompatActivity {

    private TextView txtName, txtEmail;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        txtName = findViewById(R.id.txtName);
        txtEmail = findViewById(R.id.txtEmail);

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        loadUserProfile();
    }

    private void loadUserProfile() {
        String userId = auth.getCurrentUser().getUid();
        databaseReference.child(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DataSnapshot snapshot = task.getResult();
                txtName.setText(snapshot.child("name").getValue(String.class));
                txtEmail.setText(snapshot.child("email").getValue(String.class));
            } else {
                txtName.setText("Error loading profile");
                txtEmail.setText("Error loading profile");
            }
        });
    }
}
