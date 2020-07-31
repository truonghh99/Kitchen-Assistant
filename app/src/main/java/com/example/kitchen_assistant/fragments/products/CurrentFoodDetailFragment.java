package com.example.kitchen_assistant.fragments.products;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kitchen_assistant.R;
import com.example.kitchen_assistant.activities.MainActivity;
import com.example.kitchen_assistant.clients.Spoonacular;
import com.example.kitchen_assistant.databinding.FragmentCurrentFoodDetailBinding;
import com.example.kitchen_assistant.fragments.camera.PhotoFragment;
import com.example.kitchen_assistant.fragments.shopping.PreviewShoppingItemFragment;
import com.example.kitchen_assistant.fragments.recipes.RecipeExploreFragment;
import com.example.kitchen_assistant.helpers.GlideHelper;
import com.example.kitchen_assistant.helpers.MatchingHelper;
import com.example.kitchen_assistant.helpers.MetricConverter;
import com.example.kitchen_assistant.helpers.RecipeEvaluator;
import com.example.kitchen_assistant.models.FoodItem;
import com.example.kitchen_assistant.models.Product;
import com.example.kitchen_assistant.storage.CurrentProducts;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CurrentFoodDetailFragment extends Fragment {

    private static final String PRODUCT = "Product";
    private static final String TAG = "CurrentFoodDetail";

    public static final int REQUEST_CODE = 0;
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
    public static final String title = "Product Details";

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
    private EditText etStatus;
    private EditText etNumProducts;
    private FloatingActionButton btMenuOpen;
    private FloatingActionButton btApprove;
    private FloatingActionButton btRemove;
    private FloatingActionButton btShop;
    private FloatingActionButton btCook;


    public CurrentFoodDetailFragment() {
    }

    // Take product information to display in details
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
        etNumProducts = fragmentCurrentFoodDetailBinding.etNumProducts;
        etStatus = fragmentCurrentFoodDetailBinding.etFoodStatus;
        btApprove = fragmentCurrentFoodDetailBinding.btApprove;
        btRemove = fragmentCurrentFoodDetailBinding.btRemove;
        btCook = fragmentCurrentFoodDetailBinding.btCook;
        btShop = fragmentCurrentFoodDetailBinding.btShop;
        btMenuOpen = fragmentCurrentFoodDetailBinding.btMenuOpen;

        // Assign values to views using product's info
        etName.setText(product.getProductName());
        etFoodType.setText(product.getFoodItem().getName());
        etOriginalQuantity.setText(product.getOriginalQuantity() + " " + product.getQuantityUnit());
        etCurrentQuantity.setText(product.getCurrentQuantity() + " " + product.getQuantityUnit());
        etPurchaseDate.setText(parseDate(product.getPurchaseDate(), DATE_FORMAT));
        etDuration.setText(product.getDuration() + " " + product.getDurationUnit());
        etExpirationDate.setText(parseDate(product.getExpirationDate(), DATE_FORMAT));
        etNumProducts.setText(String.valueOf(product.getNumProducts()));
        etStatus.setText(product.getFoodStatus());
        loadImage();

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
                    product.updateExpirationDate();
                    product.updateFoodStatus();
                    etExpirationDate.setText(parseDate(product.getExpirationDate(), DATE_FORMAT));
                    etStatus.setText(product.getFoodStatus());
                    return true;
                }
                return false;
            }
        });

        // Open or close floating menu
        btMenuOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openOrCloseFabMenu();
            }
        });

        // Save changes
        btApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveInfo();
                Toast.makeText(getContext(), "Your change has been saved!", Toast.LENGTH_SHORT).show();
            }
        });

        // Remove product
        btRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CurrentProducts.removeProduct(product);
                goToCurrentFood();
            }
        });

        // Fetch recipes containing this food type
        btCook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    goToExplore();
                } catch (InterruptedException | com.parse.ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        // Allow user to add this product to shopping list after previewing and editing information (if wished)
        btShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToPreviewShoppingItem();
            }
        });

        return fragmentCurrentFoodDetailBinding.getRoot();
    }

    private void openOrCloseFabMenu() {
        if (btApprove.getVisibility() == View.INVISIBLE) {
            btApprove.setVisibility(View.VISIBLE);
            btCook.setVisibility(View.VISIBLE);
            btRemove.setVisibility(View.VISIBLE);
            btShop.setVisibility(View.VISIBLE);
        } else {
            btApprove.setVisibility(View.INVISIBLE);
            btCook.setVisibility(View.INVISIBLE);
            btRemove.setVisibility(View.INVISIBLE);
            btShop.setVisibility(View.INVISIBLE);
        }
    }

    // Save all changes of current product before closing edit screen
    private void saveInfo() {
        String productName = etName.getText().toString();
        String foodType = etFoodType.getText().toString();
        Date purchaseDate = product.getPurchaseDate();
        try {
            purchaseDate = DATE_FORMAT.parse(etPurchaseDate.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Float duration = MetricConverter.extractQuantityVal(etDuration.getText().toString());
        String durationUnit = MetricConverter.extractQuantityUnit(etDuration.getText().toString());
        String foodStatus = etStatus.getText().toString();

        product.detachFoodItem();
        product.setProductName(productName);
        product.setPurchaseDate(purchaseDate);
        product.setDuration(duration);
        product.setDurationUnit(durationUnit);
        product.updateExpirationDate();
        product.setFoodStatus(foodStatus);
        product.printOutValues();

        FoodItem foodItem = new FoodItem();
        foodItem.setName(foodType);
        foodItem.setQuantity(product.getCurrentQuantity());
        foodItem.setQuantityUnit(product.getQuantityUnit());
        foodItem.setOwner(ParseUser.getCurrentUser());

        MatchingHelper.attemptToAttachFoodItem(foodItem, product);
        CurrentProducts.saveProductInBackGround(product);
        CurrentFoodFragment.notifyDataChange();
        RecipeEvaluator.evaluateAllRecipe();
    }

    // Parse Date values to proper string format (MM/dd/yyyy)
    public static String parseDate(Date date, SimpleDateFormat outputDateFormat) {
        String outputDateString = null;
        outputDateString = outputDateFormat.format(date);
        return outputDateString;
    }

    // Go to current food fragment using the initialized instance in MainActivity
    private void goToCurrentFood() {
        MainActivity.bottomNavigation.setSelectedItemId(R.id.miCurrentFood);
    }

    // Allow user to add this product to shopping list after previewing and editing information (if wished)
    private void goToPreviewShoppingItem() {
        DialogFragment dialogFragment = PreviewShoppingItemFragment.newInstance(product.getProductName(), product.getOriginalQuantity(), product.getQuantityUnit());
        dialogFragment.show(getActivity().getSupportFragmentManager(), "Dialog");
    }

    // Query recipes containing this current product
    private void goToExplore() throws InterruptedException, com.parse.ParseException {
        MainActivity.showProgressBar();
        // Create a one-element list contains only this current food item to fit query's format
        List<FoodItem> ingredientList = new ArrayList<FoodItem>() {
            {
                add(product.getFoodItem());
            }
        };
        String ingredients = Spoonacular.generateList(ingredientList);
        RecipeExploreFragment recipeExploreFragment = RecipeExploreFragment.newInstance(ingredients);
        MainActivity.switchFragment(recipeExploreFragment);
    }

    // Go to photo compose fragment
    private void goToPhoto() {
        Fragment fragment = PhotoFragment.newInstance(Parcels.wrap(product), Product.TAG);
        fragment.setTargetFragment(this, REQUEST_CODE);
        MainActivity.switchFragment(fragment);
    }

    // Reload image after user selects/take photo in photo fragment
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loadImage();
    }

    public void loadImage() {
        GlideHelper.loadImage(product.getImageUrl(), getContext(), ivImg);
    }
}