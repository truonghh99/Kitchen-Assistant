package com.example.kitchen_assistant.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kitchen_assistant.databinding.ItemShoppingBinding;
import com.example.kitchen_assistant.models.ShoppingItem;
import com.parse.ParseException;

import java.util.List;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ViewHolder> {

    private final String TAG = "ShoppingListAdapter";
    private Context context;
    private List<ShoppingItem> items;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemShoppingBinding itemShoppingBinding = ItemShoppingBinding.inflate(layoutInflater, parent, false);
        return new ViewHolder(itemShoppingBinding);
    }

    public ShoppingListAdapter(Context context, List<ShoppingItem> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ShoppingItem item = items.get(position);
        try {
            holder.bind(item);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cvShoppingItem;
        private TextView tvName;
        private TextView tvQuantity;
        private ItemShoppingBinding itemShoppingBinding;
        private CheckBox cbCheckbox;

        public ViewHolder(@NonNull ItemShoppingBinding itemShoppingBinding) {
            super(itemShoppingBinding.getRoot());
            this.itemShoppingBinding = itemShoppingBinding;
            tvName = itemShoppingBinding.tvName;
            tvQuantity = itemShoppingBinding.tvQuantity;
            cbCheckbox = itemShoppingBinding.cbCheckbox;
        }

        public void bind(final ShoppingItem item) throws ParseException {
            tvName.setText(item.getName());
            tvQuantity.setText(String.valueOf(item.getQuantity()) + " " + item.getQuantityUnit());
        }
    }

}