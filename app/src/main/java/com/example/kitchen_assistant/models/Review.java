package com.example.kitchen_assistant.models;

import android.os.Parcelable;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

@ParseClassName("Review")
public class Review extends ParseObject implements Parcelable {

    public static final String TAG = "ReviewModel";

    // Keys for Parse
    public static final String KEY_RECIPE_ID = "recipeId";
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_REVIEW_CONTENT = "reviewContent";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_RATING = "rating";
    public static final String KEY_TITLE = "title";

    // Local values
    private String recipeId;
    private String userId;
    private String reviewContent;
    private ParseFile parseFile;
    private String imageUrl;
    private String title;
    private float rating;

    public void saveInfo() {
        put(KEY_RECIPE_ID, recipeId);
        put(KEY_USER_ID, userId);
        put(KEY_REVIEW_CONTENT, reviewContent);
        if (parseFile != null) put(KEY_IMAGE, parseFile);
        put(KEY_RATING, rating);
        put(KEY_TITLE, title);
        saveInBackground();
    }

    public void fetchInfo() {
        recipeId = getString(KEY_RECIPE_ID);
        userId = getString(KEY_USER_ID);
        reviewContent = getString(KEY_REVIEW_CONTENT);
        parseFile = getParseFile(KEY_IMAGE);
        if (parseFile != null) {
            imageUrl = parseFile.getUrl();
        }
        title = getString(KEY_TITLE);
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ParseFile getImage() {
        return parseFile;
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

    public void setParseFile(ParseFile parseFile) {
        this.parseFile = parseFile;
        put(KEY_IMAGE, parseFile);
        try {
            save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        setImageUrl(parseFile.getUrl());
    }

    public ParseFile getParseFile() {
        return parseFile;
    }
}
