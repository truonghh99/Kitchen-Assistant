package com.example.kitchen_assistant.helpers;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.example.kitchen_assistant.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class ChartHelper {

    private static final String TAG = "Chart Helper";

    public static void drawNutritionBarChart(float carbs, float protein, float fat, BarChart barChart, Context context) {
        BarEntry beCarbs = new BarEntry(1, carbs);
        BarEntry beProtein = new BarEntry(2, protein);
        BarEntry beFat = new BarEntry(3, fat);

        ArrayList carbSet = new ArrayList();
        carbSet.add(beCarbs);

        ArrayList proteinSet = new ArrayList();
        proteinSet.add(beProtein);

        ArrayList fatSet = new ArrayList();
        fatSet.add(beFat);

        BarDataSet carbDataSet = new BarDataSet(carbSet, "Carbs");
        carbDataSet.setColors(ContextCompat.getColor(context, R.color.carbs));

        BarDataSet proteinDataSet = new BarDataSet(proteinSet, "Protein");
        proteinDataSet.setColors(ContextCompat.getColor(context, R.color.protein));

        BarDataSet fatDataSet = new BarDataSet(fatSet, "Fat");
        fatDataSet.setColors(ContextCompat.getColor(context, R.color.fat));

        ArrayList dataSets = new ArrayList();
        dataSets.add(carbDataSet);
        dataSets.add(proteinDataSet);
        dataSets.add(fatDataSet);

        BarData data = new BarData(dataSets);
        data.setValueTextSize(11);

        barChart.setData(data);
        data.setBarWidth(0.75f); // set custom bar width

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(getNutritionXAxisFormatter());

        barChart.getDescription().setEnabled(false);
        barChart.setTouchEnabled(false);

        barChart.animateXY(300, 800);

        barChart.invalidate();
    }

    public static IAxisValueFormatter getNutritionXAxisFormatter() {
        final String[] nutrition = new String[]{"Carbs", "Protein", "Fat"};

        IAxisValueFormatter formatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                // "value" represents the position of the label on the axis (x or y)
                return "";
            }
        };

        return formatter;
    }

    public static void drawCaloriesPercentageChart(float calories, float total, PieChart pieChart, final Context context) {
        float caloriesPercentage = calories / total * 100;

        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(caloriesPercentage));
        entries.add(new PieEntry(100 - caloriesPercentage));

        PieDataSet set = new PieDataSet(entries, "Percentage of calories toward your daily goal (%)");
        set.setColors(new ArrayList<Integer>() {
            {
                add(ContextCompat.getColor(context, R.color.occupied));
                add(ContextCompat.getColor(context, R.color.left));
            }
        });
        PieData data = new PieData(set);
        data.setValueTextSize(11);
        data.setValueTextColor(ContextCompat.getColor(context, R.color.grey));

        pieChart.setHoleRadius(40);
        pieChart.animateX(500);
        pieChart.setData(data);
        pieChart.getDescription().setEnabled(false);
        pieChart.invalidate(); // refresh
    }
}
