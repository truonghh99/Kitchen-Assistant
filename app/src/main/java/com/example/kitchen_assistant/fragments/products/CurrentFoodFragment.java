package com.example.kitchen_assistant.fragments.products;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.example.kitchen_assistant.R;
import com.example.kitchen_assistant.activities.MainActivity;
import com.example.kitchen_assistant.adapters.CurrentFoodAdapter;
import com.example.kitchen_assistant.clients.BarcodeReader;
import com.example.kitchen_assistant.clients.Spoonacular;
import com.example.kitchen_assistant.databinding.FragmentCurrentFoodBinding;
import com.example.kitchen_assistant.fragments.camera.ScannerFragment;
import com.example.kitchen_assistant.fragments.profile.ProfileFragment;
import com.example.kitchen_assistant.fragments.recipes.RecipeExploreFragment;
import com.example.kitchen_assistant.helpers.FabAnimationHelper;
import com.example.kitchen_assistant.helpers.MatchingHelper;
import com.example.kitchen_assistant.models.FoodItem;
import com.example.kitchen_assistant.models.Product;
import com.example.kitchen_assistant.storage.CurrentProducts;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.ParseException;

import org.parceler.Parcels;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CurrentFoodFragment extends Fragment {

    public static final String MANUALLY_INSERT_KEY = "Manually Insert";

    private static final String TAG = "CurrentProductFragment";
    private static final String AUTHORITY = "com.codepath.fileprovider.kitchenassistant";
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    private static final int SCANNER_REQUEST_CODE = 0;
    private static CurrentFoodAdapter adapter;

    private String photoFileName = "barcode_photo.jpg";
    private File photoFile;
    private String title = "Current Products";
    private List<Product> products;
    private FragmentCurrentFoodBinding fragmentCurrentFoodBinding;
    private FloatingActionButton btMenuOpen;
    private FloatingActionButton btCook;
    private FloatingActionButton btScan;
    private FloatingActionButton btWrite;
    private RecyclerView rvCurrentFood;

    private Boolean fabMenuOpen = false;

    public CurrentFoodFragment() {
    }

    public static CurrentFoodFragment newInstance() {
        CurrentFoodFragment fragment = new CurrentFoodFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main_toolbar, menu);
        ((MainActivity) getContext()).getSupportActionBar().setTitle(title);

        setUpSearchView(menu);
        setUpProfile(menu);
    }

    // Set up search view in menu toolbar
    private void setUpSearchView(Menu menu) {
        MenuItem miSearch = menu.findItem(R.id.miSearch);
        SearchView searchView = new SearchView(((MainActivity) getContext()).getSupportActionBar().getThemedContext());
        miSearch.setActionView(searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                final List<Product> filteredModelList = filter(products, newText);
                adapter.replaceAll(filteredModelList);
                rvCurrentFood.scrollToPosition(0);
                return true;
            }
        });
        searchView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
    }

    private void setUpProfile(Menu menu) {
        MenuItem miProfile = menu.findItem(R.id.miProfile);
        miProfile.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                goToProfile();
                return true;
            }
        });
    }

    private void goToProfile() {
        ProfileFragment profileFragment = ProfileFragment.newInstance();
        MainActivity.switchFragment(profileFragment);
    }

    // Filter product list based on given query in search bar
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        products = CurrentProducts.products;

        // Bind views
        fragmentCurrentFoodBinding = FragmentCurrentFoodBinding.inflate(getLayoutInflater());
        btMenuOpen = fragmentCurrentFoodBinding.btMenuOpen;
        btCook = fragmentCurrentFoodBinding.btCook;
        btScan = fragmentCurrentFoodBinding.btScan;
        btWrite = fragmentCurrentFoodBinding.btWrite;

        // Set up adapter & recycler view
        rvCurrentFood = fragmentCurrentFoodBinding.rvCurrentFood;
        adapter = new CurrentFoodAdapter(getActivity(), products);
        rvCurrentFood.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        rvCurrentFood.setAdapter(adapter);

        // Open or close floating menu
        btMenuOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openOrCloseFabMenu();
            }
        });

        // Open camera for user to scan new products
        btScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLaunchScanner();
            }
        });

        // Allow user to write new product without scanning barcode
        btWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToNewProductDetail(MANUALLY_INSERT_KEY);
            }
        });

        // Request recipes that contain given products
        btCook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    goToExplore();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        return fragmentCurrentFoodBinding.getRoot();
    }

    private void openOrCloseFabMenu() {
        if (!fabMenuOpen) {
            FabAnimationHelper.showFab(btScan, getContext());
            FabAnimationHelper.showFab(btWrite, getContext());
            FabAnimationHelper.showFab(btCook, getContext());
            FabAnimationHelper.rotateOpenFab(btMenuOpen, getContext());
            fabMenuOpen = true;
        } else {
            FabAnimationHelper.hideFab(btScan, getContext());
            FabAnimationHelper.hideFab(btWrite, getContext());
            FabAnimationHelper.hideFab(btCook, getContext());
            fabMenuOpen = false;
            FabAnimationHelper.rotateCloseFab(btMenuOpen, getContext());
        }
    }


    public void onLaunchScanner() {
        ScannerFragment scannerFragment = ScannerFragment.newInstance();
        scannerFragment.setTargetFragment(this, SCANNER_REQUEST_CODE);
        scannerFragment.show(getActivity().getSupportFragmentManager(), "Dialog");
    }

    // Helper function to create storing directory for taken image
    public File getPhotoFileUri(String fileName) {
        File mediaStorageDir = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "Failed to create directory");
        }
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);
        return file;
    }

    // Use Google Barcode API to extract code from taken image & process information from there
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                String code = BarcodeReader.getCodeFromImg(takenImage, getActivity().getApplicationContext());
                if (code == null) {
                    Log.e(TAG, "Couldn't identify barcode");
                    Toast.makeText(getActivity(), "Couldn't identify barcode, please scan again", Toast.LENGTH_SHORT).show();
                    goToNewProductDetail("0041789002519");
                    return;
                } else {
                    Log.i(TAG, "Got product with code: " + code);
                    goToNewProductDetail(code);
                }
            } else {
                Toast.makeText(getActivity(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == SCANNER_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Log.e(TAG, "GOT CODE FROM SCANNER");
                String code = data.getStringExtra(ScannerFragment.KEY_CODE);
                goToNewProductDetail(code);
            }
        }
    }

    // Passing taken product's code to product detail for user to edit information & confirm insertion to current product list
    private void goToNewProductDetail(String code) {
        Log.i(TAG, "Go to new product detail");
        Product product = null;
        try {
            product = MatchingHelper.attemptToCreateProduct(code);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (code != MANUALLY_INSERT_KEY && CurrentProducts.productHashMap.containsKey(code)) { // Product exists in current list
            CurrentFoodAdapter.goToCurrentProductDetail(product);
            Toast.makeText(getContext(), "You already have this product. Edit detail here!", Toast.LENGTH_LONG).show();
        } else { // Product is new
            Fragment newProductDetailFragment = NewProductDetailFragment.newInstance(Parcels.wrap(product));
            MainActivity.switchFragment(newProductDetailFragment);
        }
    }

    // Query recipes containing all current products
    private void goToExplore() throws ParseException {
        MainActivity.showProgressBar();
        List<FoodItem> ingredientList = new ArrayList<FoodItem>() {
            {
                for (Product product : products) {
                    add(product.getFoodItem());
                }
            }
        };
        String ingredients = Spoonacular.generateList(ingredientList);
        RecipeExploreFragment recipeExploreFragment = RecipeExploreFragment.newInstance(ingredients);
        MainActivity.switchFragment(recipeExploreFragment);
    }

    // Allow other classes to notify changes of product list to update view
    public static void notifyDataChange() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

}