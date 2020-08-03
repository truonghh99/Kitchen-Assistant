package com.example.kitchen_assistant.fragments.recipes;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kitchen_assistant.R;
import com.example.kitchen_assistant.activities.MainActivity;
import com.example.kitchen_assistant.adapters.IngredientAdapter;
import com.example.kitchen_assistant.clients.Spoonacular;
import com.example.kitchen_assistant.databinding.FragmentNewRecipeDetailBinding;
import com.example.kitchen_assistant.fragments.nutrition.RecipeNutritionFragment;
import com.example.kitchen_assistant.fragments.reviews.ReviewComposeFragment;
import com.example.kitchen_assistant.fragments.reviews.ReviewFragment;
import com.example.kitchen_assistant.helpers.FabAnimationHelper;
import com.example.kitchen_assistant.helpers.GlideHelper;
import com.example.kitchen_assistant.helpers.RecipeEvaluator;
import com.example.kitchen_assistant.models.Ingredient;
import com.example.kitchen_assistant.models.Rating;
import com.example.kitchen_assistant.models.Recipe;
import com.example.kitchen_assistant.models.Review;
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
    private FloatingActionButton btReview;
    private FloatingActionButton btMenuOpen;
    private String instruction;
    private RecyclerView rvIngredients;
    private RatingBar ratingBar;
    private static IngredientAdapter adapter;
    private TextView tvStatus;
    private TextView tvReviewCount;
    private List<Ingredient> ingredients;
    private Boolean fabMenuOpen = false;

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
        setHasOptionsMenu(true);
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
        btReview = fragmentNewRecipeDetailBinding.btReview;
        btCook = fragmentNewRecipeDetailBinding.btCook;
        rvIngredients = fragmentNewRecipeDetailBinding.rvIngredients;
        ratingBar = fragmentNewRecipeDetailBinding.ratingBar;
        tvStatus = fragmentNewRecipeDetailBinding.tvStatus;
        tvReviewCount = fragmentNewRecipeDetailBinding.tvReviewCount;

        ((MainActivity) getContext()).getSupportActionBar().setTitle(title);

        // Get information from recipe object
        ratingBar.setRating(recipe.getNumericRating());
        ingredients = recipe.getIngredientList();
        adapter = new IngredientAdapter(getActivity(), ingredients);
        rvIngredients.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        rvIngredients.setAdapter(adapter);

        // Bind views
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

        // Allow user to read all reviews of current recipe
        tvReviewCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToReview();
            }
        });

        // Open or close floating menu
        btMenuOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openOrCloseFabMenu();
            }
        });

        // Allow user to add current recipe to personal library
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

        btReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToReviewCompose();
            }
        });

        // Set recipe's status
        if (recipe.isCookable()) {
            tvStatus.setText("You have enough ingredient to cook this recipe!");
        } else {
            tvStatus.setText("A few ingredients are still needed");
        }

        tvReviewCount.setText(setUpReviewCount(recipe.getRating().getNumReviews()));
        return fragmentNewRecipeDetailBinding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        ((MainActivity) getContext()).getSupportActionBar().setTitle(title);
        inflater.inflate(R.menu.menu_recipe_toolbar, menu);
        MenuItem miChart = menu.findItem(R.id.miChart);
        miChart.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                goToNutritionChart();
                return true;
            }
        });
    }

    private void goToNutritionChart() {
        Fragment fragment = RecipeNutritionFragment.newInstance(Parcels.wrap(recipe));
        MainActivity.switchFragment(fragment);
    }

    // Helper class to properly display number of reviews
    private String setUpReviewCount(long numReviews) {
        if (numReviews < 2) {
            return numReviews + " review";
        }
        return numReviews + " reviews";
    }

    private void openOrCloseFabMenu() {
        if (!fabMenuOpen) {
            FabAnimationHelper.showFab(btAdd, getContext());
            FabAnimationHelper.showFab(btReview, getContext());
            FabAnimationHelper.showFab(btCook, getContext());
            FabAnimationHelper.rotateOpenFab(btMenuOpen, getContext());
            fabMenuOpen = true;
        } else {
            FabAnimationHelper.hideFab(btAdd, getContext());
            FabAnimationHelper.hideFab(btReview, getContext());
            FabAnimationHelper.hideFab(btCook, getContext());
            FabAnimationHelper.rotateCloseFab(btMenuOpen, getContext());
            fabMenuOpen = false;
        }
    }

    // Query instruction of given recipe when user chooses to
    private void queryInstruction() {
        try {
            instruction = Spoonacular.getInstruction(recipe.getCode());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private void goToReviewCompose() {
        Review review = new Review();
        Fragment fragment = ReviewComposeFragment.newInstance(Parcels.wrap(recipe), Parcels.wrap(review));
        fragment.setTargetFragment(this, 0);
        MainActivity.addFragment(fragment);
    }

    // Go to instruction fragment
    private void goToInstruction(String instruction) {
        DialogFragment dialogFragment = InstructionFragment.newInstance(instruction);
        dialogFragment.show(getActivity().getSupportFragmentManager(), "Dialog");
    }

    private void goToReview() {
        ReviewFragment fragment = ReviewFragment.newInstance(Parcels.wrap(recipe));
        MainActivity.switchFragment(fragment);
    }

    public static void notifyChange() {
        if (adapter != null) adapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loadRating();
    }

    private void loadRating() {
        tvReviewCount.setText(setUpReviewCount(recipe.getRating().getNumReviews()));
        ratingBar.setRating(recipe.getNumericRating());
    }
}