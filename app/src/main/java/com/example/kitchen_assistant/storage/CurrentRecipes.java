package com.example.kitchen_assistant.storage;

import android.util.Log;

import com.example.kitchen_assistant.fragments.CurrentProductFragment;
import com.example.kitchen_assistant.fragments.RecipeFragment;
import com.example.kitchen_assistant.models.Product;
import com.example.kitchen_assistant.models.Recipe;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class CurrentRecipes {

    private static final String TAG = "CurrentRecipes";
    public static List<Recipe> recipes = new ArrayList<>();

    public static void addAllRecipes(List<Recipe> recipes) {
        for (Recipe recipe : recipes) {
            Log.e(TAG, recipe.getName());
            addRecipe(recipe);
        }
    }
    public static void addRecipe(Recipe recipe) {
        recipes.add(recipe);
        saveRecipeInBackGround(recipe);
        //RecipeFragment.notifyDataChange();
    }
    public static void saveAllRecipes() {
        Log.e(TAG, "Start saving all recipes");
        for (Recipe recipe : recipes) {
            saveRecipeInBackGround(recipe);
        }
    }

    public static void saveRecipeInBackGround(Recipe recipe) {
        recipe.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving recipe", e);
                    return;
                }
                Log.e(TAG,"Done");
            }
        });
    }

    public static void fetchRecipeInBackground() {
        Log.i(TAG, "Start querying for current products");

        recipes = new ArrayList<>();
        ParseQuery<Recipe> query = ParseQuery.getQuery(Recipe.class);
        query.addAscendingOrder("createdAt");

        query.findInBackground(new FindCallback<Recipe>() {
            @Override
            public void done(List<Recipe> newRecipe, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error when querying new posts");
                    return;
                }
                recipes.addAll(newRecipe);
                RecipeFragment.notifyDataChange();
                Log.i(TAG, "Query completed, got " + recipes.size() + " recipes");
            }
        });
    }

    public static void removeRecipe(Recipe recipe) {
        recipes.remove(recipe);
        ParseObject productParse = ParseObject.createWithoutData("Recipe", recipe.getObjectId());
        productParse.deleteEventually();
        RecipeFragment.notifyDataChange();
    }

}
