package com.example.kitchen_assistant.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.os.Parcel;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.kitchen_assistant.R;
import com.example.kitchen_assistant.activities.MainActivity;
import com.example.kitchen_assistant.adapters.CurrentFoodAdapter;
import com.example.kitchen_assistant.clients.BarcodeReader;
import com.example.kitchen_assistant.clients.OpenFoodFacts;
import com.example.kitchen_assistant.databinding.FragmentCurrentFoodBinding;
import com.example.kitchen_assistant.models.Product;
import com.example.kitchen_assistant.storage.CurrentProducts;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.parceler.Parcels;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CurrentProductFragment extends Fragment {

    private static final String TAG = "CurrentProductFragment";
    private static final String AUTHORITY = "com.codepath.fileprovider.kitchenassistant";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public String photoFileName = "barcode_photo.jpg";
    private File photoFile;

    private List<Product> products;
    private FragmentCurrentFoodBinding fragmentCurrentFoodBinding;
    private FloatingActionButton btAdd;
    private FloatingActionButton btSearch;
    private RecyclerView rvCurrentFood;
    private static CurrentFoodAdapter adapter;

    public CurrentProductFragment() {
    }

    public static CurrentProductFragment newInstance() {
        CurrentProductFragment fragment = new CurrentProductFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentCurrentFoodBinding = FragmentCurrentFoodBinding.inflate(getLayoutInflater());
        btAdd = fragmentCurrentFoodBinding.btAdd;
        btSearch = fragmentCurrentFoodBinding.btSearch;
        rvCurrentFood = fragmentCurrentFoodBinding.rvCurrentFood;

        products = CurrentProducts.products;
        adapter = new CurrentFoodAdapter(getActivity(), products);
        rvCurrentFood.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        rvCurrentFood.setAdapter(adapter);

        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLaunchCamera();
            }
        });
        return fragmentCurrentFoodBinding.getRoot();
    }

    // Allow user to take photo using their camera
    public void onLaunchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoFile = getPhotoFileUri(photoFileName);

        Uri fileProvider = FileProvider.getUriForFile(getActivity(), AUTHORITY, photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
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
    }

    // Passing taken product's code to product detail for user to edit information & confirm insertion to current product list
    private void goToNewProductDetail(String code) {
        Log.i(TAG, "Go to new product detail");
        Product product = new Product();
        if (CurrentProducts.productHashMap.containsKey(code)) {
            Log.e(TAG, "Product exists!");
            product = CurrentProducts.productHashMap.get(code);
            CurrentFoodAdapter.goToCurrentProductDetail(product);
            Toast.makeText(getContext(), "We remember this one! Edit details here.", Toast.LENGTH_LONG).show();
            return;
        } else {
            try {
                product = OpenFoodFacts.getProductInfo(code);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            Fragment newProductDetailFragment = NewProductDetailFragment.newInstance(Parcels.wrap(product));
            MainActivity.switchFragment(newProductDetailFragment);
        }
    }

    // Allow other classes to notify changes of product list to update view
    public static void notifyDataChange() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}