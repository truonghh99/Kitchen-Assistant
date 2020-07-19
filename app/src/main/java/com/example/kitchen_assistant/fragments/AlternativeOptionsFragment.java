package com.example.kitchen_assistant.fragments;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

    private static final String TAG = "AlternativeOptions";

    private FragmentAlternativeOptionsBinding fragmentAlternativeOptionsBinding;
    private String name;
    private float quantity;
    private String quantityUnit;
    private RecyclerView rvAlternatives;
    private AlternativeAdapter adapter;
    private List<Product> products;
    private ImageView ivLeft;
    private ImageView ivRight;
    private GridLayoutManager layoutManager;

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
        ivLeft = fragmentAlternativeOptionsBinding.ivLeft;
        ivRight = fragmentAlternativeOptionsBinding.ivRight;

        products = CurrentProducts.products;
        adapter = new AlternativeAdapter(getActivity(), products);
        layoutManager = new GridLayoutManager(getActivity(), 2, GridLayoutManager.HORIZONTAL, false);
        rvAlternatives.setLayoutManager(layoutManager);
        rvAlternatives.setAdapter(adapter);

        ivRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rvAlternatives.smoothScrollToPosition(layoutManager.findLastVisibleItemPosition() + 1);
                if (layoutManager.findLastVisibleItemPosition() >= products.size() - 3) {
                    ivRight.setVisibility(View.INVISIBLE);
                }
                ivLeft.setVisibility(View.VISIBLE);
            }
        });

        ivLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rvAlternatives.smoothScrollToPosition(layoutManager.findFirstVisibleItemPosition() - 1);
                Log.e(TAG, String.valueOf(layoutManager.findFirstVisibleItemPosition()));
                if (layoutManager.findFirstVisibleItemPosition() <= 2) {
                    ivLeft.setVisibility(View.INVISIBLE);
                }
                ivRight.setVisibility(View.VISIBLE);
            }
        });

        return fragmentAlternativeOptionsBinding.getRoot();
    }
}