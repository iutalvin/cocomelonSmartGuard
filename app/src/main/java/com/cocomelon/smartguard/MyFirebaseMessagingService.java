package com.cocomelon.smartguard;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.messaging.FirebaseMessaging;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMessagingService";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "Message received from: " + remoteMessage.getFrom());

        // Handle notification payload
        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();

            Log.d(TAG, "Notification Title: " + title);
            Log.d(TAG, "Notification Body: " + body);

            // Display the notification
            showNotification(title, body);
        }

        // Handle data payload
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Data Payload: " + remoteMessage.getData());
            // Handle custom data payload
            String action = remoteMessage.getData().get("action"); // Example of custom key "action"
            handleDataPayload(action);
        }
    }

    // Display the notification
    private void showNotification(String title, String body) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "fcm_default_channel";

        // Create notification channel for devices running Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "FCM Notifications",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Channel for SmartGuard alerts and notifications.");
            notificationManager.createNotificationChannel(channel);
        }

        // Set up the intent for when the user taps the notification
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Build and show the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_notification) // Use your app's notification icon
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        notificationManager.notify(0, builder.build());
    }

    // Handle custom actions based on the data payload
    private void handleDataPayload(String action) {
        if ("alert".equals(action)) {
            Log.d(TAG, "Action: Alert received");
            // Handle alert-specific logic here
        } else if ("update".equals(action)) {
            Log.d(TAG, "Action: Update received");
            // Handle update-specific logic here
        } else {
            Log.d(TAG, "Action: Unknown action received");
        }
    }

    // This method is triggered whenever the FCM token is refreshed
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "New FCM Token: " + token);

        // Send the updated token to your server
        sendTokenToServer(token);

        // Subscribe to the topic based on the user's UID
        subscribeToUserTopic(token);
    }

    // Subscribe to the topic based on the user's UID
    private void subscribeToUserTopic(String token) {
        String uid = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : "guest";

        // Subscribe the device to the topic with the user's UID
        FirebaseMessaging.getInstance().subscribeToTopic(uid)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Successfully subscribed to topic: " + uid);
                    } else {
                        Log.e(TAG, "Subscription failed", task.getException());
                    }
                });
    }

    // Send the FCM token to your backend server or Firebase
    private void sendTokenToServer(String token) {
        try {
            String userId = FirebaseAuth.getInstance().getCurrentUser() != null
                    ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                    : "guest";

            Log.d(TAG, "Sending FCM token for userId: " + userId);

            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            Call<ResponseBody> call = apiService.updateFcmToken(userId, token);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "FCM token successfully sent to the server.");
                    } else {
                        Log.e(TAG, "Failed to send token to server: " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e(TAG, "Error sending token to server: " + t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error sending token to server: " + e.getMessage());
        }
    }
}
