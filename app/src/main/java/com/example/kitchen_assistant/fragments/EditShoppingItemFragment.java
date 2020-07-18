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
import com.example.kitchen_assistant.databinding.FragmentEditShoppingItemBinding;
import com.example.kitchen_assistant.databinding.FragmentPreviewShoppingItemBinding;
import com.example.kitchen_assistant.helpers.MatchingHelper;
import com.example.kitchen_assistant.helpers.SpinnerHelper;
import com.example.kitchen_assistant.models.FoodItem;
import com.example.kitchen_assistant.models.Product;
import com.example.kitchen_assistant.models.ShoppingItem;
import com.example.kitchen_assistant.storage.CurrentShoppingList;
import com.parse.ParseUser;

import org.parceler.Parcels;

public class EditShoppingItemFragment extends DialogFragment {

    private static final String PRODUCT_KEY = "Product";
    private static final String TAG = "EditShoppingItem";

    private FragmentEditShoppingItemBinding fragmentEditShoppingItemBinding;
    private EditText etName;
    private EditText etQuantity;
    private Spinner spinnerQuantityUnit;
    private Button btUpdate;
    private Product product;


    public EditShoppingItemFragment() {
    }

    // Initialize with a product to extract & display its food type
    public static EditShoppingItemFragment newInstance(Parcelable product) {
        EditShoppingItemFragment fragment = new EditShoppingItemFragment();
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
        fragmentEditShoppingItemBinding = FragmentEditShoppingItemBinding.inflate(getLayoutInflater());
        etName = fragmentEditShoppingItemBinding.etName;
        etQuantity = fragmentEditShoppingItemBinding.etQuantity;
        spinnerQuantityUnit = fragmentEditShoppingItemBinding.spinnerQuantityUnit;
        btUpdate = fragmentEditShoppingItemBinding.btUpdate;

        etName.setText(product.getFoodItem().getName());

        etQuantity.setText(String.valueOf(product.getOriginalQuantity()));

        SpinnerHelper.setUpMetricSpinner(spinnerQuantityUnit, product.getQuantityUnit(), getContext(), etQuantity, (float) product.getOriginalQuantity(), spinnerQuantityUnit);

        // Allow user to add modified item to shopping list via add button
        btUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveInfo();
                dismiss();
                goToCurrentShoppingList();
            }
        });

        return fragmentEditShoppingItemBinding.getRoot();
    }

    // Save information of modified item to shopping list
    private void saveInfo() {
        String itemName = etName.getText().toString();
        Float quantity = Float.parseFloat(etQuantity.getText().toString());
        String quantityUnit = spinnerQuantityUnit.getSelectedItem().toString();

        MatchingHelper.attemptToCreateShoppingItem(itemName, quantity, quantityUnit);
    }

    private void goToCurrentShoppingList() {
        MainActivity.bottomNavigation.setSelectedItemId(R.id.miShoppingList);
    }
}