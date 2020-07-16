package com.example.kitchen_assistant.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kitchen_assistant.adapters.RecipeAdapter;
import com.example.kitchen_assistant.databinding.FragmentRecipeBinding;
import com.example.kitchen_assistant.models.Recipe;
import com.example.kitchen_assistant.storage.CurrentRecipes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class RecipeFragment extends Fragment {

    private static final String TAG = "RecipeFragment";
    public static final String title = "My Recipes";

    private FragmentRecipeBinding fragmentRecipeBinding;
    private RecyclerView rvRecipe;
    private FloatingActionButton btSearch;
    private FloatingActionButton btAdd;
    private List<Recipe> recipes;
    private static RecipeAdapter adapter;

    public RecipeFragment () {
    }

    public static RecipeFragment newInstance() {
        RecipeFragment fragment = new RecipeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentRecipeBinding = FragmentRecipeBinding.inflate(getLayoutInflater());
        btAdd = fragmentRecipeBinding.btAdd;
        btSearch = fragmentRecipeBinding.btSearch;
        rvRecipe = fragmentRecipeBinding.rvRecipe;

        recipes = CurrentRecipes.recipes;
        adapter = new RecipeAdapter(getActivity(), recipes);
        rvRecipe.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvRecipe.setAdapter(adapter);

        return fragmentRecipeBinding.getRoot();
    }


    public static void notifyDataChange() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}