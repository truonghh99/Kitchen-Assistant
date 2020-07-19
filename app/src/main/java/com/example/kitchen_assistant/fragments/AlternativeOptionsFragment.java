package com.example.kitchen_assistant.fragments;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.kitchen_assistant.R;
import com.example.kitchen_assistant.activities.MainActivity;
import com.example.kitchen_assistant.adapters.AlternativeAdapter;
import com.example.kitchen_assistant.adapters.CurrentFoodAdapter;
import com.example.kitchen_assistant.databinding.FragmentAlternativeOptionsBinding;
import com.example.kitchen_assistant.databinding.FragmentPreviewShoppingItemBinding;
import com.example.kitchen_assistant.helpers.MatchingHelper;
import com.example.kitchen_assistant.helpers.SpinnerHelper;
import com.example.kitchen_assistant.models.FoodItem;
import com.example.kitchen_assistant.models.Product;
import com.example.kitchen_assistant.models.ShoppingItem;
import com.example.kitchen_assistant.storage.CurrentProducts;
import com.example.kitchen_assistant.storage.CurrentShoppingList;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.List;

public class AlternativeOptionsFragment extends DialogFragment {

    private static final String NAME_KEY = "NAME";
    private static final String QUANTITY_KEY = "QUANTITY_KEY";
    private static final String UNIT_KEY = "UNIT_KEY";

    private static final String TAG = "AlternativeOptionsFragment";

    private FragmentAlternativeOptionsBinding fragmentAlternativeOptionsBinding;
    private String name;
    private float quantity;
    private String quantityUnit;
    private RecyclerView rvAlternatives;
    private AlternativeAdapter adapter;
    private List<Product> products;

    public AlternativeOptionsFragment() {
    }

    // Initialize with a product to extract & display its food type
    public static AlternativeOptionsFragment newInstance(String name, float quantity, String quantityUnit) {
        AlternativeOptionsFragment fragment = new AlternativeOptionsFragment();
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
        fragmentAlternativeOptionsBinding = FragmentAlternativeOptionsBinding.inflate(getLayoutInflater());
        rvAlternatives = fragmentAlternativeOptionsBinding.rvAlternatives;

        products = CurrentProducts.products;
        adapter = new AlternativeAdapter(getActivity(), products);
        rvAlternatives.setLayoutManager(new GridLayoutManager(getActivity(), 2, GridLayoutManager.HORIZONTAL, false));
        rvAlternatives.setAdapter(adapter);

        return fragmentAlternativeOptionsBinding.getRoot();
    }
}