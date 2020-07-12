package com.example.kitchen_assistant.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.kitchen_assistant.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding activityLoginBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityLoginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(activityLoginBinding.getRoot());


    }
}