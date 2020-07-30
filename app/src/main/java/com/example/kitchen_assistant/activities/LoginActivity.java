package com.example.kitchen_assistant.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.kitchen_assistant.databinding.ActivityLoginBinding;
import com.example.kitchen_assistant.models.User;
import com.example.kitchen_assistant.storage.CurrentProducts;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.facebook.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javax.annotation.Nullable;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private RelativeLayout rlSignup;
    private String email;
    private String password;
    private Button btLoginFacebook;
    private CallbackManager callbackManager;
    private String name;
    private ParseFile profileImg;

    private ActivityLoginBinding activityLoginBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityLoginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(activityLoginBinding.getRoot());

        // Allow previously logged in user to skip the log in screen
        if (ParseUser.getCurrentUser() != null) {
            goMainActivity();
        }

        etUsername = activityLoginBinding.etUsername;
        etPassword = activityLoginBinding.etPassword;
        btnLogin = activityLoginBinding.btnLogin;
        rlSignup = activityLoginBinding.rlSignUp;
        btLoginFacebook = activityLoginBinding.btLoginFacebook;

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = etUsername.getText().toString();
                password = etPassword.getText().toString();
                if (email == null || password == null || email.length() == 0 || password.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Email and password cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                loginUser(email, password);
            }
        });

        rlSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goSignUpActivity();
            }
        });

        final ArrayList<String> permissions = new ArrayList<String>() {
            {
                add("public_profile");
            }
        };
        btLoginFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "CLICKED");
                ParseFacebookUtils.logInWithReadPermissionsInBackground(LoginActivity.this, permissions, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException err) {
                        if (user == null) {
                            ParseUser.logOut();
                            Log.e(TAG, "The user cancelled the Facebook login.");
                            Toast.makeText(getApplicationContext(), "Facebook log in was unsuccessful. Please try again.", Toast.LENGTH_SHORT);
                        } else if (user.isNew()) {
                            Log.e(TAG, "User signed up and logged in through Facebook!");
                            getUserDetailsFromFB();
                        } else {
                            Log.e(TAG, "User logged in through Facebook!");
                            Toast.makeText(getApplicationContext(), "Successfully Logged In!", Toast.LENGTH_SHORT);
                            goMainActivity();
                        }
                    }
                });
            }
        });
    }
    private void getUserDetailsFromFB() {

        Bundle parameters = new Bundle();
        parameters.putString("fields", "email,name,picture");
        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me", parameters, HttpMethod.GET, new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        try {

                            email = response.getJSONObject().getString("email");
                            name = response.getJSONObject().getString("name");

                            JSONObject picture = response.getJSONObject().getJSONObject("picture");
                            JSONObject data = picture.getJSONObject("data");

                            saveNewUser(email, name);
                            // Get profile image. Link: https://blog.iamsuleiman.com/facebook-login-with-parse-part-2/
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();
    }

    private void saveNewUser(String email, String name) {
        ParseUser parseUser = ParseUser.getCurrentUser();
        parseUser.setUsername(name);
        parseUser.setEmail(email);
        parseUser.saveInBackground();
        goMainActivity();
    }

    private void loginUser(String username, String password) {
        Log.e(TAG, "Attempt to login");
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with login: " + e.toString());
                    Toast.makeText(getApplicationContext(), String.valueOf(e), Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(getApplicationContext(), "Successfully Logged In!", Toast.LENGTH_SHORT);
                goMainActivity();
            }
        });
    }

    private void goMainActivity() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    private void goSignUpActivity() {
        startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }
}