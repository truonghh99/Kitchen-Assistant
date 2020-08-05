package com.example.kitchen_assistant.helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.Fragment;

import com.example.kitchen_assistant.activities.MainActivity;
import com.example.kitchen_assistant.fragments.shopping.ShoppingListFragment;
import com.example.kitchen_assistant.storage.CurrentShoppingList;

public class ActionReceiver extends BroadcastReceiver {

    private final String TAG = "ActionReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "RECEIVED");
        Bundle extras = intent.getExtras();

        String name = extras.getString(Notification.KEY_PRODUCT_NAME);
        String unit = extras.getString(Notification.KEY_UNIT);
        Float quantity = extras.getFloat(Notification.KEY_QUANTITY_INPUT, 0);

        if (name != null) {
            MatchingHelper.attemptToCreateShoppingItem(name, quantity, unit);
            Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            context.sendBroadcast(it);
            goToShoppingList();
            Notification.notificationManager.cancel(0);
        } else {
            return;
        }
    }

    private void goToShoppingList() {
        Fragment fragment = ShoppingListFragment.newInstance();
        MainActivity.switchFragment(fragment);
    }
}