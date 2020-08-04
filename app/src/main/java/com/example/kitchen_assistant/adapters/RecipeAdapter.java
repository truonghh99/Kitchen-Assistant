package com.example.kitchen_assistant.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kitchen_assistant.R;
import com.example.kitchen_assistant.activities.MainActivity;
import com.example.kitchen_assistant.databinding.ItemRecipeBinding;
import com.example.kitchen_assistant.fragments.recipes.CurrentRecipeDetailFragment;
import com.example.kitchen_assistant.fragments.recipes.NewRecipeDetailFragment;
import com.example.kitchen_assistant.helpers.GlideHelper;
import com.example.kitchen_assistant.models.Recipe;
import com.example.kitchen_assistant.storage.CurrentRecipes;

import org.parceler.Parcels;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    private final String TAG = "RecipeAdapter";
    private Context context;
    private List<Recipe> recipes;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemRecipeBinding itemRecipeBinding = ItemRecipeBinding.inflate(layoutInflater, parent, false);
        return new ViewHolder(itemRecipeBinding);
    }

    public RecipeAdapter(Context context, List<Recipe> recipes) {
        this.context = context;
        this.recipes = recipes;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.bind(recipe);
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public void replaceAll(List<Recipe> filteredRecipes) {
        recipes = filteredRecipes;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cvRecipe;
        private TextView tvName;
        private TextView tvStatus;
        private ImageView ivImage;
        private RatingBar ratingBar;
        private TextView tvCalories;

        public ViewHolder(@NonNull ItemRecipeBinding itemRecipeBinding) {
            super(itemRecipeBinding.getRoot());
            tvName = itemRecipeBinding.tvName;
            tvStatus = itemRecipeBinding.tvStatus;
            ivImage = itemRecipeBinding.ivImage;
            cvRecipe = itemRecipeBinding.cvRecipe;
            ratingBar = itemRecipeBinding.ratingBar;
            tvCalories = itemRecipeBinding.tvCalories;
        }

        public void bind(final Recipe recipe) {
            tvName.setText(recipe.getName());
            GlideHelper.loadImage(recipe.getImageUrl(), context, ivImage);
            cvRecipe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Recipe recipe = recipes.get(getAdapterPosition());
                    if (CurrentRecipes.containsRecipe(recipe)) {
                        goToCurrentRecipeDetail(recipe);
                    } else {
                        goToNewRecipeDetail(recipe);
                    }
                }
            });
            if (recipe.isCookable()) {
                handleCookableRecipe();
            } else {
                handleUncookableRecipe();
            }
            ratingBar.setRating(recipe.getNumericRating());
            tvCalories.setText(recipe.getNutrition().getCalories() + " kcal");
        }

        private void goToCurrentRecipeDetail(Recipe recipe) {
            CurrentRecipeDetailFragment currentRecipeDetailFragment = CurrentRecipeDetailFragment.newInstance(Parcels.wrap(recipe));
            MainActivity.switchFragment(currentRecipeDetailFragment);
        }

        private void goToNewRecipeDetail(Recipe recipe) {
            NewRecipeDetailFragment newRecipeDetailFragment = NewRecipeDetailFragment.newInstance(Parcels.wrap(recipe));
            MainActivity.switchFragment(newRecipeDetailFragment);
        }

        private void handleCookableRecipe() {
            tvStatus.setText("You have enough ingredient to cook this recipe!");
            cvRecipe.setCardBackgroundColor(context.getResources().getColor(R.color.available));
        }

        private void handleUncookableRecipe() {
            tvStatus.setText("A few ingredients are still needed");
            cvRecipe.setCardBackgroundColor(context.getResources().getColor(R.color.unavailable));
        }
    }
}