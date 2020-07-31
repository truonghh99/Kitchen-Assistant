package com.example.kitchen_assistant.fragments.nutrition;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.os.Parcelable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.kitchen_assistant.databinding.FragmentInstructionBinding;
import com.example.kitchen_assistant.databinding.FragmentInstructionComposeBinding;
import com.example.kitchen_assistant.databinding.FragmentNutritionComposeBinding;
import com.example.kitchen_assistant.databinding.FragmentRecipeComposeBinding;
import com.example.kitchen_assistant.models.Nutrition;
import com.example.kitchen_assistant.models.Recipe;

import org.parceler.Parcels;

public class NutritionComposeFragment extends DialogFragment {

    private static final String TAG = "NutritionCompose";
    private static final String KEY_RECIPE = "RECIPE";

    private FragmentNutritionComposeBinding fragmentNutritionComposeBinding;

    private Recipe recipe;
    private Button btSave;
    private EditText etCalories;
    private EditText etCarbs;
    private EditText etProtein;
    private EditText etFat;

    public NutritionComposeFragment() {
    }

    // Initialize with a recipe to show existing instruction or attach new one
    public static NutritionComposeFragment newInstance(Parcelable recipe) {
        NutritionComposeFragment fragment = new NutritionComposeFragment();
        Bundle args = new Bundle();
        args.putParcelable(KEY_RECIPE, recipe);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getArguments() != null) {
            recipe = Parcels.unwrap(getArguments().getParcelable(KEY_RECIPE));
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentNutritionComposeBinding = fragmentNutritionComposeBinding.inflate(getLayoutInflater());
        btSave = fragmentNutritionComposeBinding.btSave;
        etCalories = fragmentNutritionComposeBinding.etCalories;
        etProtein = fragmentNutritionComposeBinding.etProtein;
        etCarbs = fragmentNutritionComposeBinding.etCarbs;
        etFat = fragmentNutritionComposeBinding.etFat;

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveInfo();
                dismiss();
            }
        });

        return fragmentNutritionComposeBinding.getRoot();
    }

    private void saveInfo() {
        Float calories = Float.parseFloat(etCalories.getText().toString());
        Float carbs = Float.parseFloat(etCarbs.getText().toString());
        Float protein = Float.parseFloat(etProtein.getText().toString());
        Float fat = Float.parseFloat(etFat.getText().toString());

        Nutrition.requestManualNutrition(recipe, calories, carbs, protein, fat);
    }
}