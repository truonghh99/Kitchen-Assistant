package com.example.kitchen_assistant.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kitchen_assistant.databinding.ActivityLoginBinding;
import com.example.kitchen_assistant.models.User;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnSignup;
    private String email;
    private String password;

    private ActivityLoginBinding activityLoginBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityLoginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(activityLoginBinding.getRoot());


        if (ParseUser.getCurrentUser() != null) {
            goMainActivity();
        }

        etUsername = activityLoginBinding.etUsername;
        etPassword = activityLoginBinding.etPassword;
        btnLogin = activityLoginBinding.btnLogin;
        btnSignup = activityLoginBinding.btnSignup;

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

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = etUsername.getText().toString();
                password = etPassword.getText().toString();
                if (email == null || password == null || email.length() == 0 || password.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Email and password cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                registerUser(email, password);
            }
        });
    }

    private void loginUser(String username, String password) {
        Log.e(TAG, "Attempt to login");
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with login", e);
                    Toast.makeText(getApplicationContext(), String.valueOf(e), Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(getApplicationContext(), "Successfully Logged In!", Toast.LENGTH_SHORT);
                goMainActivity();
            }
        });
    }

    private void registerUser(String username, String password) {
        Log.e(TAG, "Attempt to register");
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    ParseUser.logOut();
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
                Toast.makeText(getApplicationContext(), "Successfully Signed Up!", Toast.LENGTH_SHORT);
                goMainActivity();
            }
        });
    }

    private void goMainActivity() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }
}