package com.example.kitchen_assistant.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.kitchen_assistant.R;
import com.example.kitchen_assistant.clients.OpenFoodFacts;
import com.example.kitchen_assistant.databinding.FragmentNewProductDetailBinding;
import com.example.kitchen_assistant.models.Product;

import org.parceler.Parcels;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewProductDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewProductDetailFragment extends Fragment {

    private static final String PRODUCT = "Product";
    private static final String TAG = "NewProductDetail";
    private FragmentNewProductDetailBinding fragmentNewProductDetailBinding;
    private Product product;
    private TextView tvName;
    private EditText etFoodType;
    private EditText etOriginalQuantity;
    private EditText etCurrentQuantity;
    private EditText etPurchaseDate;
    private EditText etDuration;
    private EditText etExpirationDate;

    public NewProductDetailFragment() {
    }

    public static NewProductDetailFragment newInstance(Parcelable product) {
        NewProductDetailFragment fragment = new NewProductDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(PRODUCT, product);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            product = (Product) Parcels.unwrap(getArguments().getParcelable(PRODUCT));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentNewProductDetailBinding = FragmentNewProductDetailBinding.inflate(getLayoutInflater());
        tvName = fragmentNewProductDetailBinding.tvName;
        etFoodType = fragmentNewProductDetailBinding.etFoodType;
        etOriginalQuantity = fragmentNewProductDetailBinding.etOriginalQuantity;
        etCurrentQuantity = fragmentNewProductDetailBinding.etQuantity;
        etPurchaseDate = fragmentNewProductDetailBinding.etPurchaseDate;
        etDuration = fragmentNewProductDetailBinding.etDuration;
        etExpirationDate = fragmentNewProductDetailBinding.etExpirationDate;

        Log.e(TAG, "START BINDING VIEW");
        tvName.setText(product.getProductName());
        etFoodType.setText("Undefined");
        etOriginalQuantity.setText(String.valueOf(product.getQuantity()));
        etCurrentQuantity.setText(String.valueOf(product.getQuantity()));
        etPurchaseDate.setText(String.valueOf(product.getPurchaseDate()));
        etDuration.setText("Undefined");
        etExpirationDate.setText("Undefined");

       return fragmentNewProductDetailBinding.getRoot();
    }
}