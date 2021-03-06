package com.example.kitchen_assistant.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kitchen_assistant.R;
import com.example.kitchen_assistant.activities.MainActivity;
import com.example.kitchen_assistant.databinding.ItemProductBinding;
import com.example.kitchen_assistant.fragments.products.CurrentFoodDetailFragment;
import com.example.kitchen_assistant.fragments.products.NewProductDetailFragment;
import com.example.kitchen_assistant.helpers.GlideHelper;
import com.example.kitchen_assistant.models.Product;

import org.parceler.Parcels;

import java.util.List;

public class CurrentFoodAdapter extends RecyclerView.Adapter<CurrentFoodAdapter.ViewHolder> {

    private static final String TAG = "CurrentFoodAdapter";
    private Context context;
    public static List<Product> products;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemProductBinding itemProductBinding = ItemProductBinding.inflate(layoutInflater, parent, false);
        return new ViewHolder(itemProductBinding);
    }

    public CurrentFoodAdapter(Context context, List<Product> products) {
        this.context = context;
        this.products = products;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void replaceAll(List<Product> filteredProducts) {
        products = filteredProducts;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cvProduct;
        private TextView tvName;
        private TextView tvQuantity;
        private ImageView ivImage;

        public ViewHolder(@NonNull ItemProductBinding itemProductBinding) {
            super(itemProductBinding.getRoot());
            tvName = itemProductBinding.tvName;
            tvQuantity = itemProductBinding.tvQuantity;
            ivImage = itemProductBinding.ivImage;
            cvProduct = itemProductBinding.cvProduct;
        }

        public void bind(final Product product) {
            tvName.setText(product.getProductName());
            tvQuantity.setText("" + product.getCurrentQuantity() + " " + product.getQuantityUnit());
            cvProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goToCurrentProductDetail(products.get(getAdapterPosition()));
                }
            });

            // Change card background to indicate current status of products
            switch (product.getFoodStatus().toLowerCase()) {
                case Product.STATUS_BEST:
                    cvProduct.setCardBackgroundColor(context.getResources().getColor(R.color.best));
                    tvName.setTextColor(context.getResources().getColor(R.color.best_dark));
                    break;
                case Product.STATUS_SAFE:
                    cvProduct.setCardBackgroundColor(context.getResources().getColor(R.color.fair));
                    tvName.setTextColor(context.getResources().getColor(R.color.fair_dark));
                    break;
                case Product.STATUS_BAD:
                    cvProduct.setCardBackgroundColor(context.getResources().getColor(R.color.bad));
                    tvName.setTextColor(context.getResources().getColor(R.color.bad_dark));
                    break;
            }

            GlideHelper.loadAvatar(product.getImageUrl(), context, ivImage);
        }
    }

    // Allow user to view details of selected product by calling a child fragment via MainActivity
    public static void goToCurrentProductDetail(Product product) {
        Log.e(TAG, "Go to current product detail");
        Fragment currentFoodDetailFragment = CurrentFoodDetailFragment.newInstance(Parcels.wrap(product));
        MainActivity.switchFragment(currentFoodDetailFragment);
    }

}