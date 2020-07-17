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
import android.widget.TextView;
import android.widget.Toast;

import com.example.kitchen_assistant.R;
import com.example.kitchen_assistant.databinding.ActivityMainBinding;
import com.example.kitchen_assistant.fragments.CurrentProductFragment;
import com.example.kitchen_assistant.fragments.RecipeFragment;
import com.example.kitchen_assistant.fragments.ShoppingListFragment;
import com.example.kitchen_assistant.models.Recipe;
import com.example.kitchen_assistant.storage.CurrentFoodTypes;
import com.example.kitchen_assistant.storage.CurrentProducts;
import com.example.kitchen_assistant.storage.CurrentRecipes;
import com.example.kitchen_assistant.storage.CurrentShoppingList;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public final String APP_TAG = "MyCustomApp";

    final Fragment currentFoodFragment = CurrentProductFragment.newInstance();
    final Fragment recipeFragment = RecipeFragment.newInstance();
    final Fragment toDoListFragment = ShoppingListFragment.newInstance();

    private ActivityMainBinding activityMainBinding;
    public static BottomNavigationView bottomNavigation;
    private static FragmentManager fragmentManager;
    private static Toolbar toolbar;
    private static TextView tvTitle;
    private ImageView ivLogOut;
    private static ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());

        fetchInfoFromLastUse();
        setUpToolBar();
        setUpBottomBar();
        setUpProgressBar();
    }

    // Switch between different screens using the same bottom bar
    private void setUpBottomBar() {
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContainer, currentFoodFragment).commit();
        bottomNavigation = activityMainBinding.bottomNavigation;
        bottomNavigation.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment fragment;
                        String title;
                        switch (item.getItemId()) {
                            case R.id.miCurrentFood:
                                title = CurrentProductFragment.title;
                                fragment = currentFoodFragment;
                                break;
                            case R.id.miRecipe:
                                title = RecipeFragment.title;
                                fragment = recipeFragment;
                                break;
                            case R.id.miShoppingList:
                            default:
                                title = ShoppingListFragment.title;
                                fragment = toDoListFragment;
                                break;
                        }
                        tvTitle.setText(title);
                        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                        return true;
                    }
                });
        bottomNavigation.setSelectedItemId(R.id.miCurrentFood);
    }

    // Allow user to log out via toolbar
    // TODO: allow user to view & edit profile via toolbar
    private void setUpToolBar() {
        toolbar = activityMainBinding.toolbar;
        tvTitle = activityMainBinding.title;
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
    }

    // Retrieve saved information about products, recipes, and shopping list from Parse
    private void fetchInfoFromLastUse() {
        CurrentProducts.fetchProductInBackground();
        CurrentRecipes.fetchRecipeInBackground();
        CurrentShoppingList.fetchItemsInBackground();
    }

    // Show progress bar while loading. This progress bar is used throughout all other screens
    public void setUpProgressBar() {
        progressBar = activityMainBinding.progressBar;
        showProgressBar();
    }

    // Allow other fragments to show progress bar while needed
    public static void showProgressBar() {
        progressBar.setVisibility(ProgressBar.VISIBLE);
    }

    // Allow other fragments to hide progress bar while needed
    public static void hideProgressBar() {
        progressBar.setVisibility(ProgressBar.INVISIBLE);
    }

    // Go to log in activity when use logged out
    private void goLogIn() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    // Allow other fragments to call each other for flexibility, yet each action must be done via MainActivity
    // to avoid re-initializing fragments (improve speed & memory usage)
    public static void switchFragment(Fragment fragment, String title) {
        tvTitle.setText(title);
        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
    }

    public static void switchFragmentWithTransition(Fragment fragment, String title, View view, String transitionName) {
        // TODO: create shared element before calling this, otherwise it won't work
        tvTitle.setText(title);
        fragmentManager.beginTransaction()
                .addSharedElement(view, transitionName)
                .replace(R.id.flContainer, fragment).commit();
    }
}