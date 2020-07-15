package com.example.kitchen_assistant.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kitchen_assistant.databinding.ItemRecipeBinding;
import com.example.kitchen_assistant.helpers.GlideHelper;
import com.example.kitchen_assistant.models.Recipe;

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
        }

        public void bind(final Recipe recipe) {
            tvName.setText(recipe.getName());
            GlideHelper.loadImage(recipe.getImageUrl(), context, ivImage);
        }
    }

}