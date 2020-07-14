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

    public static void setUpMetricSpinner(Spinner spinner, final String selectedItem, Context context, final EditText editText, final Float currentVal, final Spinner attachedSpinner) {
        List<String> categories = new ArrayList<String>();
        if (editText != null) newText = editText.getText().toString();
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
            Log.e(TAG, editText.getText().toString());
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    String newUnit = (String) parentView.getItemAtPosition(position);
                    DecimalFormat df = new DecimalFormat("##.##");
                    df.setRoundingMode(RoundingMode.DOWN);
                    Float convertedValue = convertGeneral(currentVal, selectedItem, newUnit);
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

    private static float convertGeneral(float currentVal, String selectedItem, String newUnit) {
        float result = 0;

        if (MetricConversionHelper.volumne.containsKey(selectedItem)) {
            result = MetricConversionHelper.convertVolume(currentVal, selectedItem, newUnit);
            return result;
        }

        if (MetricConversionHelper.weight.containsKey(selectedItem)) {
            result = MetricConversionHelper.convertWeight(currentVal, selectedItem, newUnit);
            return result;
        }

        if (MetricConversionHelper.time.containsKey(selectedItem)) {
            result = MetricConversionHelper.convertTime(currentVal, selectedItem, newUnit);
        }
        return result;
    }
}
