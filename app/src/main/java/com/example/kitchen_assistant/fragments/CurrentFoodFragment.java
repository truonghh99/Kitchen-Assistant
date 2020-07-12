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

import java.io.File;

public class CurrentFoodFragment extends Fragment {

    private static final String TAG = "CurrentFood";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public String photoFileName = "barcode_photo.jpg";
    File photoFile;

    private FragmentCurrentFoodBinding fragmentCurrentFoodBinding;
    private Button btAdd;

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
        Log.e(TAG, "ON CREATE VIEW");
        btAdd = fragmentCurrentFoodBinding.btAdd;
        Log.e(TAG, "GOT BUTTON");
        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "CLICKED");
                onLaunchCamera();
            }
        });
        Log.e(TAG, "set listenter");
        //setUpView();

        return fragmentCurrentFoodBinding.getRoot();
    }

    private void setUpView() {

    }

    public void onLaunchCamera() {
        Log.e(TAG, "ON LAUNCH CAMERA");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoFile = getPhotoFileUri(photoFileName);

        Uri fileProvider = FileProvider.getUriForFile(getActivity(), "com.codepath.fileprovider.kitchenassistant", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    public File getPhotoFileUri(String fileName) {
        File mediaStorageDir = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
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
                Log.e(TAG, code);
            } else { // Result was a failure
                Toast.makeText(getActivity(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}