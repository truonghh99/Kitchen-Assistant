package com.example.kitchen_assistant.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.kitchen_assistant.R;
import com.example.kitchen_assistant.databinding.ActivityMainBinding;
import com.example.kitchen_assistant.fragments.CurrentFoodFragment;
import com.example.kitchen_assistant.fragments.RecipeFragment;
import com.example.kitchen_assistant.fragments.ShoppingListFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public final String APP_TAG = "MyCustomApp";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public String photoFileName = "barcode_photo.jpg";
    File photoFile;

    final Fragment currentFoodFragment = CurrentFoodFragment.newInstance();
    final Fragment recipeFragment = RecipeFragment.newInstance();
    final Fragment toDoListFragment = ShoppingListFragment.newInstance();

    private ActivityMainBinding activityMainBinding;
    private BottomNavigationView bottomNavigation;
    private static FragmentManager fragmentManager;
    private Toolbar toolbar;
    private ImageView ivLogOut;
    private static ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());

        setUpBottomBar();
        setUpToolBar();
        setUpProgressBar();
    }

    private void setUpBottomBar() {
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContainer, currentFoodFragment).commit();
        bottomNavigation = activityMainBinding.bottomNavigation;
        bottomNavigation.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment fragment;
                        switch (item.getItemId()) {
                            case R.id.miCurrentFood:
                                fragment = currentFoodFragment;
                                break;
                            case R.id.miRecipe:
                                fragment = recipeFragment;
                                break;
                            case R.id.miToDoList:
                            default:
                                fragment = toDoListFragment;
                                break;
                        }
                        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                        return true;
                    }
                });
        bottomNavigation.setSelectedItemId(R.id.miCurrentFood);
    }

    private void setUpToolBar() {
        toolbar = activityMainBinding.toolbar;
        ivLogOut = activityMainBinding.ivLogOut;
        ivLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgressBar();
                ParseUser.logOutInBackground(new LogOutCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.i(TAG, String.valueOf(e));
                            return;
                        }
                        Log.i(TAG, "Logged out!");
                        Toast.makeText(MainActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                        goLogIn();
                    }
                });
            }
        });
        progressBar = activityMainBinding.progressBar;
        hideProgressBar();
    }

    public void setUpProgressBar() {
        progressBar = activityMainBinding.progressBar;
    }

    public static void showProgressBar() {
        progressBar.setVisibility(ProgressBar.VISIBLE);
    }

    public static void hideProgressBar() {
        progressBar.setVisibility(ProgressBar.INVISIBLE);
    }

    private void goLogIn() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    public static void switchFragment(Fragment fragment) {
        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}