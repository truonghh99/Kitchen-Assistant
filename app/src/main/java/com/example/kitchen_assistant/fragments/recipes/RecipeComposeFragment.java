package com.example.kitchen_assistant.fragments.recipes;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.kitchen_assistant.activities.MainActivity;
import com.example.kitchen_assistant.adapters.IngredientComposeAdapter;
import com.example.kitchen_assistant.databinding.FragmentRecipeComposeBinding;
import com.example.kitchen_assistant.fragments.camera.PhotoFragment;
import com.example.kitchen_assistant.helpers.GlideHelper;
import com.example.kitchen_assistant.helpers.RecipeEvaluator;
import com.example.kitchen_assistant.models.Ingredient;
import com.example.kitchen_assistant.models.Nutrition;
import com.example.kitchen_assistant.models.Rating;
import com.example.kitchen_assistant.models.Recipe;
import com.example.kitchen_assistant.storage.CurrentRecipes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.ParseException;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecipeComposeFragment extends Fragment {

    public static final String title = "Add A Recipe";
    private static final String TAG = "RecipeComposeFragment";
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1512;
    private static final int REQUEST_CODE = 0;
    private static final String KEY_RECIPE = "recipe";

    private FragmentRecipeComposeBinding fragmentRecipeComposeBinding;
    private ImageView ivImage;
    private EditText etName;
    private Button btInstruction;
    private FloatingActionButton btApprove;
    private RecyclerView rvIngredients;
    private IngredientComposeAdapter adapter;
    private List<Ingredient> ingredients;
    public Recipe recipe;

    public RecipeComposeFragment() {
    }

    // Initialize with an empty recipe to modify
    public static RecipeComposeFragment newInstance(Parcelable recipe) {
        RecipeComposeFragment fragment = new RecipeComposeFragment();
        Bundle args = new Bundle();
        args.putParcelable(KEY_RECIPE, recipe);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            recipe = Parcels.unwrap(getArguments().getParcelable(KEY_RECIPE));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentRecipeComposeBinding = FragmentRecipeComposeBinding.inflate(getLayoutInflater());
        ivImage = fragmentRecipeComposeBinding.ivImage;
        etName = fragmentRecipeComposeBinding.etName;
        btInstruction = fragmentRecipeComposeBinding.btInstruction;
        btApprove = fragmentRecipeComposeBinding.btApprove;
        rvIngredients = fragmentRecipeComposeBinding.rvIngredients;

        loadImage();

        ((MainActivity) getContext()).getSupportActionBar().setTitle(title);
        ingredients = new ArrayList<Ingredient>() {{
            Ingredient ingredient = new Ingredient();
            add(ingredient);
        }};

        // Set up recycler view & adapter
        adapter = new IngredientComposeAdapter(getActivity(), ingredients, rvIngredients);
        rvIngredients.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvIngredients.setAdapter(adapter);

        // Compose instruction
        btInstruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToInstructionCompose();
            }
        });

        // Modify & save recipe based on given input
        btApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveInfo();
                CurrentRecipes.addRecipe(recipe);
                Toast.makeText(getContext(), "Saved your recipe!", Toast.LENGTH_SHORT).show();
            }
        });

        // Allow user to upload photo of recipe
        ivImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToPhoto();
            }
        });

        return fragmentRecipeComposeBinding.getRoot();
    }

    private void goToInstructionCompose() {
        DialogFragment dialogFragment = InstructionComposeFragment.newInstance(Parcels.wrap(recipe));
        dialogFragment.show(getActivity().getSupportFragmentManager(), "Dialog");
    }

    // Extract information from user's input and assign it to given recipe
    private void saveInfo() {
        HashMap<String, Ingredient> ingredientHashMap = generateIngredientHashMap(adapter.ingredients);
        recipe.setName(etName.getText().toString());
        recipe.setIngredients(ingredientHashMap);
        recipe.setCode(etName.getText().toString());

        try {
            Rating.requestRating(recipe);
            Nutrition.requestManualNutrition(recipe);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        RecipeEvaluator.evaluateRecipe(recipe);
    }

    // Generate hashmap from list of ingredients to assign to recipe object
    private HashMap<String, Ingredient> generateIngredientHashMap(List<Ingredient> adapterIngredients) {
        HashMap<String, Ingredient> result = new HashMap<>();
        for (Ingredient ingredient : adapterIngredients) {
            if (ingredient.getName() != null) {
                ingredient.setRecipe(recipe);
                result.put(ingredient.getName(), ingredient);
            }
        }
        return result;
    }

    // Go to photo compose screen
    private void goToPhoto() {
        Fragment fragment = PhotoFragment.newInstance(Parcels.wrap(recipe), Recipe.TAG);
        fragment.setTargetFragment(this, REQUEST_CODE);
        MainActivity.switchFragment(fragment);
    }

    // Update recipe's image using result of photo compose screen
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loadImage();
    }

    public void loadImage() {
        if (recipe.getParseFile() != null) {
            Log.e(TAG, recipe.getImageUrl());
            GlideHelper.loadImage(recipe.getImageUrl(), getContext(), ivImage);
        } else {
            Log.e(TAG, "No image to load");
        }
    }
}