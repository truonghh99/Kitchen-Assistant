package com.example.kitchen_assistant.storage;

import android.util.Log;

import com.example.kitchen_assistant.activities.MainActivity;
import com.example.kitchen_assistant.fragments.products.CurrentFoodFragment;
import com.example.kitchen_assistant.fragments.recipes.RecipeFragment;
import com.example.kitchen_assistant.models.Ingredient;
import com.example.kitchen_assistant.models.Recipe;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CurrentRecipes {

    private static final String TAG = "CurrentRecipes";
    public static List<Recipe> recipes;
    private static HashMap<String, Recipe> recipeHashMap;

    public static void addRecipe(Recipe recipe) {
        Log.e(TAG, "ADDED RECIPE " + recipe.getCode());
        recipes.add(0, recipe);
        RecipeFragment.notifyDataChange();
        recipeHashMap.put(recipe.getCode(), recipe);

        recipe.saveInfo();
        saveRecipeInBackground(recipe);
    }

    public static void saveRecipeInBackground(Recipe recipe) {
        recipe.saveInfo();
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
        Log.i(TAG, "Start querying for current recipes");
        MainActivity.showProgressBar();

        recipes = new ArrayList<>();
        recipeHashMap = new HashMap<>();
        ParseQuery<Recipe> query = ParseQuery.getQuery(Recipe.class);
        query.whereEqualTo(Recipe.KEY_USER_ID, ParseUser.getCurrentUser().getObjectId());
        query.addDescendingOrder(Recipe.KEY_COOKABLE);
        query.findInBackground(new FindCallback<Recipe>() {
            @Override
            public void done(List<Recipe> newRecipes, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error when querying new recipes");
                    return;
                }
                initialize(newRecipes);
                CurrentFoodFragment.notifyDataChange();
                Log.i(TAG, "Query completed, got " + recipes.size() + " recipes");
                MainActivity.hideProgressBar();
            }
        });
    }

    private static void initialize(List<Recipe> newRecipes) {
        for (Recipe recipe : newRecipes) {
            recipe.fetchInfo();
            Log.e(TAG, "Find ingredient of: " + recipe.getName());
            recipes.add(recipe);
            recipeHashMap.put(recipe.getCode(), recipe);
            Log.e(TAG, "Find rating of: " + recipe.getName());
            queryIngredients(recipe);
        }
    }

    private static void queryIngredients(final Recipe recipe) {
        ParseQuery<Ingredient> query = ParseQuery.getQuery(Ingredient.class);
        query.whereContains(Ingredient.KEY_RECIPE, recipe.getObjectId());
        query.findInBackground(new FindCallback<Ingredient>() {
            @Override
            public void done(List<Ingredient> ingredients, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error when querying ingredients");
                    return;
                }
                HashMap<String, Ingredient> ingredientHashMap = generateIngredientHashmap(ingredients, recipe);
                recipe.setIngredients(ingredientHashMap);
                Log.i(TAG, "Query completed, got " + ingredients.size() + " ingredients");
                MainActivity.hideProgressBar();
            }
        });
    }

    private static HashMap<String, Ingredient> generateIngredientHashmap(List<Ingredient> ingredients, Recipe recipe) {
        HashMap<String, Ingredient> result = new HashMap<>();
        for (Ingredient ingredient : ingredients) {
            ingredient.fetchInfo();
            ingredient.setRecipe(recipe);
            result.put(ingredient.getName(), ingredient);
        }
        return result;
    }

    public static void removeRecipe(Recipe recipe) {
        recipes.remove(recipe);
        ParseObject recipeParse = ParseObject.createWithoutData("Recipe", recipe.getObjectId());
        recipeParse.deleteEventually();
        RecipeFragment.notifyDataChange();
    }

    public static void addAllRecipes(List<Recipe> newRecipes) {
        for (Recipe recipe : newRecipes) {
            addRecipe(recipe);
        }
    }

    public static boolean containsRecipe(Recipe recipe) {
        return recipeHashMap.containsKey(recipe.getCode());
    }

    public static HashMap<String, Recipe> getRecipeHashmap() {
        return recipeHashMap;
    }

    public static int getCurrentNumRecipes() {
        return recipes.size();
    }
}
