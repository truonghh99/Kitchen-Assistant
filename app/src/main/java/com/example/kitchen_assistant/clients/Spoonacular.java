package com.example.kitchen_assistant.clients;

import android.util.Log;

import com.example.kitchen_assistant.activities.MainActivity;
import com.example.kitchen_assistant.models.FoodItem;
import com.example.kitchen_assistant.models.Ingredient;
import com.example.kitchen_assistant.models.Product;
import com.example.kitchen_assistant.models.Recipe;
import com.parse.ParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
    private static final String GET_INSTRUCTION_URL = "https://api.spoonacular.com/recipes/{id}/analyzedInstructions";
    private static final String GET_INGREDIENTS_URL = "https://api.spoonacular.com/recipes/{id}/ingredientWidget.json";

    private static String NUM_RESULT_EACH_QUERY = "10";

    private static List<Recipe> recipes;

    // Get 10 recipes that contains the given ingredient list
    public static List<Recipe> getByIngredients(String ingredientsList) throws InterruptedException {
        Log.i(TAG, "Start querying recipes");
        recipes = new ArrayList<>();

        // Initialize network client
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(GET_BY_INGREDIENTS_URL).newBuilder();
        urlBuilder.addQueryParameter("apiKey", API_KEY);
        urlBuilder.addQueryParameter("number", NUM_RESULT_EACH_QUERY);
        urlBuilder.addQueryParameter("ingredients", ingredientsList);
        String url = urlBuilder.build().toString();
        Request request = new Request.Builder()
                .url(url)
                .build();

        // Request recipes from API
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

        // Wait for async request to complete
        while (recipes.size() == 0) {
            Thread.currentThread().sleep(10);
        }
        return recipes;
    }

    // Get instruction of given recipe
    public static String getInstruction(String recipeId) throws InterruptedException {
        Log.i(TAG, "Start querying instruction for recipe " + recipeId);
        final String[] instruction = {null};

        // Initialize network client
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(GET_INSTRUCTION_URL.replace("{id}", recipeId)).newBuilder();
        urlBuilder.addQueryParameter("apiKey", API_KEY);
        String url = urlBuilder.build().toString();
        Request request = new Request.Builder()
                .url(url)
                .build();

        // Request instruction from API
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
                            try {
                                JSONArray jsonArray = new JSONArray(jsonData);
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                JSONArray steps = jsonObject.getJSONArray("steps");
                                instruction[0] = convertInstruction(steps);
                                Log.e(TAG, steps.toString());
                            } catch (JSONException e) {
                                instruction[0] = "We're sorry, but there's no instruction available right now";
                                return;
                            }
                        }
                    }
                });

        // Wait for async request to complete
        while (instruction[0] == null) {
            Thread.currentThread().sleep(10);
        }
        return instruction[0];
    }

    // Helper: convert returned instruction to displayable format
    private static String convertInstruction(JSONArray steps) throws JSONException {
        String instruction = "";
        for (int i = 0; i < steps.length(); ++i) {
            String currentStep = steps.getJSONObject(i).getString("step");
            int index = i + 1;
            instruction += "<b>- Step " + index + ":</b> " + currentStep + "<br><br>";
        }
        Log.e(TAG, instruction);
        return instruction;
    }

    // Generate ingredient list to fit Spoonacular's required format
    public static String generateList(List<FoodItem> foodItems) throws ParseException {
        if (foodItems.size() == 0) return "";
        String result = foodItems.get(0).getName();
        for (int i = 1; i < foodItems.size(); ++i) {
            result += ',' + foodItems.get(i).getName();
        }
        return result;
    }

    // Get ingredient of given recipe. Result is attached directly to recipe object
    public static void getIngredients(final Recipe recipe) {
        Log.i(TAG, "Start querying ingredient for recipe " + recipe.getCode());

        // Initialize network request
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(GET_INGREDIENTS_URL.replace("{id}", recipe.getCode())).newBuilder();
        urlBuilder.addQueryParameter("apiKey", API_KEY);
        String url = urlBuilder.build().toString();
        Log.e(TAG, url);
        Request request = new Request.Builder()
                .url(url)
                .build();

        // Get ingredients from the API
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
                            try {
                                JSONObject jsonObject = new JSONObject(jsonData);
                                recipe.setIngredients(Ingredient.extractIngredientsFromJson(jsonObject, recipe));
                            } catch (JSONException e) {
                            }
                        }
                    }
                });
    }
}
