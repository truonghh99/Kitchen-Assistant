package com.example.kitchen_assistant.helpers;

import android.util.Log;

import com.example.kitchen_assistant.models.FoodItem;
import com.example.kitchen_assistant.models.Ingredient;
import com.example.kitchen_assistant.models.Product;
import com.example.kitchen_assistant.models.Recipe;
import com.example.kitchen_assistant.models.ShoppingItem;
import com.example.kitchen_assistant.storage.CurrentFoodTypes;
import com.example.kitchen_assistant.storage.CurrentProducts;
import com.example.kitchen_assistant.storage.CurrentRecipes;
import com.example.kitchen_assistant.storage.CurrentShoppingList;

import java.util.List;
import java.util.Map;

public class RecipeEvaluator {

    private static final String TAG = "RecipeEvaluator";

    public static boolean ingredientIsInCart(Ingredient ingredient) {
        String name = ingredient.getName();
        Float quantity = ingredient.getQuantity();
        String quantityUnit = ingredient.getQuantityUnit();
        if (!CurrentShoppingList.itemHashMap.containsKey(name)) {
            return false;
        } else {
            ShoppingItem item = CurrentShoppingList.itemHashMap.get(name);
            Float convertedQuantity = MetricConverter.convertGeneral(quantity, quantityUnit, item.getQuantityUnit());
            if (convertedQuantity > item.getQuantity()) {
                return false;
            }
        }
        return true;
    }

    public static void evaluateIngredient(Ingredient ingredient) {
        Log.e(TAG, "Evaluating " + ingredient.getName());
        ingredient.setAvailable(true);
        String name = ingredient.getName();
        Float quantity = ingredient.getQuantity();
        String quantityUnit = ingredient.getQuantityUnit();

        if (ingredient.getPreferredProduct() != null && CurrentProducts.containsProduct(ingredient.getPreferredProduct())) { // User has selected a preferred product before
            Product product = CurrentProducts.getProductWithCode(ingredient.getPreferredProduct());
            Float convertedQuantity = MetricConverter.convertGeneral(quantity, quantityUnit, product.getQuantityUnit());
            if (convertedQuantity > product.getCurrentQuantity()) {
                return; // Have enough preferred product, don't need to consider any other food item
            }
        }

        if (!CurrentFoodTypes.foodItems.containsKey(name)) {
            ingredient.setAvailable(false);
        } else {
            FoodItem foodItem = CurrentFoodTypes.foodItems.get(name);
            Float convertedQuantity = MetricConverter.convertGeneral(quantity, quantityUnit, foodItem.getQuantityUnit());
            if (convertedQuantity > foodItem.getQuantity()) {
                ingredient.setAvailable(false);
            } else {
                ingredient.setPreferredProduct(foodItem.getFirstProduct().getProductCode()); // Default: select first product of the matched type
                Log.e(TAG, "Set preferred product for " + ingredient.getName() + ": " + foodItem.getFirstProduct().getProductName());
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
        CurrentRecipes.saveRecipeInBackground(recipe);
    }

    public static void evaluateAllRecipe() {
        for (Map.Entry mapElement : CurrentRecipes.recipeHashMap.entrySet()) {
            Recipe recipe = (Recipe) mapElement.getValue();
            evaluateRecipe(recipe);
        }
    }
}
