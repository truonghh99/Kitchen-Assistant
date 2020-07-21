package com.example.kitchen_assistant.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kitchen_assistant.databinding.ItemShoppingBinding;
import com.example.kitchen_assistant.fragments.shopping.EditShoppingItemFragment;
import com.example.kitchen_assistant.models.Recipe;
import com.example.kitchen_assistant.models.ShoppingItem;
import com.example.kitchen_assistant.storage.CurrentShoppingList;
import com.parse.ParseException;

import org.parceler.Parcels;

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

    public void replaceAll(List<ShoppingItem> filteredItems) {
        items = filteredItems;
        notifyDataSetChanged();
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
            cvShoppingItem = itemShoppingBinding.cvShoppingItem;
            tvName = itemShoppingBinding.tvName;
            tvQuantity = itemShoppingBinding.tvQuantity;
            cbCheckbox = itemShoppingBinding.cbCheckbox;
        }

        public void bind(final ShoppingItem item) throws ParseException {
            tvName.setText(item.getName());
            tvQuantity.setText(String.valueOf(item.getQuantity()) + " " + item.getQuantityUnit());
            cbCheckbox.setChecked(items.get(getAdapterPosition()).getChecked());
            cbCheckbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(((CompoundButton) view).isChecked()){
                        items.get(getAdapterPosition()).setChecked(true);
                    } else {
                        items.get(getAdapterPosition()).setChecked(false);
                    }
                    CurrentShoppingList.saveItemInBackGround(item);
                }
            });
            cvShoppingItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DialogFragment dialogFragment = EditShoppingItemFragment.newInstance(Parcels.wrap(item));
                    dialogFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), "Dialog");
                }
            });

        }
    }

}