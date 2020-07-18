package com.example.kitchen_assistant.models;

import android.os.Parcelable;
import android.util.Log;

import com.example.kitchen_assistant.helpers.MetricConverter;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

@ParseClassName("Ingredient")
public class Ingredient extends ParseObject implements Parcelable {

    public static final String TAG = "Ingredient";

    // Key for Parse
    public static final String KEY_NAME = "name";
    public static final String KEY_QUANTITY = "quantity";
    public static final String KEY_QUANTITY_UNIT = "quantityUnit";
    public static final String KEY_RECIPE = "recipe";
    private static final String KEY_AVAILABLE = "available";
    private static final String KEY_PREFERRED_PRODUCT = "preferredProduct";

    // Local values
    private String name;
    private float quantity;
    private String quantityUnit;
    private ParseObject recipe;
    private boolean available;
    private String preferredProduct;

    // Key for Spoonacular
    public static final String KEY_INGREDIENTS = "ingredients";
    public static final String KEY_INGREDIENT_NAME = "name";
    public static final String KEY_AMOUNT = "amount";
    public static final String KEY_METRIC = "metric";
    public static final String KEY_UNIT = "unit";
    public static final String KEY_VALUE = "value";


    public static HashMap<String, Ingredient> extractIngredientsFromJson(JSONObject jsonObject, Recipe recipe) throws JSONException {
        HashMap<String, Ingredient> result = new HashMap<>();
        JSONArray jsonArray = jsonObject.getJSONArray(KEY_INGREDIENTS);
        for (int i = 0; i < jsonArray.length(); ++i) {
            Ingredient ingredient = new Ingredient();
            JSONObject current = jsonArray.getJSONObject(i);
            ingredient.setName(current.getString(KEY_INGREDIENT_NAME));
            ingredient.setQuantity((float) current.getJSONObject(KEY_AMOUNT).getJSONObject(KEY_METRIC).getDouble(KEY_VALUE));
            ingredient.setQuantityUnit(current.getJSONObject(KEY_AMOUNT).getJSONObject(KEY_METRIC).getString(KEY_UNIT));
            ingredient.setRecipe(recipe);
            result.put(ingredient.getName(), ingredient);
        }
        return result;
    }

    public void fetchInfo() {
        name = getString(KEY_NAME);
        quantity = getNumber(KEY_QUANTITY).floatValue();
        quantityUnit = getString(KEY_QUANTITY_UNIT);
        recipe = getParseObject(KEY_RECIPE);
        available = getBoolean(KEY_AVAILABLE);
        preferredProduct = getString(KEY_PREFERRED_PRODUCT);
    }

    public void saveInfo() {
        put(KEY_NAME, name);
        put(KEY_QUANTITY, quantity);
        put(KEY_QUANTITY_UNIT, quantityUnit);
        put(KEY_RECIPE, recipe);
        put(KEY_AVAILABLE, available);
        if (preferredProduct != null) {
            put(KEY_PREFERRED_PRODUCT, preferredProduct);
        }
        saveInBackground();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public String getQuantityUnit() {
        return quantityUnit;
    }

    public void setQuantityUnit(String quantityUnit) {
        this.quantityUnit = quantityUnit;
    }

    public ParseObject getRecipe() {
        return recipe;
    }

    public void setRecipe (Recipe recipe) {
        this.recipe = recipe;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void increaseQuantity(Float currentQuantity, String quantityUnit) {
        Log.e(TAG, "Before adding: " + getQuantity());
        float toIncrease = MetricConverter.convertGeneral(currentQuantity, getQuantityUnit(), quantityUnit);
        setQuantity(getQuantity() + toIncrease);
        Log.e(TAG, "After adding: " + getQuantity());
    }

    public String getPreferredProduct() {
        return preferredProduct;
    }

    public void setPreferredProduct(String preferredProduct) {
        this.preferredProduct = preferredProduct;
    }
}
