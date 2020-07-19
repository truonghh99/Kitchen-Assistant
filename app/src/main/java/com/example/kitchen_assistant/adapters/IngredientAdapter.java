package com.example.kitchen_assistant.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kitchen_assistant.R;
import com.example.kitchen_assistant.databinding.ItemIngredientBinding;
import com.example.kitchen_assistant.fragments.AlternativeOptionsFragment;
import com.example.kitchen_assistant.fragments.PreviewShoppingItemFragment;
import com.example.kitchen_assistant.helpers.RecipeEvaluator;
import com.example.kitchen_assistant.models.FoodItem;
import com.example.kitchen_assistant.models.Ingredient;
import com.example.kitchen_assistant.models.Recipe;
import com.example.kitchen_assistant.storage.CurrentFoodTypes;
import com.example.kitchen_assistant.storage.CurrentProducts;
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
        private TextView tvStatus;

        public ViewHolder(@NonNull ItemIngredientBinding itemIngredientBinding) {
            super(itemIngredientBinding.getRoot());
            tvName = itemIngredientBinding.tvName;
            tvQuantity = itemIngredientBinding.tvQuantity;
            cvIngredient = itemIngredientBinding.cvIngredient;
            tvStatus = itemIngredientBinding.tvStatus;
        }

        public void bind(final Ingredient ingredient) {
            tvName.setText(ingredient.getName());
            tvQuantity.setText("" + ingredient.getQuantity() + " " + ingredient.getQuantityUnit());

            if (ingredient.isAvailable()) {
                handleAvailableProduct(ingredient);
            } else {
                if (RecipeEvaluator.ingredientIsInCart(ingredient)) {
                    handleInCartProduct(ingredient);
                } else {
                    handleUnavailableProduct(ingredient);
                }
            }

            cvIngredient.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goToAlternativeOptions(ingredient);
                }
            });
        }

        private void goToAlternativeOptions(Ingredient ingredient) {
            DialogFragment dialogFragment = AlternativeOptionsFragment.newInstance(ingredient.getName(), ingredient.getQuantity(), ingredient.getQuantityUnit());
            dialogFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), "Dialog");
        }

        private void handleAvailableProduct(Ingredient ingredient) {
            cvIngredient.setCardBackgroundColor(context.getResources().getColor(R.color.available));
            tvStatus.setText("Using your " + CurrentProducts.getNameWithCode(ingredient.getPreferredProduct()));
        }

        private void handleInCartProduct(Ingredient ingredient) {
            cvIngredient.setCardBackgroundColor(context.getResources().getColor(R.color.inCart));
            tvStatus.setText("Already in your shopping list");
        }

        private void handleUnavailableProduct(Ingredient ingredient) {
            cvIngredient.setCardBackgroundColor(context.getResources().getColor(R.color.unavailable));
            tvStatus.setText("You don't have this ingredient");
        }
    }

}