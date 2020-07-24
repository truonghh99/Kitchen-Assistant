package com.example.kitchen_assistant.models;

import android.os.Parcelable;
import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

@ParseClassName("User")
public class User extends ParseObject implements Parcelable {

    public static final String TAG = "UserModel";

    // Keys for Parse
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PROFILE_IMG = "profileImage";

    // Local values
    private String username;
    private ParseFile profileImage;

    public static User fetchFromUserId(String userId) {
        User user = new User();
        user.setObjectId(userId);
        user.fetchInfo();
        return user;
    }

    public void fetchInfo() {
        try {
            fetch();
        } catch (ParseException e) {
            Log.e(TAG, "CANNOT FETCH USER");
            e.printStackTrace();
        }
        username = getString(KEY_USERNAME);
        profileImage = getParseFile(KEY_PROFILE_IMG);
    }

    public void saveInfo() {
        put(KEY_PROFILE_IMG, profileImage);
        put(KEY_USERNAME, username);
        saveInBackground();
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
}
