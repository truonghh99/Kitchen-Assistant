package com.example.kitchen_assistant.clients;

import android.util.Log;

import com.example.kitchen_assistant.activities.MainActivity;
import com.example.kitchen_assistant.models.FoodItem;
import com.example.kitchen_assistant.models.Product;
import com.example.kitchen_assistant.models.Recipe;
import com.parse.ParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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

    public static List<Recipe> getByIngredients(String ingredientsList) throws InterruptedException {
        Log.i(TAG, "Start querying recipes");
        recipes = new ArrayList<>();

        Log.e(TAG, "Ingredient list: " + ingredientsList);
        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse(GET_BY_INGREDIENTS_URL).newBuilder();
        urlBuilder.addQueryParameter("apiKey", API_KEY);
        urlBuilder.addQueryParameter("number", NUM_RESULT_EACH_QUERY);
        urlBuilder.addQueryParameter("ingredients", ingredientsList);

        String url = urlBuilder.build().toString();
        Request request = new Request.Builder()
                .url(url)
                .build();

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
                                recipes = Recipe.extractFromJsonArray(jsonArray);
                                Log.i(TAG, "Successfully extracted " + recipes.size() + " recipes");
                                MainActivity.hideProgressBar();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

        Thread.currentThread().sleep(500);

        return recipes;
    }

    // Generate ingredient list to fit Spoonacular's required format
    private static String generateList(List<FoodItem> foodItems) throws ParseException {
        if (foodItems.size() == 0) return "";
        String result = foodItems.get(0).getName();
        for (int i = 1; i < foodItems.size(); ++i) {
            result += ',' + foodItems.get(i).getName();
        }
        return result;
    }
}
