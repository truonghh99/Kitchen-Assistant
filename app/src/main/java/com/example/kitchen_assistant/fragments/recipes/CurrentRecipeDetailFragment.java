package com.example.kitchen_assistant.fragments.recipes;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kitchen_assistant.activities.MainActivity;
import com.example.kitchen_assistant.adapters.IngredientAdapter;
import com.example.kitchen_assistant.databinding.FragmentCurrentRecipeDetailBinding;
import com.example.kitchen_assistant.fragments.reviews.ReviewComposeFragment;
import com.example.kitchen_assistant.fragments.reviews.ReviewFragment;
import com.example.kitchen_assistant.helpers.GlideHelper;
import com.example.kitchen_assistant.helpers.RecipeEvaluator;
import com.example.kitchen_assistant.models.Ingredient;
import com.example.kitchen_assistant.models.Recipe;
import com.example.kitchen_assistant.models.Review;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.parceler.Parcels;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewRecipeDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CurrentRecipeDetailFragment extends Fragment {

    private static final String KEY_RECIPE = "Key Recipe";
    public static final String title = "Recipe Detail";

    private Recipe recipe;
    private FragmentCurrentRecipeDetailBinding fragmentCurrentRecipeDetailBinding;
    private ImageView ivImage;
    private TextView tvName;
    private Button btInstruction;
    private RecyclerView rvIngredients;
    private static IngredientAdapter adapter;
    private List<Ingredient> ingredients;
    private FloatingActionButton btMenuOpen;
    private FloatingActionButton btRemove;
    private FloatingActionButton btCook;
    private FloatingActionButton btReview;
    private TextView tvStatus;
    private TextView tvReviewCount;
    private RatingBar ratingBar;

    public CurrentRecipeDetailFragment() {
    }

    public static CurrentRecipeDetailFragment newInstance(Parcelable recipe) {
        CurrentRecipeDetailFragment fragment = new CurrentRecipeDetailFragment();
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
        fragmentCurrentRecipeDetailBinding = FragmentCurrentRecipeDetailBinding.inflate(getLayoutInflater());
        ivImage = fragmentCurrentRecipeDetailBinding.ivImage;
        tvName = fragmentCurrentRecipeDetailBinding.tvName;
        btInstruction = fragmentCurrentRecipeDetailBinding.btInstruction;
        rvIngredients = fragmentCurrentRecipeDetailBinding.rvIngredients;
        btMenuOpen = fragmentCurrentRecipeDetailBinding.btMenuOpen;
        btCook = fragmentCurrentRecipeDetailBinding.btCook;
        btRemove = fragmentCurrentRecipeDetailBinding.btRemove;
        btReview = fragmentCurrentRecipeDetailBinding.btReview;
        ratingBar = fragmentCurrentRecipeDetailBinding.ratingBar;
        tvStatus = fragmentCurrentRecipeDetailBinding.tvStatus;
        tvReviewCount = fragmentCurrentRecipeDetailBinding.tvReviewCount;

        ((MainActivity) getContext()).getSupportActionBar().setTitle(title);
        loadRating();

        ingredients = recipe.getIngredientList();
        adapter = new IngredientAdapter(getActivity(), ingredients);
        rvIngredients.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        rvIngredients.setAdapter(adapter);

        GlideHelper.loadImage(recipe.getImageUrl(), getContext(), ivImage);
        tvName.setText(recipe.getName());

        tvReviewCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToReview();
            }
        });

        btInstruction.setOnClickListener(new View.OnClickListener() {
            // TODO: display existing instruction from recipe object
            @Override
            public void onClick(View view) {
                String instruction = recipe.getInstructions();
                goToInstruction(instruction);
            }
        });

        btMenuOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openOrCloseFabMenu();
            }
        });

        btCook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recipe.isCookable()) {
                    RecipeEvaluator.updateFoodFromCookedRecipe(recipe);
                    Toast.makeText(getContext(), "Subtracted all the ingredients used in this recipe", Toast.LENGTH_SHORT).show();
                    RecipeEvaluator.evaluateRecipe(recipe);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Please select product for each ingredient", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToReviewCompose();
            }
        });

        if (recipe.isCookable()) {
            tvStatus.setText("You have enough ingredient to cook this recipe!");
        } else {
            tvStatus.setText("A few ingredients are still needed");
        }

        return fragmentCurrentRecipeDetailBinding.getRoot();
    }

    private String setUpReviewCount(long numReviews) {
        if (numReviews < 2) {
            return numReviews + " review";
        }
        return numReviews + " reviews";
    }

    private void openOrCloseFabMenu() {
        if (btReview.getVisibility() == View.INVISIBLE) {
            btCook.setVisibility(View.VISIBLE);
            btRemove.setVisibility(View.VISIBLE);
            btReview.setVisibility(View.VISIBLE);
        } else {
            btCook.setVisibility(View.INVISIBLE);
            btRemove.setVisibility(View.INVISIBLE);
            btReview.setVisibility(View.INVISIBLE);
        }
    }

    private void goToReviewCompose() {
        Review review = new Review();
        DialogFragment dialogFragment = ReviewComposeFragment.newInstance(Parcels.wrap(recipe), Parcels.wrap(review));
        dialogFragment.setTargetFragment(this, 0);
        dialogFragment.show(getActivity().getSupportFragmentManager(), "Dialog");
    }

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