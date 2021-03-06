package com.example.kitchen_assistant.fragments.recipes;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kitchen_assistant.activities.MainActivity;
import com.example.kitchen_assistant.adapters.RecipeAdapter;
import com.example.kitchen_assistant.clients.Spoonacular;
import com.example.kitchen_assistant.databinding.FragmentRecipeExploreBinding;
import com.example.kitchen_assistant.models.Recipe;

import java.util.ArrayList;
import java.util.List;

public class RecipeExploreFragment extends Fragment {

    private static final String TAG = "RecipeExploreFragment";
    public static final String title = "Explore Recipes";
    public static final String KEY_QUERY_PARAMETER = "Query parameter";

    private FragmentRecipeExploreBinding fragmentRecipeExploreBinding;
    private RecyclerView rvRecipe;
    private List<Recipe> recipes;
    private static RecipeAdapter adapter;

    public RecipeExploreFragment () {
    }

    // Initialize with a query parameter to request OpenFoodFact and get recipe list to display
    public static RecipeExploreFragment newInstance(String queryParameter) {
        RecipeExploreFragment fragment = new RecipeExploreFragment();
        Bundle args = new Bundle();
        args.putString(KEY_QUERY_PARAMETER, queryParameter);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getArguments() != null) {
            String ingredients = (getArguments().getString(KEY_QUERY_PARAMETER));
            try {
                recipes = queryRecipes(ingredients);
                Log.e(TAG, "GOT: " + recipes.size() + " recipes");
            } catch (InterruptedException e) {
                recipes = new ArrayList<>();
                e.printStackTrace();
            }
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentRecipeExploreBinding = FragmentRecipeExploreBinding.inflate(getLayoutInflater());
        rvRecipe = fragmentRecipeExploreBinding.rvRecipe;

        ((MainActivity) getContext()).getSupportActionBar().setTitle(title);

        // Set up recycler view and adapter
        adapter = new RecipeAdapter(getActivity(), recipes);
        rvRecipe.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvRecipe.setAdapter(adapter);

        return fragmentRecipeExploreBinding.getRoot();
    }

    // Query recipes containing this current product
    private List<Recipe> queryRecipes(String ingredients) throws InterruptedException {
        // Create a one-element list contains only this current food item to fit query's format
        Log.i(TAG, "Asking for recipes with " + ingredients);
        List<Recipe> recipes = Spoonacular.getByIngredients(ingredients);
        Log.i(TAG, "Received " + recipes.size() + " recipes");
        return recipes;
    }
}