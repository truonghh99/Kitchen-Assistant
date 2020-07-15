package com.example.kitchen_assistant.fragments;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.kitchen_assistant.R;
import com.example.kitchen_assistant.activities.MainActivity;
import com.example.kitchen_assistant.adapters.ShoppingListAdapter;
import com.example.kitchen_assistant.clients.Spoonacular;
import com.example.kitchen_assistant.databinding.FragmentCurrentFoodDetailBinding;
import com.example.kitchen_assistant.databinding.FragmentPreviewShoppingItemBinding;
import com.example.kitchen_assistant.helpers.GlideHelper;
import com.example.kitchen_assistant.helpers.SpinnerHelper;
import com.example.kitchen_assistant.models.FoodItem;
import com.example.kitchen_assistant.models.Product;
import com.example.kitchen_assistant.models.Recipe;
import com.example.kitchen_assistant.models.ShoppingItem;
import com.example.kitchen_assistant.storage.CurrentProducts;
import com.example.kitchen_assistant.storage.CurrentRecipes;
import com.example.kitchen_assistant.storage.CurrentShoppingList;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.sql.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewProductDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PreviewShoppingItemFragment extends DialogFragment {

    private static final String ITEM = "Product";
    private static final String TAG = "PreviewShoppingItem";
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

    private FragmentPreviewShoppingItemBinding fragmentPreviewShoppingItemBinding;
    private EditText etName;
    private EditText etQuantity;
    private Spinner spinnerQuantityUnit;
    private Button btAdd;
    private ShoppingItem item;


    public PreviewShoppingItemFragment() {
    }

    public static PreviewShoppingItemFragment newInstance(Parcelable item) {
        PreviewShoppingItemFragment fragment = new PreviewShoppingItemFragment();
        Bundle args = new Bundle();
        args.putParcelable(ITEM, item);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            FoodItem foodItem = (FoodItem) Parcels.unwrap(getArguments().getParcelable(ITEM));
            item = new ShoppingItem();
            try {
                item.setName(foodItem.getName());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            item.setQuantity(foodItem.getquantity());
            item.setQuantityUnit(foodItem.getQuantityUnit());

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

        Log.e(TAG, "START BINDING VIEW");

        try {
            etName.setText(item.getName());
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
        }

        etQuantity.setText(String.valueOf(item.getQuantity()));

        SpinnerHelper.setUpMetricSpinner(spinnerQuantityUnit, item.getQuantityUnit(), getContext(), etQuantity, (float) item.getQuantity(), spinnerQuantityUnit);


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


    private void saveInfo() {
        String itemName = etName.getText().toString();
        Float quantity = Float.parseFloat(etQuantity.getText().toString());
        String quantityUnit = spinnerQuantityUnit.getSelectedItem().toString();


        item.setName(itemName);
        item.setQuantity(quantity);
        item.setQuantityUnit(quantityUnit);

        // TODO: Check if such item exists before creating a new one

        ShoppingItem shoppingItem = new ShoppingItem();
        shoppingItem.setName(itemName);
        shoppingItem.setQuantity(quantity);
        shoppingItem.setQuantityUnit(quantityUnit);
        shoppingItem.setOwner(ParseUser.getCurrentUser());

        CurrentShoppingList.addItem(item);
    }

    private void goToCurrentShoppingList() {
        MainActivity.bottomNavigation.setSelectedItemId(R.id.miShoppingList);
    }
}