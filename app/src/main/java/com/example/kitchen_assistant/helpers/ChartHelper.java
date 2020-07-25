package com.example.kitchen_assistant.helpers;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class ChartHelper {

    public static void drawNutritionBarChart(float carbs, float protein, float fat, BarChart barChart) {
        BarEntry beCarbs = new BarEntry(carbs, 0);
        BarEntry beProtein = new BarEntry(protein, 1);
        BarEntry beFat = new BarEntry(fat, 2);

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

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(getNutritionXAxisFormatter());
    }

    public static IAxisValueFormatter getNutritionXAxisFormatter() {
        final ArrayList xAxis = new ArrayList<String>();
        xAxis.add("Carbs");
        xAxis.add("Protein");
        xAxis.add("Fat");

        IAxisValueFormatter formatter = new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return (String) xAxis.get((int) value);
            }
        };

        return formatter;
    }
}
