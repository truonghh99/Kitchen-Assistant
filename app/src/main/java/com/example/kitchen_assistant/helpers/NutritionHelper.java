package com.example.kitchen_assistant.helpers;

import com.example.kitchen_assistant.models.HistoryEntry;
import com.example.kitchen_assistant.storage.CurrentHistoryEntries;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class NutritionHelper {

    public static float caloriesFromCarbs(float carbs) {
        return carbs * 4;
    }

    public static float caloriesFromProtein(float protein) {
        return protein * 4;
    }

    public static float caloriesFromFat(float fat) {
        return fat * 9;
    }

    public static HashMap<String, Float> getNutritionInfoInDuration(Date startDate, Date endDate) {
        HistoryEntry firstEntry = CurrentHistoryEntries.getFirstWithLowerBound(startDate);
        HistoryEntry lastEntry = CurrentHistoryEntries.getLastWithUpperBound(endDate);

        final float calories = lastEntry.getCumulativeCalories() - firstEntry.getCumulativeCalories();
        final  float protein = lastEntry.getCumulativeProtein() - firstEntry.getCumulativeProtein();
        final float carbs = lastEntry.getCumulativeCarbs() - firstEntry.getCumulativeCarbs();
        final float fat = lastEntry.getCumulativeFat() - firstEntry.getCumulativeFat();

        HashMap<String, Float> nutrition = new HashMap<String, Float>() {
            {
                put("calories", calories);
                put("protein", protein);
                put("carbs", carbs);
                put("fat", fat);
            }
        };

        return nutrition;
    }

    public static List<HashMap<String, Float>> getAllDailyNutritionInfo() {
        List<HashMap<String, Float>> result = new ArrayList<>();

        Date startDate = TimeConverter.getFirstOfDate(CurrentHistoryEntries.getFirstDate());
        Date endDate = TimeConverter.getLastOfDate(CurrentHistoryEntries.getFirstDate());

        while (startDate.before(CurrentHistoryEntries.getLastDate())) {
            HashMap<String, Float> dailyNutrition = getNutritionInfoInDuration(startDate, endDate);
            result.add(dailyNutrition);
            startDate = TimeConverter.addOneDay(startDate);
            endDate = TimeConverter.addOneDay(endDate);
        }

        return result;
    }
}
