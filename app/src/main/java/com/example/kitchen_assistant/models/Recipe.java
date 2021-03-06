package com.example.kitchen_assistant.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcelable;
import android.util.Log;

import com.example.kitchen_assistant.clients.Spoonacular;
import com.example.kitchen_assistant.storage.CurrentRecipes;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@ParseClassName("Recipe")
public class Recipe extends ParseObject implements Parcelable {

    public static final String TAG = "RecipeModel";
    public static final String MANUALLY_INSERT_KEY = "Manually Insert";
    private static final String DEFAULT_INSTRUCTIONS = "Sorry, there's no instructions available";

    // Keyword for Parse columns
    public static final String KEY_ID = "objectId";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_NAME = "recipeName";
    public static final String KEY_INSTRUCTIONS = "instructions";
    public static final String KEY_CODE = "recipeCode";
    public static final String KEY_COOKABLE = "cookable";
    public static final String KEY_USER_ID = "userId";


    // Keyword for Spoonacular
    private static final String KEY_NAME_JSON_API = "title";
    private static final String KEY_INGREDIENTS_JSON_API = "missedIngredients";
    private static final String KEY_IMAGE_JSON_API = "image";
    private static final String KEY_ID_API = "id";
    private static final String KEY_IMG = "image";

    // Local properties
    private String name;
    private String recipeCode;
    private String userId;
    private String imageUrl;
    private String instructions;
    private boolean cookable;
    private ParseFile parseFile;
    private HashMap<String, Ingredient> ingredients = new HashMap<>();
    private Rating rating;
    private Nutrition nutrition;

    public static Recipe extractFromJsonObject(JSONObject json) throws JSONException {
        Recipe result = new Recipe();
        result.setName(json.getString(KEY_NAME_JSON_API));
        result.setImageUrl(json.getString(KEY_IMAGE_JSON_API));
        try {
            result.saveImageFromUrl(result.getImageUrl());
        } catch (IOException e) {
            e.printStackTrace();
        }
        result.setCode(json.getString(KEY_ID_API));
        result.setInstructions("no instructions");
        Spoonacular.getIngredients(result);
        try {
            Rating.requestRating(result);
            Nutrition.requestNutrition(result);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static List<Recipe> extractFromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Recipe> recipes = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); ++i) {
            JSONObject json = jsonArray.getJSONObject(i);
            Recipe recipe = extractFromJsonObject(json);
            recipes.add(recipe);
        }
        return recipes;
    }

    public void fetchInfo() {
        name = getString(KEY_NAME);
        parseFile = getParseFile(KEY_IMG);
        if (parseFile != null) {
            imageUrl = parseFile.getUrl();
        }
        instructions = getString(KEY_INSTRUCTIONS);
        recipeCode = getString(KEY_CODE);
        cookable = getBoolean(KEY_COOKABLE);
        userId = getString(KEY_USER_ID);
        try {
            Rating.requestRating(this);
            Nutrition.requestNutrition(this);
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e(TAG, "CANNOT REQUEST RATING OR NUTRITION INFO");
        }
    }

    public void saveInfo() {
        put(KEY_NAME, name);
        if (parseFile != null) put(KEY_IMG, parseFile);
        if (instructions != null) {
            put(KEY_INSTRUCTIONS, instructions);
        } else {
            put(KEY_INSTRUCTIONS, DEFAULT_INSTRUCTIONS);
        }
        put(KEY_CODE, recipeCode);
        Log.e(TAG, "New code: " + recipeCode);
        put(KEY_COOKABLE, cookable);
        put(KEY_USER_ID, ParseUser.getCurrentUser().getObjectId());
        for (Ingredient ingredient : getIngredientList()) {
            ingredient.saveInfo();
        }
    }

    public void saveImageFromUrl(String url) throws IOException {
        try {
            java.net.URL img_value = new java.net.URL(url);
            Bitmap mIcon = BitmapFactory
                    .decodeStream(img_value.openConnection()
                            .getInputStream());
            if (mIcon != null) {
                byte[] imgByteArray = encodeToByteArray(mIcon);
                this.parseFile = new ParseFile(imgByteArray);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] encodeToByteArray(Bitmap image) {
        Bitmap b = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imgByteArray = baos.toByteArray();

        return imgByteArray ;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imgUrl) {
        this.imageUrl = imgUrl;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getCode() {
        return recipeCode;
    }

    public void setCode(String code) {
        this.recipeCode = code;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public Float getNumericRating() {
        return rating.getRating();
    }
    public void setIngredients(HashMap<String, Ingredient> ingredientHashMap) {
        ingredients = ingredientHashMap;
    }

    public List<Ingredient> getIngredientList() {
        Log.e(TAG, ingredients.toString());
        return new ArrayList<>(ingredients.values());
    }

    public boolean isCookable() {
        return cookable;
    }

    public void setCookable(boolean cookable) {
        this.cookable = cookable;
    }

    public void addReview(String reviewContent, String title, Float rating) {
        Review review = new Review();
        review.setTitle(title);
        review.setRating(rating);
        review.setReviewContent(reviewContent);
        review.setRecipeId(recipeCode);
        review.setUserId(ParseUser.getCurrentUser().getObjectId());
        review.saveInfo();
        this.rating.addRating(rating);
    }

    public void addReview(Review review) {
        review.setRecipeId(recipeCode);
        review.setUserId(ParseUser.getCurrentUser().getObjectId());
        review.saveInfo();
        this.rating.addRating(review.getRating());
    }

    public List<Review> getReviews() {
        List<Review> result = new ArrayList<>();
        ParseQuery<Review> query = ParseQuery.getQuery("Review");
        query.whereEqualTo(Review.KEY_RECIPE_ID, recipeCode);
        query.addDescendingOrder("createdAt");
        try {
            result = query.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void setParseFile(ParseFile parseFile) {
        Log.e(TAG, "Setting Parse File");
        this.parseFile = parseFile;
        put(KEY_IMG, parseFile);
        try {
            Log.e(TAG, "Saving to Parse");
            save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        setImageUrl(parseFile.getUrl());
    }

    public ParseFile getParseFile() {
        return parseFile;
    }

    public void setNutrition(Nutrition nutrition) {
        this.nutrition = nutrition;
        nutrition.saveInfo();
    }

    public Nutrition getNutrition() {
        return nutrition;
    }
}
