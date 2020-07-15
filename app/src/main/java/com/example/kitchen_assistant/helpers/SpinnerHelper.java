package com.example.kitchen_assistant.helpers;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.example.kitchen_assistant.databinding.FragmentNewProductDetailBinding;
import com.example.kitchen_assistant.models.Product;

public class SpinnerHelper {

    private static final String TAG = "SpinnerHelper";
    private static String newText;
    private static final String DECIMAL_FORMAT = "##.##";

    // Set up spinners to deal with metric data (including metric conversion & modifying attached spinner. Eg: all the quantity attributes
    // belong to the same object should always be converted to the same unit)
    public static void setUpMetricSpinner(Spinner spinner, final String selectedItem, Context context, final EditText editText, final Float currentVal, final Spinner attachedSpinner) {
        List<String> categories = new ArrayList<String>();
        if (editText != null) newText = editText.getText().toString();

        // Identify which metric category this spinner belongs to
        if (MetricConversionHelper.weight.containsKey(selectedItem)) {
            categories = MetricConversionHelper.weightCategories;
        }
        if (MetricConversionHelper.volumne.containsKey(selectedItem)) {
            categories = MetricConversionHelper.volumneCategories;
        }
        if (MetricConversionHelper.time.containsKey(selectedItem)) {
            categories = MetricConversionHelper.timeCategories;
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, categories);
        spinner.setAdapter(dataAdapter);
        spinner.setSelection(categories.indexOf(selectedItem));

        if (editText != null) {
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    String newUnit = (String) parentView.getItemAtPosition(position);
                    DecimalFormat df = new DecimalFormat(DECIMAL_FORMAT);
                    df.setRoundingMode(RoundingMode.DOWN);
                    Float convertedValue = MetricConversionHelper.convertGeneral(currentVal, selectedItem, newUnit);
                    newText = String.valueOf(df.format(convertedValue));
                    editText.setText(newText);
                    if (attachedSpinner != null) attachedSpinner.setSelection(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                }

            });
        }
    }

    // Status spinner includes only 3 values and should not affect any other spinner / text view
    public static void setUpStatusSpinner(Spinner spinner, final String selectedItem, Context context) {
        List<String> categories = new ArrayList<String>(){
            {
                add(Product.STATUS_BEST);
                add(Product.STATUS_SAFE);
                add(Product.STATUS_BAD);
            }
        };
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, categories);
        spinner.setAdapter(dataAdapter);
        spinner.setSelection(categories.indexOf(selectedItem));
    }

}
