package com.example.kitchen_assistant.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cvRecipe;
        private TextView tvName;
        private TextView tvStatus;
        private ItemRecipeBinding itemRecipeBinding;
        private ImageView ivImage;

        public ViewHolder(@NonNull ItemRecipeBinding itemRecipeBinding) {
            super(itemRecipeBinding.getRoot());
            this.itemRecipeBinding = itemRecipeBinding;
            tvName = itemRecipeBinding.tvName;
            tvStatus = itemRecipeBinding.tvStatus;
            ivImage = itemRecipeBinding.ivImage;
            cvRecipe = itemRecipeBinding.cvRecipe;
        }

        public void bind(final Recipe recipe) {
            tvName.setText(recipe.getName());
            GlideHelper.loadImage(recipe.getImageUrl(), context, ivImage);
            cvRecipe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO: GO TO DETAIL
                    Recipe recipe = recipes.get(getAdapterPosition());
                    if (CurrentRecipes.recipeHashMap.containsKey(recipe.getCode())) {
                        CurrentRecipeDetailFragment currentRecipeDetailFragment = CurrentRecipeDetailFragment.newInstance(Parcels.wrap(recipe));
                        MainActivity.switchFragmentWithTransition(currentRecipeDetailFragment, CurrentRecipeDetailFragment.title, ivImage, "image");
                    } else {
                        NewRecipeDetailFragment newRecipeDetailFragment = NewRecipeDetailFragment.newInstance(Parcels.wrap(recipe));
                        MainActivity.switchFragmentWithTransition(newRecipeDetailFragment, NewRecipeDetailFragment.title, ivImage, "image");
                    }
                }
            });
            if (recipe.isCookable()) {
                tvStatus.setText("You have enough ingredient to cook this recipe!");
                cvRecipe.setCardBackgroundColor(context.getResources().getColor(R.color.available));
            } else {
                tvStatus.setText("A few ingredients are still needed");
                cvRecipe.setCardBackgroundColor(context.getResources().getColor(R.color.unavailable));
            }
        }
    }

}