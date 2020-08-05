package com.example.kitchen_assistant.helpers;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.kitchen_assistant.R;
import com.example.kitchen_assistant.activities.LoginActivity;
import com.example.kitchen_assistant.activities.MainActivity;
import com.example.kitchen_assistant.models.Product;

public class Notification {

    private static final String CHANNEL_ID = "KitchenAssistant";
    private static final CharSequence CHANNEL_NAME = "KitchenAssistantNotifications";
    public static final String KEY_QUANTITY_INPUT = "Quantity";
    private static final CharSequence QUANTITY_LABEL = "Quantity" ;
    public static final String KEY_UNIT = "QuantityUnit";
    public static final String KEY_PRODUCT_NAME = "Product Name";
    private static NotificationChannel notificationChannel = null;
    public static NotificationManager notificationManager = null;
    private static String SHOPPING_NOTIFICATION_TITLE = "Restock needed!";

    @RequiresApi(api = Build.VERSION_CODES.O)

    public static void createShoppingNotification(Product product, Context context) {
        createNotificationChannel(context);

        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(MainActivity.INTENT_NAME, MainActivity.SHOPPING_NOTIFICATION_TAG);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        Intent shoppingIntent = new Intent(context, ActionReceiver.class);
        shoppingIntent.putExtra(KEY_QUANTITY_INPUT, product.getOriginalQuantity());
        shoppingIntent.putExtra(KEY_UNIT, product.getQuantityUnit());
        shoppingIntent.putExtra(KEY_PRODUCT_NAME, product.getProductName());

        PendingIntent actionIntent = PendingIntent.getBroadcast(context, 0, shoppingIntent, 0);

        androidx.core.app.NotificationCompat.Action action = new androidx.core.app.NotificationCompat.Action.Builder(
                R.drawable.ic_shop, "ADD TO SHOPPING LIST", actionIntent)
                .build();

        String content = "You're running out of " + product.getProductName();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(SHOPPING_NOTIFICATION_TITLE)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .addAction(action)
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
