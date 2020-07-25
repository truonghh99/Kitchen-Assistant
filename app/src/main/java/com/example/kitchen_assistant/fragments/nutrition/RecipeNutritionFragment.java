package com.example.kitchen_assistant.fragments.nutrition;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kitchen_assistant.R;
import com.example.kitchen_assistant.databinding.FragmentRecipeNutritionBinding;
import com.example.kitchen_assistant.models.Recipe;

import org.parceler.Parcels;

public class RecipeNutritionFragment extends Fragment {

    private static final String KEY_RECIPE = "recipe";

    private Recipe recipe;

    private FragmentRecipeNutritionBinding fragmentRecipeNutritionBinding;

    public RecipeNutritionFragment() {
    }

    public static RecipeNutritionFragment newInstance(Parcelable recipe) {
        RecipeNutritionFragment fragment = new RecipeNutritionFragment();
        Bundle args = new Bundle();
        args.putParcelable(KEY_RECIPE, recipe);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            recipe = Parcels.unwrap(getArguments().getParcelable(KEY_RECIPE));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentRecipeNutritionBinding = FragmentRecipeNutritionBinding.inflate(getLayoutInflater());
        return fragmentRecipeNutritionBinding.getRoot();
    }
}