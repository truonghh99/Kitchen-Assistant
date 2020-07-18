package com.example.kitchen_assistant.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kitchen_assistant.R;
import com.example.kitchen_assistant.databinding.ItemIngredientBinding;
import com.example.kitchen_assistant.helpers.RecipeEvaluator;
import com.example.kitchen_assistant.models.Ingredient;
import com.example.kitchen_assistant.models.Recipe;
import com.example.kitchen_assistant.storage.CurrentShoppingList;

import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.ViewHolder> {

    private static final String TAG = "IngredientAdapter";
    private Context context;
    private List<Ingredient> ingredients;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemIngredientBinding itemIngredientBinding = ItemIngredientBinding.inflate(layoutInflater, parent, false);
        return new ViewHolder(itemIngredientBinding);
    }

    public IngredientAdapter(Context context, List<Ingredient> ingredients) {
        this.context = context;
        this.ingredients = ingredients;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ingredient ingredient = ingredients.get(position);
        holder.bind(ingredient);
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cvIngredient;
        private TextView tvName;
        private TextView tvQuantity;

        public ViewHolder(@NonNull ItemIngredientBinding itemIngredientBinding) {
            super(itemIngredientBinding.getRoot());
            tvName = itemIngredientBinding.tvName;
            tvQuantity = itemIngredientBinding.tvQuantity;
            cvIngredient = itemIngredientBinding.cvIngredient;
        }

        public void bind(final Ingredient ingredient) {
            tvName.setText(ingredient.getName());
            tvQuantity.setText("" + ingredient.getQuantity() + " " + ingredient.getQuantityUnit());
            // Change card background to indicate current status of products
            if (ingredient.isAvailable()) {
                cvIngredient.setCardBackgroundColor(context.getResources().getColor(R.color.available));
            } else {
                if (RecipeEvaluator.ingredientIsInCart(ingredient)) {
                    cvIngredient.setCardBackgroundColor(context.getResources().getColor(R.color.inCart)); //TODO: Check quantity
                } else {
                    cvIngredient.setCardBackgroundColor(context.getResources().getColor(R.color.unavailable));
                }
            }
        }
    }

}