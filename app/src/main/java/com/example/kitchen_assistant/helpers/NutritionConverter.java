package com.example.kitchen_assistant.helpers;

public class NutritionConverter {

    public static float caloriesFromCarbs(float carbs) {
        return carbs * 4;
    }

    public static float caloriesFromProtein(float protein) {
        return protein * 4;
    }

    public static float caloriesFromFat(float fat) {
        return fat * 9;
    }
}
