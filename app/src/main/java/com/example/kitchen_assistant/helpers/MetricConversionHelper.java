package com.example.kitchen_assistant.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MetricConversionHelper {
    public static final HashMap<String, Float> volumne = new HashMap<String, Float>(){
        {
            put("oz", (float) 1);
            put("ml", (float) 0.038);
            put("l", (float) 33.8);
            put("cup", (float) 8);
            put("gallon", (float) 128);
            put("tbsp", (float) 0.5);
            put("tsp", (float) 0.167);
        }
    };

    public static final HashMap<String, Float> weight = new HashMap<String, Float>(){
        {
            put("oz", (float) 1);
            put("lbs", (float) 16);
            put("g", (float) 0.035);
            put("kg", (float) 35.27);
            put("cup", (float) 8);
            put("tbsp", (float) 0.5);
            put("tsp", (float) 0.167);
        }
    };

    public static final HashMap<String, Float> time = new HashMap<String, Float>(){
        {
            put("day", (float) 1);
            put("month", (float) 30);
            put("year", (float) 365);
        }
    };

    public static List<String> volumneCategories = new ArrayList<String>(){
        {
            add("oz");
            add("ml");
            add("l");
            add("cup");
            add("gallon");
            add("tbsp");
            add("tsp");
        }
    };

    public static List<String> weightCategories = new ArrayList<String>(){
        {
            add("oz");
            add("lbs");
            add("g");
            add("kg");
            add("cup");
            add("tbsp");
            add("tsp");
        }
    };

    public static List<String> timeCategories = new ArrayList<String>(){
        {
            add("day");
            add("month");
            add("year");
        }
    };

    public static float convertVolume(float quantity, String unitFrom, String unitTo) {
        float result = 0;
        result = quantity * volumne.get(unitFrom)/volumne.get(unitTo);
        return result;
    }

    public static float convertWeight(float quantity, String unitFrom, String unitTo) {
        float result = 0;
        result = quantity * weight.get(unitFrom)/weight.get(unitTo);
        return result;
    }

    public static float convertTime(float quantity, String unitFrom, String unitTo) {
        float result = 0;
        if (unitFrom == "year" && unitTo == "month") {
            return quantity * 12;
        }
        if (unitTo == "month" && unitTo == "year") {
            return quantity / 12;
        }
        result = quantity * time.get(unitFrom)/time.get(unitTo);
        return result;
    }
}
