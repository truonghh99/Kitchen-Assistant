package com.example.kitchen_assistant.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.kitchen_assistant.R;
import com.example.kitchen_assistant.databinding.ActivityLoginBinding;
import com.example.kitchen_assistant.databinding.ActivitySignUpBinding;
import com.example.kitchen_assistant.helpers.GlideHelper;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";
    private ImageView ivAvatar;
    private EditText etName;
    private EditText etEmail;
    private EditText etUsername;
    private EditText etPassword;
    private Button btnSignUp;
    private RelativeLayout rlLogIn;
    private String email;
    private String password;
    private ActivitySignUpBinding activitySignUpBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySignUpBinding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(activitySignUpBinding.getRoot());

        ivAvatar = activitySignUpBinding.ivAvatar;
        etName = activitySignUpBinding.etName;
        etEmail = activitySignUpBinding.etEmail;
        etUsername = activitySignUpBinding.etUsername;
        etPassword = activitySignUpBinding.etPassword;
        btnSignUp = activitySignUpBinding.btnSignup;
        rlLogIn = activitySignUpBinding.rlLogIn;

        btnSignUp.setOnClickListener(new View.OnClickListener() {
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
        rlLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goLoginActivity();
            }
        });
        GlideHelper.loadAvatar("default", getApplicationContext(), ivAvatar);
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
        startActivity(new Intent(SignUpActivity.this, MainActivity.class));
        finish();
    }

    private void goLoginActivity() {
        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
        finish();
    }
}