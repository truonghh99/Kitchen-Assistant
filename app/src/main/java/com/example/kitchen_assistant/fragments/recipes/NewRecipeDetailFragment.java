package com.example.kitchen_assistant.fragments.recipes;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kitchen_assistant.adapters.IngredientAdapter;
import com.example.kitchen_assistant.clients.Spoonacular;
import com.example.kitchen_assistant.databinding.FragmentNewRecipeDetailBinding;
import com.example.kitchen_assistant.helpers.GlideHelper;
import com.example.kitchen_assistant.helpers.RecipeEvaluator;
import com.example.kitchen_assistant.models.Ingredient;
import com.example.kitchen_assistant.models.Recipe;
import com.example.kitchen_assistant.storage.CurrentRecipes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.parceler.Parcels;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewRecipeDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewRecipeDetailFragment extends Fragment {

    private static final String KEY_RECIPE = "Key Recipe";
    public static final String title = "New Recipe Detail";
    private static final String TAG = "NewRecipeDetailFragment";

    private Recipe recipe;
    private FragmentNewRecipeDetailBinding fragmentNewRecipeDetailBinding;
    private ImageView ivImage;
    private TextView tvName;
    private Button btInstruction;
    private FloatingActionButton btAdd;
    private FloatingActionButton btCook;
    private FloatingActionButton btShop;
    private FloatingActionButton btMenuOpen;
    private String instruction;
    private RecyclerView rvIngredients;
    private static IngredientAdapter adapter;
    private List<Ingredient> ingredients;

    public NewRecipeDetailFragment() {
    }

    public static NewRecipeDetailFragment newInstance(Parcelable recipe) {
        NewRecipeDetailFragment fragment = new NewRecipeDetailFragment();
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
        RecipeEvaluator.evaluateRecipe(recipe);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentNewRecipeDetailBinding = FragmentNewRecipeDetailBinding.inflate(getLayoutInflater());
        ivImage = fragmentNewRecipeDetailBinding.ivImage;
        tvName = fragmentNewRecipeDetailBinding.tvName;
        btInstruction = fragmentNewRecipeDetailBinding.btInstruction;
        btAdd = fragmentNewRecipeDetailBinding.btAdd;
        btMenuOpen = fragmentNewRecipeDetailBinding.btMenuOpen;
        btShop = fragmentNewRecipeDetailBinding.btShop;
        btCook = fragmentNewRecipeDetailBinding.btCook;
        rvIngredients = fragmentNewRecipeDetailBinding.rvIngredients;

        ingredients = recipe.getIngredientList();
        adapter = new IngredientAdapter(getActivity(), ingredients);
        rvIngredients.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        rvIngredients.setAdapter(adapter);

        GlideHelper.loadImage(recipe.getImageUrl(), getContext(), ivImage);
        tvName.setText(recipe.getName());
        btInstruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                queryInstruction();
                Log.e(TAG, instruction);
                goToInstruction(instruction);
            }
        });
        btMenuOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openOrCloseFabMenu();
            }
        });
        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (instruction == null) queryInstruction();
                recipe.setInstructions(instruction);
                RecipeEvaluator.evaluateRecipe(recipe);
                CurrentRecipes.addRecipe(recipe);
                Toast.makeText(getContext(), "Recipe added to your library", Toast.LENGTH_SHORT).show();
            }
        });
        return fragmentNewRecipeDetailBinding.getRoot();
    }

    private void openOrCloseFabMenu() {
        if (btShop.getVisibility() == View.INVISIBLE) {
            btCook.setVisibility(View.VISIBLE);
            btAdd.setVisibility(View.VISIBLE);
            btShop.setVisibility(View.VISIBLE);
        } else {
            btCook.setVisibility(View.INVISIBLE);
            btAdd.setVisibility(View.INVISIBLE);
            btShop.setVisibility(View.INVISIBLE);
        }
    }

    private void queryInstruction() {
        try {
            instruction = Spoonacular.getInstruction(recipe.getCode());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void goToInstruction(String instruction) {
        DialogFragment dialogFragment = InstructionFragment.newInstance(instruction);
        dialogFragment.show(getActivity().getSupportFragmentManager(), "Dialog");
    }

    public static void notifyChange() {
        if (adapter != null) adapter.notifyDataSetChanged();
    }

}