package com.example.kitchen_assistant.clients;

import android.util.Log;

import com.example.kitchen_assistant.models.FoodItem;
import com.example.kitchen_assistant.models.Product;
import com.example.kitchen_assistant.models.Recipe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Spoonacular {

    private static final String TAG = "Spoonacular";
    private static final String API_KEY = "27c4cc71068649faa0fcf7188f37cc93";
    public static String HEADER = "KitchenAssistant - Android - Version 1.0 - https://github.com/truonghh99/Kitchen-Assistant/blob/master/README.md";
    private static final String GET_BY_INGREDIENTS_URL = "https://api.spoonacular.com/recipes/findByIngredients?";
    private static String NUM_RESULT_EACH_QUERY = "10";

    private static List<Recipe> recipes;

    public static List<Recipe> getByIngredients(List<FoodItem> foodItems) throws InterruptedException {
        Log.e(TAG, "Start querying recipes");
        recipes = new ArrayList<>();

        //String ingredientsList = generateList(foodItems);
        String ingredientsList = "apples,flour,sugar";
        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse(GET_BY_INGREDIENTS_URL).newBuilder();
        urlBuilder.addQueryParameter("apiKey", API_KEY);
        urlBuilder.addQueryParameter("number", NUM_RESULT_EACH_QUERY);
        urlBuilder.addQueryParameter("ingredients", ingredientsList);

        String url = urlBuilder.build().toString();
        Request request = new Request.Builder()
                .url(url)
                .build();

        Log.e(TAG, url);

        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            throw new IOException("Unexpected code " + response);
                        } else {
                            String jsonData = response.body().string();
                            Log.e(TAG, jsonData);
                            try {
                                JSONArray jsonArray = new JSONArray(jsonData);
                                //product = new Product(jsonObject);
                                //Log.e(TAG, "Created new product object");
                                recipes = Recipe.extractFromJsonArray(jsonArray);
                                Log.e(TAG, "Successfully extracted " + recipes.size() + " recipes");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

        while (recipes.size() == 0) {
            Thread.currentThread().sleep(10);
        }
        Log.e(TAG, "GOT " + recipes.size() + " recipes");
        return recipes;
    }

    private static String generateList(List<FoodItem> foodItems) {
        if (foodItems.size() == 0) return "";
        String result = foodItems.get(0).getName();
        for (FoodItem food : foodItems) {
            result += ',' + food.getName();
        }
        return result;
    }
}
