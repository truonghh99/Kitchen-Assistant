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
import com.example.kitchen_assistant.databinding.FragmentNewProductDetailBinding;
import com.example.kitchen_assistant.helpers.GlideHelper;
import com.example.kitchen_assistant.helpers.MetricConversionHelper;
import com.example.kitchen_assistant.helpers.SpinnerHelper;
import com.example.kitchen_assistant.models.FoodItem;
import com.example.kitchen_assistant.models.Product;
import com.example.kitchen_assistant.storage.CurrentFoodTypes;
import com.example.kitchen_assistant.storage.CurrentProducts;
import com.parse.ParseUser;

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
public class NewProductDetailFragment extends Fragment {

    private static final String PRODUCT = "Product";
    private static final String TAG = "NewProductDetail";
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

    private FragmentNewProductDetailBinding fragmentNewProductDetailBinding;
    private Product product;
    private EditText etName;
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
    private Button btAdd;


    public NewProductDetailFragment() {
    }

    // Initialize with a product to display
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
        etName = fragmentNewProductDetailBinding.etName;
        etFoodType = fragmentNewProductDetailBinding.etFoodType;
        etOriginalQuantity = fragmentNewProductDetailBinding.etOriginalQuantity;
        etCurrentQuantity = fragmentNewProductDetailBinding.etQuantity;
        etPurchaseDate = fragmentNewProductDetailBinding.etPurchaseDate;
        etDuration = fragmentNewProductDetailBinding.etDuration;
        etExpirationDate = fragmentNewProductDetailBinding.etExpirationDate;
        ivImg = fragmentNewProductDetailBinding.ivImg;
        spinnerCurrentQuantityUnit = fragmentNewProductDetailBinding.spinnerQuantityUnit;
        spinnerOriginalQuantityUnit = fragmentNewProductDetailBinding.spinnerOriginalQuantityUnit;
        spinnerDurationUnit = fragmentNewProductDetailBinding.spinnerDurationUnit;
        spinnerStatus = fragmentNewProductDetailBinding.spinnerStatus;
        etNumProducts = fragmentNewProductDetailBinding.etNumProducts;
        btAdd = fragmentNewProductDetailBinding.btAdd;

        etName.setText(product.getProductName());
        etFoodType.setText("Undefined"); // Wait for user to identify product's food type. TODO: Check if such product ever existed to retrieve saved info before asking
        etOriginalQuantity.setText(String.valueOf(product.getOriginalQuantity()));
        etCurrentQuantity.setText(String.valueOf(product.getCurrentQuantity()));
        etPurchaseDate.setText(parseDate(product.getPurchaseDate(), DATE_FORMAT));
        etDuration.setText(String.valueOf(product.getDuration()));
        etExpirationDate.setText(parseDate(product.getExpirationDate(), DATE_FORMAT));
        etNumProducts.setText(String.valueOf(product.getNumProducts()));
        GlideHelper.loadImage(product.getImgUrl(), getContext(), ivImg);

        SpinnerHelper.setUpMetricSpinner(spinnerCurrentQuantityUnit, product.getQuantityUnit(), getContext(), etCurrentQuantity, (float) product.getCurrentQuantity(), spinnerOriginalQuantityUnit);
        SpinnerHelper.setUpMetricSpinner(spinnerOriginalQuantityUnit, product.getQuantityUnit(), getContext(), etOriginalQuantity, (float) product.getOriginalQuantity(), spinnerCurrentQuantityUnit);
        SpinnerHelper.setUpMetricSpinner(spinnerDurationUnit, product.getDurationUnit(), getContext(), etDuration, (float) product.getDuration(), null);
        SpinnerHelper.setUpStatusSpinner(spinnerStatus, product.getFoodStatus(), getContext());

        // Automatically update current quantity according to number of products
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

        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String productName = etName.getText().toString();
                String foodType = etFoodType.getText().toString();
                Float originalQuantity = Float.parseFloat(etOriginalQuantity.getText().toString());
                String quantityUnit = spinnerOriginalQuantityUnit.getSelectedItem().toString();
                Float currentQuantity = Float.parseFloat(etCurrentQuantity.getText().toString());
                Date purchaseDate = product.getPurchaseDate();
                try {
                    purchaseDate = DATE_FORMAT.parse(etPurchaseDate.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Float duration = Float.parseFloat(etDuration.getText().toString());
                String durationUnit = spinnerDurationUnit.getSelectedItem().toString();
                String foodStatus = spinnerStatus.getSelectedItem().toString();

                product.setProductName(productName);
                product.setOriginalQuantity(originalQuantity);
                product.setQuantityUnit(quantityUnit);
                product.setCurrentQuantity(currentQuantity);
                product.setNumProducts(currentQuantity / originalQuantity);
                product.setPurchaseDate(purchaseDate);
                product.setDuration(duration);
                product.setDurationUnit(durationUnit);
                product.updateExpirationDate();
                product.setFoodStatus(foodStatus);
                product.printOutValues();

                FoodItem foodItem = new FoodItem();
                if (CurrentFoodTypes.foodItems.containsKey(foodType)) { // If such food type exists, increase quantity
                    Log.e(TAG, "Exist!");
                    foodItem = CurrentFoodTypes.foodItems.get(foodType);
                    foodItem.increaseQuantity(currentQuantity, quantityUnit);
                    product.setFoodItem(foodItem);
                    Log.e(TAG, String.valueOf(product.getFoodItem().getQuantity()));
                } else { // If such food type doesn't exist, create new one
                    foodItem.setName(foodType);
                    foodItem.setQuantity(currentQuantity);
                    foodItem.setQuantityUnit(quantityUnit);
                    foodItem.setOwner(ParseUser.getCurrentUser());
                    CurrentFoodTypes.addFoodItem(foodItem);
                }
                product.setFoodItem(foodItem);

                CurrentProducts.addProduct(product);
                goToCurrentFood();

            }
        });

        return fragmentNewProductDetailBinding.getRoot();
    }

    // Parse Date values to proper string format (MM/dd/yyyy). TODO: Is it better to create another helper class to handle those toString format?
    public static String parseDate(Date date, SimpleDateFormat outputDateFormat) {
        String outputDateString = null;
        outputDateString = outputDateFormat.format(date);
        return outputDateString;
    }

    // Go to current food fragment using the initialized instance in MainActivity
    private void goToCurrentFood() {
        MainActivity.bottomNavigation.setSelectedItemId(R.id.miCurrentFood);
    }}