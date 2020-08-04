package com.example.kitchen_assistant.helpers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.kitchen_assistant.R;

public class Notification {

    private static final String CHANNEL_ID = "KitchenAssistant";
    private static final CharSequence CHANNEL_NAME = "KitchenAssistantNotifications";

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void createNotification(String title, String content, Context context) {
        // Create notification channel
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
        notificationManager.createNotificationChannel(notificationChannel);

        // Create notification builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notificationManager.notify(0, builder.build());
    }

}
