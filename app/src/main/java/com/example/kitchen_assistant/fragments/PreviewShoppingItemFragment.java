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
import com.example.kitchen_assistant.helpers.MatchingHelper;
import com.example.kitchen_assistant.helpers.SpinnerHelper;
import com.example.kitchen_assistant.models.FoodItem;
import com.example.kitchen_assistant.models.Product;
import com.example.kitchen_assistant.models.ShoppingItem;
import com.example.kitchen_assistant.storage.CurrentShoppingList;
import com.parse.ParseUser;

import org.parceler.Parcels;

public class PreviewShoppingItemFragment extends DialogFragment {

    private static final String NAME_KEY = "NAME";
    private static final String QUANTITY_KEY = "QUANTITY_KEY";
    private static final String UNIT_KEY = "UNIT_KEY";

    private static final String TAG = "PreviewShoppingItem";

    private FragmentPreviewShoppingItemBinding fragmentPreviewShoppingItemBinding;
    private EditText etName;
    private EditText etQuantity;
    private Spinner spinnerQuantityUnit;
    private Button btAdd;
    private String name;
    private float quantity;
    private String quantityUnit;

    public PreviewShoppingItemFragment() {
    }

    // Initialize with a product to extract & display its food type
    public static PreviewShoppingItemFragment newInstance(String name, float quantity, String quantityUnit) {
        PreviewShoppingItemFragment fragment = new PreviewShoppingItemFragment();
        Bundle args = new Bundle();
        args.putString(NAME_KEY, name);
        args.putString(UNIT_KEY, quantityUnit);
        args.putFloat(QUANTITY_KEY, quantity);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            name = getArguments().getString(NAME_KEY);
            quantity = getArguments().getFloat(QUANTITY_KEY);
            quantityUnit = getArguments().getString(UNIT_KEY);
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

        etName.setText(name);

        etQuantity.setText(String.valueOf(quantity));

        SpinnerHelper.setUpMetricSpinner(spinnerQuantityUnit, quantityUnit, getContext(), etQuantity, quantity, spinnerQuantityUnit);

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

        MatchingHelper.attemptToCreateShoppingItem(itemName, quantity, quantityUnit);
    }

    private void goToCurrentShoppingList() {
        MainActivity.bottomNavigation.setSelectedItemId(R.id.miShoppingList);
    }
}