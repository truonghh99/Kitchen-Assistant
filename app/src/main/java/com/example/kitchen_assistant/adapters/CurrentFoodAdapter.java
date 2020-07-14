package com.example.kitchen_assistant.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kitchen_assistant.R;
import com.example.kitchen_assistant.activities.MainActivity;
import com.example.kitchen_assistant.databinding.ItemProductBinding;
import com.example.kitchen_assistant.fragments.CurrentProductDetailFragment;
import com.example.kitchen_assistant.fragments.NewProductDetailFragment;
import com.example.kitchen_assistant.models.Product;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class CurrentFoodAdapter extends RecyclerView.Adapter<CurrentFoodAdapter.ViewHolder> {

    public interface OnClickListener {
        void onClickListener(int position);
    }

    private final String TAG = "CurrentFoodAdapter";
    private Context context;
    private List<Product> products;

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

    // Clean all elements of the recycler
    public void clear() {
        products.clear();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Product> list) {
        products.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cvProduct;
        private TextView tvName;
        private TextView tvQuantity;
        private TextView tvExpirationDate;
        private ItemProductBinding itemProductBinding;
        private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

        public ViewHolder(@NonNull ItemProductBinding itemProductBinding) {
            super(itemProductBinding.getRoot());
            this.itemProductBinding = itemProductBinding;
            tvName = itemProductBinding.tvName;
            tvQuantity = itemProductBinding.tvQuantity;
            tvExpirationDate = itemProductBinding.tvExpirationDate;
            cvProduct = itemProductBinding.cvProduct;
        }

        public void bind(final Product product) {
            tvName.setText(product.getProductName());
            tvQuantity.setText("" + product.getCurrentQuantity() + " " + product.getQuantityUnit());
            tvExpirationDate.setText("Expiration date: " + NewProductDetailFragment.parseDate(product.getExpirationDate(), NewProductDetailFragment.DATE_FORMAT));
            cvProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goToCurrentProductDetail(products.get(getAdapterPosition()));
                }
            });
            Log.e(TAG, product.getFoodStatus());
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
            MainActivity.hideProgressBar();
        }
    }

    private void goToCurrentProductDetail(Product product) {
        Log.e(TAG, "Go to current product detail");
        Fragment currentFoodDetailFragment = CurrentProductDetailFragment.newInstance(Parcels.wrap(product));
        MainActivity.switchFragment(currentFoodDetailFragment);
    }

}