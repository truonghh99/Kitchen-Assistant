package com.example.kitchen_assistant.helpers;

import android.content.Context;

import androidx.core.content.ContextCompat;

import com.example.kitchen_assistant.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChartHelper {

    private static final float VALUE_TEXT_SIZE = 11;

    // Bar charts
    private static final float BAR_WIDTH = 0.75f;
    private static final int BAR_ANIMATION_X = 300;
    private static final int BAR_ANIMATION_Y = 800;

    // Pie charts
    private static final float HOLE_RADIUS = 50;
    private static final float TRANSPARENT_RADIUS = 55;
    private static final int PIE_ANIMATION = 500;

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
        data.setValueTextSize(VALUE_TEXT_SIZE);

        barChart.setData(data);
        data.setBarWidth(BAR_WIDTH); // set custom bar width

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(getNutritionXAxisFormatter());

        barChart.getDescription().setEnabled(false);
        barChart.setTouchEnabled(false);

        barChart.animateXY(BAR_ANIMATION_X, BAR_ANIMATION_Y);

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

    public static void drawCaloriesByNutritionChart(float calories, float carbs, float protein, float fat, float total, PieChart pieChart, final Context context) {
        float proteinPercentage = NutritionHelper.caloriesFromProtein(protein) / total * 100;
        float carbsPercentage = NutritionHelper.caloriesFromCarbs(carbs) / total * 100;
        float fatPercentage = NutritionHelper.caloriesFromFat(fat) / total * 100;

        List<PieEntry> entries = new ArrayList<>();
        PieDataSet set = new PieDataSet(entries, "Percentage of calories toward your daily goal (%)");

        if (calories <= total) {
            entries.add(new PieEntry(carbsPercentage));
            entries.add(new PieEntry(proteinPercentage));
            entries.add(new PieEntry(fatPercentage));
            entries.add(new PieEntry(100 - carbsPercentage - proteinPercentage - fatPercentage));
            set.setColors(new ArrayList<Integer>() {
                {
                    add(ContextCompat.getColor(context, R.color.carbs));
                    add(ContextCompat.getColor(context, R.color.protein));
                    add(ContextCompat.getColor(context, R.color.fat));
                    add(ContextCompat.getColor(context, R.color.left));
                }
            });
        } else {
            entries.add(new PieEntry(100));
            set.setColor(ContextCompat.getColor(context, R.color.occupied));
        }

        PieData data = new PieData(set);
        data.setDrawValues(false);
        data.setValueTextSize(VALUE_TEXT_SIZE);
        data.setValueTextColor(ContextCompat.getColor(context, R.color.grey));

        pieChart.setHoleRadius(HOLE_RADIUS);
        pieChart.setCenterText((int) calories + " / " + (int) total + " kcal");

        pieChart.animateX(PIE_ANIMATION);
        pieChart.setTransparentCircleRadius(TRANSPARENT_RADIUS);
        pieChart.getLegend().setEnabled(false);
        pieChart.setData(data);
        pieChart.getDescription().setEnabled(false);
        pieChart.invalidate(); // refresh
    }

    public static void drawCaloriesLineChart(List<HashMap<String, Float>> nutritionEntries, Float goal, LineChart lineChart, final Context context) {
        ArrayList<Entry> values = new ArrayList<>();
        int i = 1;
        for (HashMap<String, Float> entry : nutritionEntries) {
            values.add(new Entry(i, entry.get("calories")));
            ++i;
        }
        LineDataSet dataSet = new LineDataSet(values, "Daily Calories Intake");
        dataSet.setLineWidth(2);
        dataSet.setColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));

        LineData data = new LineData(dataSet);
        data.setValueTextSize(VALUE_TEXT_SIZE);
        data.setValueTextColor(ContextCompat.getColor(context, R.color.grey));

        lineChart.getXAxis().setValueFormatter(getNutritionXAxisFormatter());
        lineChart.setData(data);
        lineChart.getDescription().setEnabled(false);
        lineChart.setTouchEnabled(false);
        lineChart.invalidate();
    }
}
