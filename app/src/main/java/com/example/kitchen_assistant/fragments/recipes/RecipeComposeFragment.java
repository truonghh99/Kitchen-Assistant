package com.example.kitchen_assistant.fragments.recipes;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecipeComposeFragment extends Fragment {

    public static final String title = "Add A Recipe";
    private static final String TAG = "RecipeComposeFragment";

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
        rvIngredients = fragmentRecipeComposeBinding.rvIngredients;

        recipe = new Recipe();
        ingredients = new ArrayList<Ingredient>() {{
            Ingredient ingredient = new Ingredient();
            add(ingredient);
        }};

        adapter = new IngredientComposeAdapter(getActivity(), ingredients, rvIngredients);
        rvIngredients.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvIngredients.setAdapter(adapter);

        btApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveInfo();
            }
        });

        return fragmentRecipeComposeBinding.getRoot();
    }

    private void saveInfo() {
        HashMap<String, Ingredient> ingredientHashMap = generateIngredientHashMap(ingredients);

        recipe.setName(etName.getText().toString());
        recipe.setIngredients(ingredientHashMap);
        recipe.setCode(Recipe.MANUALLY_INSERT_KEY);

        RecipeEvaluator.evaluateRecipe(recipe);
    }

    private HashMap<String, Ingredient> generateIngredientHashMap(List<Ingredient> ingredients) {
        HashMap<String, Ingredient> result = new HashMap<>();
        for (Ingredient ingredient : ingredients) {
            if (ingredient.getName() != null) {
                result.put(ingredient.getName(), ingredient);
            }
        }
        return result;
    }
}