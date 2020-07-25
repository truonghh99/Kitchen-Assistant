package com.example.kitchen_assistant.helpers;

import android.util.Log;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class ChartHelper {

    private static final String TAG = "Chart Helper";

    public static void drawNutritionBarChart(float carbs, float protein, float fat, BarChart barChart) {
        BarEntry beCarbs = new BarEntry(1, carbs);
        BarEntry beProtein = new BarEntry(2, protein);
        BarEntry beFat = new BarEntry(3, fat);

        ArrayList currentNutritionSet = new ArrayList();
        currentNutritionSet.add(beCarbs);
        currentNutritionSet.add(beProtein);
        currentNutritionSet.add(beFat);

        BarDataSet dataSetCurrent = new BarDataSet(currentNutritionSet, "Nutrition Consumed");
        dataSetCurrent.setColors(ColorTemplate.COLORFUL_COLORS);

        ArrayList dataSets = new ArrayList();
        dataSets.add(dataSetCurrent);

        BarData data = new BarData(dataSetCurrent);

        barChart.setData(data);
        data.setBarWidth(0.75f); // set custom bar width

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(getNutritionXAxisFormatter());

        Description description = new Description();
        description.setText("Nutrition in gram");
        barChart.setDescription(description);

        barChart.animateXY(500, 500);

        barChart.invalidate();
    }

    public static IAxisValueFormatter getNutritionXAxisFormatter() {
        final String[] nutrition = new String[] { "Carbs", "Protein", "Fat" };

        IAxisValueFormatter formatter = new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                // "value" represents the position of the label on the axis (x or y)
                if ((int) value == value) {
                    return (String) nutrition[(int) value - 1];
                }
                return "";
            }

        };

        return formatter;
    }
}
