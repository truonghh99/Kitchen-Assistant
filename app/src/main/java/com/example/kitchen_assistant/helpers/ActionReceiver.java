package com.example.kitchen_assistant.helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.kitchen_assistant.storage.CurrentShoppingList;

public class ActionReceiver extends BroadcastReceiver {

    private final String TAG = "ActionReciever";

    @Override
    public void onReceive(Context context, Intent intent) {

        String name = intent.getStringExtra(Notification.KEY_PRODUCT_NAME);
        String unit = intent.getStringExtra(Notification.KEY_UNIT);
        Float quantity = intent.getFloatExtra(Notification.KEY_QUANTITY_INPUT, 0);

        //MatchingHelper.attemptToCreateShoppingItem(name, quantity, unit);
        Log.e(TAG, "RECEIVED");

        Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(it);
    }
}