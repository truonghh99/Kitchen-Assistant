package com.example.kitchen_assistant.models;

import android.os.Parcelable;
import android.util.Log;

import com.example.kitchen_assistant.helpers.MetricConverter;
import com.parse.CountCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("Rating")
public class Rating extends ParseObject implements Parcelable {

    private static final String TAG = "RatingModel";

    // Keys for Parse
    public static final String KEY_RECIPE_ID = "recipeId";
    public static final String KEY_RATING = "rating";

    // Local values
    private String recipeId;
    private Float rating;
    private long numReviews;

    public void fetchInfo() {
        recipeId = getString(KEY_RECIPE_ID);
        rating = getNumber(KEY_RATING).floatValue();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Review");
        query.whereEqualTo(KEY_RECIPE_ID, recipeId);
        query.countInBackground(new CountCallback() {
            public void done(int count, ParseException e) {
                if (e == null) {
                    numReviews = count;
                } else {
                    Log.e(TAG, "Cannot count number of reviews for recipe " + recipeId);
                }
            }
        });
    }

    public void saveInfo() {
        put(KEY_RECIPE_ID, recipeId);
        put(KEY_RATING, rating);
        saveInBackground();
    }

    public String getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public long getNumReviews() {
        return numReviews;
    }

    public void setNumReviews(long numReviews) {
        this.numReviews = numReviews;
    }

    public void addRating(float newRating) {
        float totalRating = numReviews * getRating();
        ++numReviews;
        totalRating += newRating;
        setRating(totalRating / numReviews);
        saveInfo();
    }
}
