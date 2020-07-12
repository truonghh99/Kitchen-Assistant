package com.example.kitchen_assistant.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.kitchen_assistant.R;
import com.example.kitchen_assistant.activities.MainActivity;
import com.example.kitchen_assistant.clients.BarcodeReader;
import com.example.kitchen_assistant.databinding.FragmentCurrentFoodBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;

public class CurrentFoodFragment extends Fragment {

    private static final String TAG = "CurrentFood";
    private static final String AUTHORITY = "com.codepath.fileprovider.kitchenassistant";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public String photoFileName = "barcode_photo.jpg";
    File photoFile;

    private FragmentCurrentFoodBinding fragmentCurrentFoodBinding;
    private FloatingActionButton btAdd;

    public CurrentFoodFragment() {
    }

    public static CurrentFoodFragment newInstance() {
        CurrentFoodFragment fragment = new CurrentFoodFragment();
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
        setUpView();

        return fragmentCurrentFoodBinding.getRoot();
    }

    private void setUpView() {
        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLaunchCamera();
            }
        });
        Log.e(TAG, "set listenter");
    }

    public void onLaunchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoFile = getPhotoFileUri(photoFileName);

        Uri fileProvider = FileProvider.getUriForFile(getActivity(), AUTHORITY, photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    public File getPhotoFileUri(String fileName) {
        File mediaStorageDir = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "ailed to create directory");
        }
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);
        return file;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                String code = BarcodeReader.getCodeFromImg(takenImage, getActivity().getApplicationContext());
                if (code == null) {
                    Toast.makeText(getActivity(), "Couldn't identify barcode, please scan again", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.e(TAG, String.valueOf(code));
                goToNewProductDetail();
            } else {
                Toast.makeText(getActivity(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void goToNewProductDetail() {
        Fragment newProductDetailFragment = NewProductDetailFragment.newInstance();
        MainActivity.switchFragment(newProductDetailFragment);
    }
}