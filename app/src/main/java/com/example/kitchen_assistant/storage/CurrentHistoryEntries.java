package com.example.kitchen_assistant.storage;

import android.util.Log;

import com.example.kitchen_assistant.activities.MainActivity;
import com.example.kitchen_assistant.fragments.shopping.ShoppingListFragment;
import com.example.kitchen_assistant.models.HistoryEntry;
import com.example.kitchen_assistant.models.ShoppingItem;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CurrentHistoryEntries {

    private static final String TAG = "CurrentHistoryEntries";
    public static List<HistoryEntry> entries;

    public static void addEntry(HistoryEntry entry) {
        saveEntryInBackGround(entry);
        entries.add(0, entry);
    }

    public static void saveEntryInBackGround(HistoryEntry entry) {
        Log.e(TAG, "Start saving shopping items");
        entry.saveInfo();
        entry.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving history entry", e);
                    return;
                }
                Log.e(TAG,"Successfully saved history entry");
            }
        });
    }

    public static void fetchEntriesInBackground() {
        Log.i(TAG, "Start querying for current history entries");
        MainActivity.showProgressBar();

        entries = new ArrayList<>();
        ParseQuery<HistoryEntry> query = ParseQuery.getQuery(HistoryEntry.class);
        query.addDescendingOrder("createdAt");

        query.findInBackground(new FindCallback<HistoryEntry>() {
            @Override
            public void done(List<HistoryEntry> newItems, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error when querying new history entries");
                    return;
                }
                initialize(newItems);
                ShoppingListFragment.notifyDataChange();
                Log.i(TAG, "Query completed, got " + entries.size() + " entries");
            }
        });
    }

    private static void initialize(List<HistoryEntry> newEntries) {
        for (HistoryEntry entry : newEntries) {
            entry.fetchInfo();
            entries.add(entry);
        }
        HistoryEntry.updateLatestEntry(entries.get(0));
    }

    public static void removeEntry(HistoryEntry entry) {
        entries.remove(entry);
        ParseObject productParse = ParseObject.createWithoutData("HistoryEntry", entry.getObjectId());
        productParse.deleteEventually();
    }
}
