package com.example.kitchen_assistant.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.kitchen_assistant.R;
import com.example.kitchen_assistant.activities.MainActivity;
import com.example.kitchen_assistant.clients.Spoonacular;
import com.example.kitchen_assistant.databinding.FragmentCurrentFoodDetailBinding;
import com.example.kitchen_assistant.helpers.GlideHelper;
import com.example.kitchen_assistant.helpers.SpinnerHelper;
import com.example.kitchen_assistant.models.FoodItem;
import com.example.kitchen_assistant.models.Product;
import com.example.kitchen_assistant.models.Recipe;
import com.example.kitchen_assistant.storage.CurrentProducts;
import com.example.kitchen_assistant.storage.CurrentRecipes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.parceler.Parcels;

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
public class CurrentProductDetailFragment extends Fragment {

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
    private FloatingActionButton btApprove;
    private FloatingActionButton btRemove;
    private FloatingActionButton btShop;
    private FloatingActionButton btCook;


    public CurrentProductDetailFragment() {
    }

    public static CurrentProductDetailFragment newInstance(Parcelable product) {
        CurrentProductDetailFragment fragment = new CurrentProductDetailFragment();
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
        btApprove = fragmentCurrentFoodDetailBinding.btApprove;
        btRemove =fragmentCurrentFoodDetailBinding.btRemove;
        btCook = fragmentCurrentFoodDetailBinding.btCook;
        btShop = fragmentCurrentFoodDetailBinding.btShop;

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

        btApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveInfo();
                goToCurrentFood();
            }
        });

        btRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CurrentProducts.removeProduct(product);
                goToCurrentFood();
            }
        });

        btCook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    queryRecipes();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        return fragmentCurrentFoodDetailBinding.getRoot();
    }

    private void queryRecipes() throws InterruptedException {
        List<Recipe> recipes = Spoonacular.getByIngredients(new ArrayList<FoodItem>());
        Log.i(TAG, "Received " + recipes.size() + " recipes");
        for (Recipe recipe: recipes) {
            Log.e(TAG, recipe.getName());
        }
        CurrentRecipes.addAllRecipes(recipes);
        goToRecipe();
    }

    private void goToRecipe() {
        MainActivity.bottomNavigation.setSelectedItemId(R.id.miRecipe);
    }

    private void saveInfo() {
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

        CurrentProducts.saveProductInBackGround(product);
        CurrentProductFragment.notifyDataChange();
    }

    public static String parseDate(Date date, SimpleDateFormat outputDateFormat) {
        String outputDateString = null;
        outputDateString = outputDateFormat.format(date);
        return outputDateString;
    }

    private void goToCurrentFood() {
        MainActivity.bottomNavigation.setSelectedItemId(R.id.miCurrentFood);
    }}