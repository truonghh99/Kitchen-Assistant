package com.example.kitchen_assistant.adapters;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kitchen_assistant.R;
import com.example.kitchen_assistant.databinding.ItemAlternativeBinding;
import com.example.kitchen_assistant.fragments.recipes.CurrentRecipeDetailFragment;
import com.example.kitchen_assistant.fragments.recipes.NewRecipeDetailFragment;
import com.example.kitchen_assistant.helpers.RecipeEvaluator;
import com.example.kitchen_assistant.models.Ingredient;
import com.example.kitchen_assistant.models.Product;

import java.util.List;

public class AlternativeAdapter extends RecyclerView.Adapter<AlternativeAdapter.ViewHolder> {

    private static final String TAG = "AlternativeAdapter";
    private Context context;
    private List<Product> products;
    private Ingredient ingredient;
    private Dialog dialog;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemAlternativeBinding itemAlternativeBinding = ItemAlternativeBinding.inflate(layoutInflater, parent, false);
        return new ViewHolder(itemAlternativeBinding);
    }

    public AlternativeAdapter(Context context, List<Product> products, Ingredient ingredient, Dialog dialog) {
        this.context = context;
        this.products = products;
        this.ingredient = ingredient;
        this.dialog = dialog;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);
        holder.bind(product);
    }

    public void replaceAll(List<Product> filteredProducts) {
        products = filteredProducts;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cvProduct;
        private TextView tvName;
        private TextView tvQuantity;

        public ViewHolder(@NonNull ItemAlternativeBinding itemAlternativeBinding) {
            super(itemAlternativeBinding.getRoot());
            tvName = itemAlternativeBinding.tvName;
            tvQuantity = itemAlternativeBinding.tvQuantity;
            cvProduct = itemAlternativeBinding.cvProduct;
        }

        public void bind(final Product product) {
            tvName.setText(product.getProductName());
            tvQuantity.setText("" + product.getCurrentQuantity() + " " + product.getQuantityUnit());

            // Change card background to indicate current status of products
            switch (product.getFoodStatus()) {
                case Product.STATUS_BEST:
                    cvProduct.setCardBackgroundColor(context.getResources().getColor(R.color.best));
                    break;
                case Product.STATUS_SAFE:
                    cvProduct.setCardBackgroundColor(context.getResources().getColor(R.color.fair));
                    break;
                case Product.STATUS_BAD:
                    cvProduct.setCardBackgroundColor(context.getResources().getColor(R.color.bad));
                    break;
            }

            cvProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ingredient.setPreferredProduct(product.getProductCode());
                    RecipeEvaluator.evaluateRecipe(ingredient.getRecipe());
                    CurrentRecipeDetailFragment.notifyChange();
                    NewRecipeDetailFragment.notifyChange();
                    ingredient.saveInfo();
                    dialog.dismiss();
                }
            });
        }
    }

}