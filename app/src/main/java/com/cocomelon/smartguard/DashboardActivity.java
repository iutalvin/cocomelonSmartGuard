package com.cocomelon.smartguard;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DashboardActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private FirebaseAuth mAuth;
    private TextView userNameTextView, connectedDevicesTextView, notificationsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize Firebase Auth
        mAuth = FirebaseUtils.getAuthInstance();

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawable_layout);
        NavigationView navigationView = findViewById(R.id.NavigationView);

        // Set up the drawer toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.drawer_open, R.string.drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Set navigation item click listener
        navigationView.setNavigationItemSelectedListener(this::handleNavigation);

        // Initialize TextViews
        userNameTextView = findViewById(R.id.userName);
        connectedDevicesTextView = findViewById(R.id.connectedDevices);
        notificationsTextView = findViewById(R.id.notifications);

        // Ensure the user is logged in before proceeding
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Fetch profile data
            fetchData("profile", new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String name = snapshot.child("name").getValue(String.class);
                        userNameTextView.setText("Welcome, " + (name != null ? name : "User"));
                    } else {
                        showToast("No profile data found.");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    showToast("Error fetching profile: " + error.getMessage());
                }
            });

            // Fetch devices data
            fetchData("devices", new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    StringBuilder devices = new StringBuilder();
                    for (DataSnapshot deviceSnapshot : snapshot.getChildren()) {
                        String deviceName = deviceSnapshot.child("name").getValue(String.class);
                        String deviceStatus = deviceSnapshot.child("status").getValue(String.class);
                        devices.append(deviceName).append(": ").append(deviceStatus).append("\n");
                    }
                    connectedDevicesTextView.setText(devices.length() > 0 ? devices : "No devices connected.");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    showToast("Error fetching devices: " + error.getMessage());
                }
            });

            // Fetch alerts
            fetchAlerts();
            // Update navigation header
            updateNavHeader();
        } else {
            // Redirect to Login if no user is logged in
            startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
            finish();
        }
    }

    private boolean handleNavigation(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.nav_home) {
            showToast("Already on Dashboard");
        } else if (itemId == R.id.nav_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
        } else if (itemId == R.id.nav_devices) {
            startActivity(new Intent(this, DevicesActivity.class));
        } else if (itemId == R.id.nav_settings) {
            startActivity(new Intent(this, SettiingsActivity.class));
        } else if (itemId == R.id.nav_logout) {
            confirmLogout();
        } else {
            showToast("Unknown action");
        }

        drawerLayout.closeDrawers();
        return true;
    }

    private void fetchData(String path, ValueEventListener listener) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users")
                    .child(currentUser.getUid())
                    .child(path);

            // Check if data exists before calling listener
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        listener.onDataChange(snapshot);
                    } else {
                        showToast(path + " not found");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    showToast("Error fetching " + path + ": " + error.getMessage());
                }
            });
        } else {
            showToast("User not logged in");
        }
    }

    private void fetchAlerts() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            DatabaseReference alertsRef = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(currentUser.getUid())
                    .child("alerts");

            alertsRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    String alertType = snapshot.child("alertType").getValue(String.class);
                    String location = snapshot.child("location").getValue(String.class);
                    String timestamp = snapshot.child("timestamp").getValue(String.class);

                    notificationsTextView.append(alertType + " detected at " + location + " on " + timestamp + "\n");
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {}

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    showToast("Error fetching alerts: " + error.getMessage());
                }
            });
        }
    }

    private void updateNavHeader() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            NavigationView navigationView = findViewById(R.id.NavigationView);
            View headerView = navigationView.getHeaderView(0);

            TextView userNameHeader = headerView.findViewById(R.id.nav_header_username);
            TextView userEmailHeader = headerView.findViewById(R.id.nav_header_email);

            userNameHeader.setText(currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "User");
            userEmailHeader.setText(currentUser.getEmail() != null ? currentUser.getEmail() : "Email not available");
        }
    }

    private void confirmLogout() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to log out?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> logoutUser())
                .setNegativeButton("No", null)
                .show();
    }

    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        Intent loginIntent = new Intent(DashboardActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    private void showToast(String message) {
        Toast.makeText(DashboardActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
