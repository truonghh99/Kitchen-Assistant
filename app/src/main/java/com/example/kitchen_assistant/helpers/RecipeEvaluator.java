package com.example.kitchen_assistant.helpers;

import com.example.kitchen_assistant.models.FoodItem;
import com.example.kitchen_assistant.models.Ingredient;
import com.example.kitchen_assistant.models.Recipe;
import com.example.kitchen_assistant.storage.CurrentFoodTypes;
import com.example.kitchen_assistant.storage.CurrentRecipes;

import java.util.List;
import java.util.Map;

public class RecipeEvaluator {

    public static void evaluateIngredient(Ingredient ingredient) {
        ingredient.setAvailable(true);
        String name = ingredient.getName();
        Float quantity = ingredient.getQuantity();
        String quantityUnit = ingredient.getQuantityUnit();
        if (!CurrentFoodTypes.foodItems.containsKey(name)) {
            ingredient.setAvailable(false);
        } else {
            FoodItem foodItem = CurrentFoodTypes.foodItems.get(name);
            Float convertedQuantity = MetricConverter.convertGeneral(quantity, quantityUnit, foodItem.getQuantityUnit());
            if (convertedQuantity > foodItem.getQuantity()) {
                ingredient.setAvailable(false);
            }
        }
    }

    public static void evaluateRecipe(Recipe recipe) {
        recipe.setCookable(true);
        List<Ingredient> ingredientList = recipe.getIngredientList();
        for (Ingredient ingredient : ingredientList) {
            evaluateIngredient(ingredient);
            if (!ingredient.isAvailable()) {
                recipe.setCookable(false);
            }
        }
    }

    public static void evaluateAllRecipe() {
        for (Map.Entry mapElement : CurrentRecipes.recipeHashMap.entrySet()) {
            Recipe recipe = (Recipe) mapElement.getValue();
            evaluateRecipe(recipe);
        }
    }
}
