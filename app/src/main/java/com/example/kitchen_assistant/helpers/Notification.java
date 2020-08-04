package com.example.kitchen_assistant.helpers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.kitchen_assistant.R;

public class Notification {

    private static final String CHANNEL_ID = "KitchenAssistant";
    private static final CharSequence CHANNEL_NAME = "KitchenAssistantNotifications";
    private static NotificationChannel notificationChannel = null;
    private static NotificationManager notificationManager = null;

    @RequiresApi(api = Build.VERSION_CODES.O)

    public static void createShoppingNotification(String title, String content, Context context) {
        createNotificationChannel(context);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        notificationManager.notify(0, builder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void createNotificationChannel(Context context) {
        if (notificationChannel != null && notificationManager != null) return;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        int importance = NotificationManager.IMPORTANCE_LOW;
        if (notificationChannel == null) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

}
