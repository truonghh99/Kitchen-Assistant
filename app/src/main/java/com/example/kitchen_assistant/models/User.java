package com.example.kitchen_assistant.models;

import android.os.Parcelable;
import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcel;

@Parcel
public class User {

    public static final String TAG = "UserModel";

    // Keys for Parse
    public static final String KEY_USERNAME = "username";
    public static final String KEY_NAME = "name";
    public static final String KEY_PROFILE_IMG = "profileImage";
    public static final String KEY_CALORIES_GOAL = "caloriesGoal";

    // Local values
    private ParseUser user;
    private String username = "nousername";
    private ParseFile profileImage;
    private String name = "No name available";
    private Float caloriesGoal = (float) 1200;

    public static User fetchFromUserId(String userId) {
        Log.e(TAG, "Finding user with user id: " + userId);
        ParseQuery<ParseUser> query = ParseQuery.getQuery("_User");
        User user = new User();
        query.whereEqualTo("objectId", userId);
        ParseUser parseUser = null;
        try {
            parseUser = query.find().get(0);
        } catch (ParseException e) {
            Log.e(TAG, "Cannot find user");
            e.printStackTrace();
        }
        user.setParseUser(parseUser);
        user.fetchInfo();
        return user;
    }

    private void setParseUser(ParseUser parseUser) {
        user = parseUser;
    }

    public void fetchInfo() {
        try {
            user.fetch();
        } catch (ParseException e) {
            Log.e(TAG, "CANNOT FETCH USER");
            e.printStackTrace();
        }
        username = user.getString(KEY_USERNAME);
        profileImage = user.getParseFile(KEY_PROFILE_IMG);
        name = user.getString(KEY_NAME);
        if (user.getNumber(KEY_CALORIES_GOAL) != null) caloriesGoal = user.getNumber(KEY_CALORIES_GOAL).floatValue();
    }

    public void saveInfo() {
        user.put(KEY_PROFILE_IMG, profileImage);
        user.put(KEY_USERNAME, username);
        user.put(KEY_NAME, name);
        user.put(KEY_CALORIES_GOAL, caloriesGoal);
        user.saveInBackground();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ParseFile getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(ParseFile profileImage) {
        this.profileImage = profileImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getCaloriesGoal() {
        return caloriesGoal;
    }

    public void setCaloriesGoal(float goal) {
        caloriesGoal = goal;
    }

}
