package com.example.kitchen_assistant.fragments;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.kitchen_assistant.R;
import com.example.kitchen_assistant.activities.MainActivity;
import com.example.kitchen_assistant.databinding.FragmentPreviewShoppingItemBinding;
import com.example.kitchen_assistant.helpers.SpinnerHelper;
import com.example.kitchen_assistant.models.FoodItem;
import com.example.kitchen_assistant.models.Product;
import com.example.kitchen_assistant.models.ShoppingItem;
import com.example.kitchen_assistant.storage.CurrentShoppingList;
import com.parse.ParseUser;

import org.parceler.Parcels;

public class PreviewShoppingItemFragment extends DialogFragment {

    private static final String PRODUCT_KEY = "Product";
    private static final String TAG = "PreviewShoppingItem";

    private FragmentPreviewShoppingItemBinding fragmentPreviewShoppingItemBinding;
    private EditText etName;
    private EditText etQuantity;
    private Spinner spinnerQuantityUnit;
    private Button btAdd;
    private Product product;


    public PreviewShoppingItemFragment() {
    }

    // Initialize with a product to extract & display its food type
    public static PreviewShoppingItemFragment newInstance(Parcelable product) {
        PreviewShoppingItemFragment fragment = new PreviewShoppingItemFragment();
        Bundle args = new Bundle();
        args.putParcelable(PRODUCT_KEY, product);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            product = Parcels.unwrap(getArguments().getParcelable(PRODUCT_KEY));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentPreviewShoppingItemBinding = FragmentPreviewShoppingItemBinding.inflate(getLayoutInflater());
        etName = fragmentPreviewShoppingItemBinding.etName;
        etQuantity = fragmentPreviewShoppingItemBinding.etQuantity;
        spinnerQuantityUnit = fragmentPreviewShoppingItemBinding.spinnerQuantityUnit;
        btAdd = fragmentPreviewShoppingItemBinding.btAdd;

        try {
            etName.setText(product.getFoodItem().getName());
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
        }

        etQuantity.setText(String.valueOf(product.getOriginalQuantity()));

        SpinnerHelper.setUpMetricSpinner(spinnerQuantityUnit, product.getQuantityUnit(), getContext(), etQuantity, (float) product.getOriginalQuantity(), spinnerQuantityUnit);

        // Allow user to add modified item to shopping list via add button
        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveInfo();
                dismiss();
                goToCurrentShoppingList();
            }
        });

        return fragmentPreviewShoppingItemBinding.getRoot();
    }

    // Save information of modified item to shopping list
    private void saveInfo() {
        String itemName = etName.getText().toString();
        Float quantity = Float.parseFloat(etQuantity.getText().toString());
        String quantityUnit = spinnerQuantityUnit.getSelectedItem().toString();

        // TODO: Check if such item exists before creating a new one

        ShoppingItem shoppingItem = new ShoppingItem();
        shoppingItem.setName(itemName);
        shoppingItem.setQuantity(quantity);
        shoppingItem.setQuantityUnit(quantityUnit);
        shoppingItem.setOwner(ParseUser.getCurrentUser());

        CurrentShoppingList.addItem(shoppingItem);
    }

    private void goToCurrentShoppingList() {
        MainActivity.bottomNavigation.setSelectedItemId(R.id.miShoppingList);
    }
}