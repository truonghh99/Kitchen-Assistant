package com.example.kitchen_assistant.fragments.recipes;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.Toast;

import com.example.kitchen_assistant.adapters.AlternativeAdapter;
import com.example.kitchen_assistant.databinding.FragmentAlternativeOptionsBinding;
import com.example.kitchen_assistant.helpers.MatchingHelper;
import com.example.kitchen_assistant.models.Ingredient;
import com.example.kitchen_assistant.models.Product;
import com.example.kitchen_assistant.storage.CurrentProducts;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class AlternativeOptionsFragment extends DialogFragment {

    private static final String NAME_KEY = "NAME";
    private static final String QUANTITY_KEY = "QUANTITY_KEY";
    private static final String UNIT_KEY = "UNIT_KEY";
    private static final String INGREDIENT_KEY = "INGREDIENT_KEY";

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
    private Button btBuy;
    private Ingredient ingredient;
    private EditText etSearch;

    public AlternativeOptionsFragment() {
    }

    // Initialize with a product to extract & display its food type
    public static AlternativeOptionsFragment newInstance(Parcelable ingredient) {
        AlternativeOptionsFragment fragment = new AlternativeOptionsFragment();
        Bundle args = new Bundle();
        args.putParcelable(INGREDIENT_KEY, ingredient);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ingredient = Parcels.unwrap(getArguments().getParcelable(INGREDIENT_KEY));
            name = ingredient.getName();
            quantity = ingredient.getQuantity();
            quantityUnit = ingredient.getQuantityUnit();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentAlternativeOptionsBinding = FragmentAlternativeOptionsBinding.inflate(getLayoutInflater());
        rvAlternatives = fragmentAlternativeOptionsBinding.rvAlternatives;
        ivLeft = fragmentAlternativeOptionsBinding.ivLeft;
        ivRight = fragmentAlternativeOptionsBinding.ivRight;
        btBuy = fragmentAlternativeOptionsBinding.btBuy;
        etSearch = fragmentAlternativeOptionsBinding.etSearch;

        products = CurrentProducts.products;
        adapter = new AlternativeAdapter(getActivity(), products, ingredient, getDialog());
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

        btBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MatchingHelper.attemptToCreateShoppingItem(name, quantity, quantityUnit);
                Toast.makeText(getContext(), "Added to your shoppling list: " + quantity + quantityUnit + " of " + name, Toast.LENGTH_SHORT).show();
                CurrentRecipeDetailFragment.notifyChange();
                dismiss();
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence query, int start, int before, int count) {
                List<Product> filteredProducts = filter(products, query.toString());
                adapter.replaceAll(filteredProducts);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return fragmentAlternativeOptionsBinding.getRoot();
    }

    private List<Product> filter(List<Product> products, String query) {
        final String lowerCaseQuery = query.toLowerCase();
        final List<Product> filteredModelList = new ArrayList<>();
        for (Product product : products) {
            final String text = product.getProductName().toLowerCase();
            if (text.contains(lowerCaseQuery)) {
                filteredModelList.add(product);
            }
        }
        return filteredModelList;
    }
}