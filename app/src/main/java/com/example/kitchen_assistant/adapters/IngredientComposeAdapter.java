package com.example.kitchen_assistant.adapters;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kitchen_assistant.R;
import com.example.kitchen_assistant.databinding.ItemIngredientBinding;
import com.example.kitchen_assistant.databinding.ItemIngredientComposeBinding;
import com.example.kitchen_assistant.fragments.recipes.AlternativeOptionsFragment;
import com.example.kitchen_assistant.helpers.RecipeEvaluator;
import com.example.kitchen_assistant.models.Ingredient;
import com.example.kitchen_assistant.storage.CurrentProducts;

import org.parceler.Parcels;

import java.util.List;

public class IngredientComposeAdapter extends RecyclerView.Adapter<IngredientComposeAdapter.ViewHolder> {

    private static final String TAG = "IngredientComposeAdapte";
    private Context context;
    public List<Ingredient> ingredients;
    private RecyclerView rvIngredients;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemIngredientComposeBinding itemIngredientComposeBinding = ItemIngredientComposeBinding.inflate(layoutInflater, parent, false);
        return new ViewHolder(itemIngredientComposeBinding);
    }

    public IngredientComposeAdapter(Context context, List<Ingredient> ingredients, RecyclerView rvIngredients) {
        this.context = context;
        this.ingredients = ingredients;
        this.rvIngredients = rvIngredients;
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
        private EditText etName;
        private EditText etQuantity;
        private EditText etUnit;

        public ViewHolder(@NonNull ItemIngredientComposeBinding itemIngredientComposeBinding) {
            super(itemIngredientComposeBinding.getRoot());
            etName = itemIngredientComposeBinding.etName;
            etQuantity = itemIngredientComposeBinding.etQuantity;
            etUnit = itemIngredientComposeBinding.etUnit;
        }

        public void bind(final Ingredient ingredient) {
            etName.requestFocus();
            etUnit.setOnKeyListener(new View.OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        ingredient.setName(etName.getText().toString());
                        ingredient.setQuantity(Float.parseFloat(etQuantity.getText().toString()));
                        ingredient.setQuantityUnit(etUnit.getText().toString());
                        ingredients.add(ingredients.size(), new Ingredient());
                        notifyItemInserted(ingredients.size() - 1);
                        rvIngredients.smoothScrollToPosition(ingredients.size() - 1);
                        return true;
                    }
                    return false;
                }
            });
        }
    }
}