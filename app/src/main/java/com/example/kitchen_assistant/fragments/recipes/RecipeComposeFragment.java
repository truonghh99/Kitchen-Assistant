package com.example.kitchen_assistant.fragments.recipes;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kitchen_assistant.adapters.IngredientAdapter;
import com.example.kitchen_assistant.adapters.IngredientComposeAdapter;
import com.example.kitchen_assistant.clients.Spoonacular;
import com.example.kitchen_assistant.databinding.FragmentNewRecipeDetailBinding;
import com.example.kitchen_assistant.databinding.FragmentRecipeComposeBinding;
import com.example.kitchen_assistant.helpers.GlideHelper;
import com.example.kitchen_assistant.helpers.RecipeEvaluator;
import com.example.kitchen_assistant.models.Ingredient;
import com.example.kitchen_assistant.models.Recipe;
import com.example.kitchen_assistant.storage.CurrentRecipes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.parceler.Parcels;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static android.app.Activity.RESULT_OK;

public class RecipeComposeFragment extends Fragment {

    public static final String title = "Add A Recipe";
    private static final String TAG = "RecipeComposeFragment";
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1512;

    private FragmentRecipeComposeBinding fragmentRecipeComposeBinding;
    private ImageView ivImage;
    private EditText etName;
    private Button btInstruction;
    private FloatingActionButton btApprove;
    private String instruction;
    private RecyclerView rvIngredients;
    private IngredientComposeAdapter adapter;
    private List<Ingredient> ingredients;
    private Recipe recipe;
    private File photoFile;

    public RecipeComposeFragment() {
    }

    public static RecipeComposeFragment newInstance() {
        RecipeComposeFragment fragment = new RecipeComposeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        recipe = new Recipe();
        ingredients = new ArrayList<Ingredient>() {{
            Ingredient ingredient = new Ingredient();
            add(ingredient);
        }};

        adapter = new IngredientComposeAdapter(getActivity(), ingredients, rvIngredients);
        rvIngredients.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvIngredients.setAdapter(adapter);

        btInstruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToInstructionCompose();
            }
        });

        btApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveInfo();
                CurrentRecipes.addRecipe(recipe);
                Toast.makeText(getContext(), "Saved your recipe!", Toast.LENGTH_SHORT).show();
            }
        });

        return fragmentRecipeComposeBinding.getRoot();
    }

    private void goToInstructionCompose() {
        DialogFragment dialogFragment = InstructionComposeFragment.newInstance(Parcels.wrap(recipe));
        dialogFragment.show(getActivity().getSupportFragmentManager(), "Dialog");
    }

    private void saveInfo() {
        HashMap<String, Ingredient> ingredientHashMap = generateIngredientHashMap(adapter.ingredients);
        recipe.setName(etName.getText().toString());
        recipe.setIngredients(ingredientHashMap);
        recipe.setCode(Recipe.MANUALLY_INSERT_KEY);

        RecipeEvaluator.evaluateRecipe(recipe);
    }

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
}