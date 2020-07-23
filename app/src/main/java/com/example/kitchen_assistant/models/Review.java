package com.example.kitchen_assistant.models;

import android.os.Parcelable;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

@ParseClassName("Review")
public class Review extends ParseObject implements Parcelable {

    private static final String TAG = "ReviewModel";

    // Keys for Parse
    public static final String KEY_RECIPE_ID = "recipeId";
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_REVIEW_CONTENT = "reviewContent";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_RATING = "rating";

    // Local values
    private String recipeId;
    private String userId;
    private String reviewContent;
    private ParseFile image;
    private String imageUrl;
    private float rating;

    public void saveInfo() {
        put(KEY_RECIPE_ID, recipeId);
        put(KEY_USER_ID, userId);
        put(KEY_REVIEW_CONTENT, reviewContent);
        put(KEY_IMAGE, image);
        put(KEY_RATING, rating);
        saveInBackground();
    }

    public void fetchInfo() {
        recipeId = getString(KEY_RECIPE_ID);
        userId = getString(KEY_USER_ID);
        reviewContent = getString(reviewContent);
        image = getParseFile(KEY_IMAGE);
        if (image != null) {
            imageUrl = image.getUrl();
        }
        rating = getNumber(KEY_RATING).floatValue();
    }

    public String getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getReviewContent() {
        return reviewContent;
    }

    public void setReviewContent(String reviewContent) {
        this.reviewContent = reviewContent;
    }

    public ParseFile getImage() {
        return image;
    }

    public void setImage(ParseFile image) {
        this.image = image;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

}