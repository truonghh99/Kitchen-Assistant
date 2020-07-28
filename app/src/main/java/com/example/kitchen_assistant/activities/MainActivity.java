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
import com.example.kitchen_assistant.fragments.products.CurrentFoodFragment;
import com.example.kitchen_assistant.fragments.recipes.RecipeFragment;
import com.example.kitchen_assistant.fragments.shopping.ShoppingListFragment;
import com.example.kitchen_assistant.storage.CurrentHistoryEntries;
import com.example.kitchen_assistant.storage.CurrentProducts;
import com.example.kitchen_assistant.storage.CurrentRecipes;
import com.example.kitchen_assistant.storage.CurrentShoppingList;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public final String APP_TAG = "MyCustomApp";

    final Fragment currentFoodFragment = CurrentFoodFragment.newInstance();
    final Fragment recipeFragment = RecipeFragment.newInstance();
    final Fragment toDoListFragment = ShoppingListFragment.newInstance();

    private ActivityMainBinding activityMainBinding;
    public static BottomNavigationView bottomNavigation;
    private static FragmentManager fragmentManager;
    private static ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());

        setUpBottomBar();
        setUpProgressBar();
        fetchInfoFromLastUse();
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
                                fragment = currentFoodFragment;
                                break;
                            case R.id.miRecipe:
                                fragment = recipeFragment;
                                break;
                            case R.id.miShoppingList:
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

    // Retrieve saved information about products, recipes, and shopping list from Parse
    private void fetchInfoFromLastUse() {
        CurrentProducts.fetchProductInBackground();
        CurrentRecipes.fetchRecipeInBackground();
        CurrentShoppingList.fetchItemsInBackground();
        CurrentHistoryEntries.fetchEntriesInBackground();
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
    public static void switchFragment(Fragment fragment) {
        fragmentManager.beginTransaction()
                .replace(R.id.flContainer, fragment)
                .addToBackStack(null)
                .commit();
    }

    public static void switchFragmentWithTransition(Fragment fragment, View view, String transitionName) {
        // TODO: create shared element before calling this, otherwise it won't work
        fragmentManager.beginTransaction()
                .addSharedElement(view, transitionName)
                .addToBackStack(null)
                .replace(R.id.flContainer, fragment).commit();
    }

    public static void addFragment(Fragment fragment) {
        fragmentManager.beginTransaction()
                .add(R.id.flContainer, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed(){
        if (fragmentManager.getBackStackEntryCount() > 0) {
            Log.i("MainActivity", "popping backstack");
            fragmentManager.popBackStack();
        } else {
            Log.i("MainActivity", "nothing on backstack, calling super");
            super.onBackPressed();
        }
    }}