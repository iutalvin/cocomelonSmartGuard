package com.cocomelon.smartguard;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseNotificationListener {

    private static final String TAG = "FirebaseNotification";
    private static final String CHANNEL_ID = "smartguard_channel";
    private Context context;

    public FirebaseNotificationListener(Context context) {
        this.context = context;
        setupNotificationChannel();
        startListening();
    }

    private void setupNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "SmartGuard Alerts";
            String description = "Channel for SmartGuard motion sensor alerts";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void startListening() {
        DatabaseReference alertsRef = FirebaseDatabase.getInstance()
                .getReference("users/user123/alerts");

        alertsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                if (snapshot.exists()) {
                    String alertType = snapshot.child("alertType").getValue(String.class);
                    String sensorValue = snapshot.child("sensorValue").getValue(String.class);
                    String timestamp = snapshot.child("timestamp").getValue(String.class);

                    // Display a notification for the new alert
                    sendNotification(alertType, sensorValue, timestamp);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot snapshot, String previousChildName) {
                // Handle if necessary
            }

            @Override
            public void onChildRemoved(DataSnapshot snapshot) {
                // Handle if necessary
            }

            @Override
            public void onChildMoved(DataSnapshot snapshot, String previousChildName) {
                // Handle if necessary
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Database error: " + error.getMessage());
            }
        });
    }

    private void sendNotification(String alertType, String sensorValue, String timestamp) {
        String message = "New " + alertType + " detected! Sensor value: " + sensorValue + " at " + timestamp;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification) // Replace with your app's notification icon
                .setContentTitle("SmartGuard Alert")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
