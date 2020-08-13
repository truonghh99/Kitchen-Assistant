package com.example.kitchen_assistant.fragments.products;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.kitchen_assistant.R;
import com.example.kitchen_assistant.activities.MainActivity;
import com.example.kitchen_assistant.databinding.FragmentNewProductDetailBinding;
import com.example.kitchen_assistant.fragments.camera.PhotoFragment;
import com.example.kitchen_assistant.helpers.GlideHelper;
import com.example.kitchen_assistant.helpers.MatchingHelper;
import com.example.kitchen_assistant.helpers.MetricConverter;
import com.example.kitchen_assistant.models.FoodItem;
import com.example.kitchen_assistant.models.Product;
import com.example.kitchen_assistant.storage.CurrentProducts;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    public static final String title = "Add New Product";
    private static final int REQUEST_CODE = 0;

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
    private EditText etStatus;
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
            product = Parcels.unwrap(getArguments().getParcelable(PRODUCT));
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
        etNumProducts = fragmentNewProductDetailBinding.etNumProducts;
        etStatus = fragmentNewProductDetailBinding.etFoodStatus;
        btAdd = fragmentNewProductDetailBinding.btAdd;

        ((MainActivity) getContext()).getSupportActionBar().setTitle(title);

        // If product is scanned, display returned information from OpenFoodFacts. Otherwise leave views empty
        if (product.getProductCode() != CurrentFoodFragment.MANUALLY_INSERT_KEY) {
            etName.setText(product.getProductName());
            etFoodType.setText(product.getFoodItem().getName());
            etOriginalQuantity.setText(product.getOriginalQuantity() + " " + product.getQuantityUnit());
            etCurrentQuantity.setText(product.getCurrentQuantity() + " " + product.getQuantityUnit());
            etDuration.setText(product.getDuration() + " " + product.getDurationUnit());
            etExpirationDate.setText(parseDate(product.getExpirationDate(), DATE_FORMAT));
            etNumProducts.setText(String.valueOf(product.getNumProducts()));
            etStatus.setText(product.getFoodStatus());
        }

        loadImage();
        etPurchaseDate.setText(parseDate(new Date(), DATE_FORMAT));

        // Allow user to change product's photo
        ivImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToPhoto();
            }
        });

        // Automatically update current quantity according to number of products
        etNumProducts.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    float numProducts = Float.parseFloat(etNumProducts.getText().toString());
                    product.setNumProducts(numProducts);
                    product.updateCurrentQuantity();
                    etCurrentQuantity.setText(product.getCurrentQuantity() + " " + product.getQuantityUnit());
                    return true;
                }
                return false;
            }
        });

        // Automatically update current quantity according to original quantity
        etOriginalQuantity.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    float originalQuantity = MetricConverter.extractQuantityVal(etOriginalQuantity.getText().toString());
                    String unit = MetricConverter.extractQuantityUnit(etOriginalQuantity.getText().toString());
                    product.setOriginalQuantity(originalQuantity);
                    product.setQuantityUnit(unit);
                    product.updateCurrentQuantity();
                    etCurrentQuantity.setText(product.getCurrentQuantity() + " " + product.getQuantityUnit());
                    return true;
                }
                return false;
            }
        });

        // Automatically update expiration date & status according to duration
        etDuration.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    Float duration = MetricConverter.extractQuantityVal(etDuration.getText().toString());
                    String durationUnit = MetricConverter.extractQuantityUnit(etDuration.getText().toString());
                    product.setDuration(duration);
                    product.setDurationUnit(durationUnit);
                    if (product.getPurchaseDate() != null) {
                        product.updateExpirationDate();
                        product.updateFoodStatus();
                        etExpirationDate.setText(parseDate(product.getExpirationDate(), DATE_FORMAT));
                        etStatus.setText(product.getFoodStatus());
                    }
                    return true;
                }
                return false;
            }
        });

        // Automatically update expiration date & status according to purchase date
        etPurchaseDate.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    Date purchaseDate = product.getPurchaseDate();
                    try {
                        purchaseDate = DATE_FORMAT.parse(etPurchaseDate.getText().toString());
                    } catch (ParseException e) {
                        Toast.makeText(getContext(), "Invalid date format", Toast.LENGTH_SHORT);
                    }
                    product.setPurchaseDate(purchaseDate);
                    if (product.getDurationUnit() != null) {
                        product.updateExpirationDate();
                        product.updateFoodStatus();
                        etExpirationDate.setText(parseDate(product.getExpirationDate(), DATE_FORMAT));
                        etStatus.setText(product.getFoodStatus());
                    }
                    return true;
                }
                return false;
            }
        });

        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveProduct();
            }
        });

        return fragmentNewProductDetailBinding.getRoot();
    }

    private void saveProduct() {
        // Extract information from user's input
        String productName = etName.getText().toString();
        String foodType = etFoodType.getText().toString();
        float originalQuantity = MetricConverter.extractQuantityVal(etOriginalQuantity.getText().toString());
        String quantityUnit = MetricConverter.extractQuantityUnit(etOriginalQuantity.getText().toString());
        float currentQuantity = MetricConverter.extractQuantityVal(etCurrentQuantity.getText().toString());
        Date purchaseDate = product.getPurchaseDate();
        try {
            purchaseDate = DATE_FORMAT.parse(etPurchaseDate.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Float duration = MetricConverter.extractQuantityVal(etDuration.getText().toString());
        String durationUnit = MetricConverter.extractQuantityUnit(etDuration.getText().toString());
        String foodStatus = etStatus.getText().toString();

        // Assign attributes to product object
        if (product.getProductCode() == CurrentFoodFragment.MANUALLY_INSERT_KEY) {
            product.setProductCode(productName);
        }
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

        // Create corresponding foodItem
        FoodItem foodItem = new FoodItem();
        foodItem.setName(foodType);
        foodItem.setQuantity(currentQuantity);
        foodItem.setQuantityUnit(quantityUnit);
        foodItem.setOwner(ParseUser.getCurrentUser());

        // Attach foodItem & add product
        MatchingHelper.attemptToAttachFoodItem(foodItem, product);
        CurrentProducts.addProduct(product);
        goToCurrentFood();
    }

    // Parse Date values to proper string format (MM/dd/yyyy).
    public static String parseDate(Date date, SimpleDateFormat outputDateFormat) {
        String outputDateString = null;
        outputDateString = outputDateFormat.format(date);
        return outputDateString;
    }

    // Go to current food fragment using the initialized instance in MainActivity
    private void goToCurrentFood() {
        MainActivity.bottomNavigation.setSelectedItemId(R.id.miCurrentFood);
    }

    // Go to photo compose fragment
    private void goToPhoto() {
        Fragment fragment = PhotoFragment.newInstance(Parcels.wrap(product), Product.TAG);
        fragment.setTargetFragment(this, REQUEST_CODE);
        MainActivity.switchFragment(fragment);
    }

    // Reload product's image using selected image from photo compose fragment
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loadImage();
    }

    public void loadImage() {
        if (product.getImageUrl() != null) {
            Log.e(TAG, product.getImageUrl());
            GlideHelper.loadImage(product.getImageUrl(), getContext(), ivImg);
        } else {
            Log.e(TAG, "No image to load");
        }
    }
}