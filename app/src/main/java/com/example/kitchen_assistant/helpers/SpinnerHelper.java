package com.example.kitchen_assistant.helpers;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SpinnerHelper {

    public static void setUpSpinner(Spinner spinner, String selectedItem, Context context) {
        List<String> categories = new ArrayList<String>();
        categories.add(selectedItem);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, categories);

        spinner.setAdapter(dataAdapter);
    }
}
