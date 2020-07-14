package com.example.kitchen_assistant.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.kitchen_assistant.R;
import com.example.kitchen_assistant.activities.MainActivity;
import com.example.kitchen_assistant.clients.OpenFoodFacts;
import com.example.kitchen_assistant.databinding.FragmentCurrentFoodDetailBinding;
import com.example.kitchen_assistant.helpers.GlideHelper;
import com.example.kitchen_assistant.helpers.MetricConversionHelper;
import com.example.kitchen_assistant.helpers.SpinnerHelper;
import com.example.kitchen_assistant.models.Product;
import com.example.kitchen_assistant.storage.CurrentProducts;

import org.parceler.Parcels;
import org.w3c.dom.Text;

import java.io.IOException;
import java.text.ParseException;
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
public class CurrentFoodDetailFragment extends Fragment {

    private static final String PRODUCT = "Product";
    private static final String TAG = "CurrentFoodDetail";
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

    private FragmentCurrentFoodDetailBinding fragmentCurrentFoodDetailBinding;
    private Product product;
    private TextView etName;
    private EditText etFoodType;
    private EditText etOriginalQuantity;
    private EditText etCurrentQuantity;
    private EditText etPurchaseDate;
    private EditText etDuration;
    private EditText etExpirationDate;
    private ImageView ivImg;
    private Spinner spinnerOriginalQuantityUnit;
    private Spinner spinnerCurrentQuantityUnit;
    private Spinner spinnerDurationUnit;
    private Spinner spinnerStatus;
    private EditText etNumProducts;


    public CurrentFoodDetailFragment() {
    }

    public static CurrentFoodDetailFragment newInstance(Parcelable product) {
        CurrentFoodDetailFragment fragment = new CurrentFoodDetailFragment();
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
        fragmentCurrentFoodDetailBinding = FragmentCurrentFoodDetailBinding.inflate(getLayoutInflater());
        etName = fragmentCurrentFoodDetailBinding.etName;
        etFoodType = fragmentCurrentFoodDetailBinding.etFoodType;
        etOriginalQuantity = fragmentCurrentFoodDetailBinding.etOriginalQuantity;
        etCurrentQuantity = fragmentCurrentFoodDetailBinding.etQuantity;
        etPurchaseDate = fragmentCurrentFoodDetailBinding.etPurchaseDate;
        etDuration = fragmentCurrentFoodDetailBinding.etDuration;
        etExpirationDate = fragmentCurrentFoodDetailBinding.etExpirationDate;
        ivImg = fragmentCurrentFoodDetailBinding.ivImg;
        spinnerCurrentQuantityUnit = fragmentCurrentFoodDetailBinding.spinnerQuantityUnit;
        spinnerOriginalQuantityUnit = fragmentCurrentFoodDetailBinding.spinnerOriginalQuantityUnit;
        spinnerDurationUnit = fragmentCurrentFoodDetailBinding.spinnerDurationUnit;
        spinnerStatus = fragmentCurrentFoodDetailBinding.spinnerStatus;
        etNumProducts = fragmentCurrentFoodDetailBinding.etNumProducts;

        Log.e(TAG, "START BINDING VIEW");
        etName.setText(product.getProductName());
        etFoodType.setText("Undefined");
        etOriginalQuantity.setText(String.valueOf(product.getOriginalQuantity()));
        etCurrentQuantity.setText(String.valueOf(product.getCurrentQuantity()));
        etPurchaseDate.setText(parseDate(product.getPurchaseDate(), DATE_FORMAT));
        etDuration.setText(String.valueOf(product.getDuration()));
        etExpirationDate.setText(parseDate(product.getExpirationDate(), DATE_FORMAT));
        etNumProducts.setText(String.valueOf(product.getNumProducts()));
        GlideHelper.loadImage(product.getImgUrl(), getContext(), ivImg);
        Log.e(TAG, product.getImgUrl());

        SpinnerHelper.setUpMetricSpinner(spinnerCurrentQuantityUnit, product.getQuantityUnit(), getContext(), etCurrentQuantity, (float) product.getCurrentQuantity(), spinnerOriginalQuantityUnit);
        SpinnerHelper.setUpMetricSpinner(spinnerOriginalQuantityUnit, product.getQuantityUnit(), getContext(), etOriginalQuantity, (float) product.getOriginalQuantity(), spinnerCurrentQuantityUnit);
        SpinnerHelper.setUpMetricSpinner(spinnerDurationUnit, product.getDurationUnit(), getContext(), etDuration, (float) product.getDuration(), null);
        SpinnerHelper.setUpStatusSpinner(spinnerStatus, product.getFoodStatus(), getContext());

        etNumProducts.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (etNumProducts.getText().toString().isEmpty()) return;
                float numProducts = Float.parseFloat(etNumProducts.getText().toString());
                float newQuantity = Float.parseFloat(etOriginalQuantity.getText().toString()) * numProducts;
                etCurrentQuantity.setText(String.valueOf(newQuantity));
            }
        });

        return fragmentCurrentFoodDetailBinding.getRoot();
    }

    public static String parseDate(Date date, SimpleDateFormat outputDateFormat) {
        String outputDateString = null;
        outputDateString = outputDateFormat.format(date);
        return outputDateString;
    }

    private void goToCurrentFood() {
        Log.e(TAG, "Go to current food fragment");
        Fragment currentFoodFragment = CurrentFoodFragment.newInstance();
        MainActivity.switchFragment(currentFoodFragment);
    }}