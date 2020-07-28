package com.example.kitchen_assistant.fragments.nutrition;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kitchen_assistant.R;
import com.example.kitchen_assistant.activities.MainActivity;
import com.example.kitchen_assistant.databinding.FragmentRecipeNutritionBinding;
import com.example.kitchen_assistant.helpers.ChartHelper;
import com.example.kitchen_assistant.models.Recipe;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;

import org.parceler.Parcels;

public class RecipeNutritionFragment extends Fragment {

    private static final String KEY_RECIPE = "recipe";

    private Recipe recipe;

    private FragmentRecipeNutritionBinding fragmentRecipeNutritionBinding;
    private BarChart bcNutrition;
    private PieChart pcCalories;
    private TextView tvCalories;

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
        bcNutrition = fragmentRecipeNutritionBinding.bcNutrition;
        pcCalories = fragmentRecipeNutritionBinding.pcCalories;

        ((MainActivity) getContext()).getSupportActionBar().setTitle(recipe.getName());

        ChartHelper.drawNutritionBarChart(recipe.getNutrition().getCarbs(), recipe.getNutrition().getProtein(), recipe.getNutrition().getFat(), bcNutrition, getContext());
        ChartHelper.drawCaloriesByNutritionChart(recipe.getNutrition().getCalories(), recipe.getNutrition().getCarbs(), recipe.getNutrition().getProtein(),
                recipe.getNutrition().getFat(), 1200, pcCalories, getContext()); // TODO: Change total to user's customized goal

        return fragmentRecipeNutritionBinding.getRoot();
    }
}