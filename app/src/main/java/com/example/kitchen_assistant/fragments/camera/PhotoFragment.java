package com.example.kitchen_assistant.fragments.camera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.kitchen_assistant.activities.MainActivity;
import com.example.kitchen_assistant.databinding.FragmentPhotoBinding;
import com.example.kitchen_assistant.models.Product;
import com.example.kitchen_assistant.models.Recipe;
import com.example.kitchen_assistant.models.Review;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.ParseFile;

import org.parceler.Parcels;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static android.app.Activity.RESULT_OK;


public class PhotoFragment extends Fragment {

    private static final String KEY_MODEL_TAG = "modelTag";
    private static final String KEY_MODEL = "model";
    public final String TAG = "PhotoFragment";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    private static final int LOAD_IMAGE_ACTIVITY_REQUEST_CODE = 1512;
    private static final int RESULT_CODE = 0;
    private final int IMAGE_QUALITY = 10;

    private String photoFileName = "photo.jpg";
    private ImageView ivCamera;
    private File photoFile;
    private Button btCamera;
    private Button btLibrary;
    private FloatingActionButton btApprove;
    private FragmentPhotoBinding fragmentPhotoBinding;
    private String modelTag;

    private Product product;
    private Recipe recipe;
    private Review review;
    private Object BitmapScaler;

    public PhotoFragment() {
    }

    public static PhotoFragment newInstance(Parcelable model, String modelTag) {
        PhotoFragment fragment = new PhotoFragment();
        Bundle args = new Bundle();
        args.putParcelable(KEY_MODEL, model);
        args.putString(KEY_MODEL_TAG, modelTag);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            modelTag = getArguments().getString(KEY_MODEL_TAG);
            switch (modelTag) {
                case Product.TAG:
                    product = Parcels.unwrap(getArguments().getParcelable(KEY_MODEL));
                    break;
                case Recipe.TAG:
                    recipe = Parcels.unwrap(getArguments().getParcelable(KEY_MODEL));
                    break;
                case Review.TAG:
                    review = Parcels.unwrap(getArguments().getParcelable(KEY_MODEL));
                    break;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentPhotoBinding = FragmentPhotoBinding.inflate(inflater, container, false);
        ivCamera = fragmentPhotoBinding.ivCamera;
        btCamera = fragmentPhotoBinding.btCamera;
        btLibrary = fragmentPhotoBinding.btLibrary;
        btApprove = fragmentPhotoBinding.btApprove;

        photoFile = getPhotoFileUri(photoFileName);

        // Launch camera when camera icon or camera button is clicked
        ivCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCamera();
            }
        });

        btCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCamera();
            }
        });

        // Launch library when library icon is clicked
        btLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchLibrary();
            }
        });

        // Start uploading selected/captured image
        btApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePhoto();
            }
        });

        return fragmentPhotoBinding.getRoot();
    }

    // Allow user to select image from device's library
    private void launchLibrary() {
        try {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*"); // Only accept image types
            startActivityForResult(intent, LOAD_IMAGE_ACTIVITY_REQUEST_CODE);
        } catch (Exception e){
            Log.i(TAG, "Error while opening library", e);
        }
    }

    // Allow user to capture new image
    public void launchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider.kitchenassistant", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        intent.resolveActivity(getContext().getPackageManager());
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    // Save captured/selected image to proper model based on parent fragment's input
    public void savePhoto() {
        Log.e(TAG, "Saving selected file");
        if (photoFile == null) {
            Toast.makeText(getContext(), "You should include a photo!", Toast.LENGTH_SHORT).show();
            return;
        }
        MainActivity.showProgressBar();
        switch (modelTag) {
            case Product.TAG:
                product.setParseFile(new ParseFile(photoFile));
                break;
            case Recipe.TAG:
                recipe.setParseFile(new ParseFile(photoFile));
                break;
            case Review.TAG:
                review.setParseFile(new ParseFile(photoFile));
                break;
        }
        Toast.makeText(getContext(), "Saved your photo!", Toast.LENGTH_SHORT).show();
        getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_CODE, new Intent());
        MainActivity.hideProgressBar();
        getFragmentManager().popBackStack();
    }

    // Helper function to create directory for captured image
    public File getPhotoFileUri(String fileName) {
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "Failed to create directory");
        }

        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // If user chose to capture new image
            case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Uri takenPhotoUri = Uri.fromFile(getPhotoFileUri(photoFileName));
                    Bitmap rawTakenImage = BitmapFactory.decodeFile(takenPhotoUri.getPath());
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    rawTakenImage.compress(Bitmap.CompressFormat.JPEG, IMAGE_QUALITY, bytes);
                    photoFile = getPhotoFileUri(photoFileName + "_resized");
                    FileOutputStream fos = null;
                    try {
                        photoFile.createNewFile();
                        fos = new FileOutputStream(photoFile);
                        fos.write(bytes.toByteArray());
                        fos.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                    ivCamera.setImageBitmap(takenImage);
                } else {
                    Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
                }
                break;
            // If user chose to upload existing image
            case LOAD_IMAGE_ACTIVITY_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    try {
                        // Compress & save selected to photoFile (used for loading)
                        OutputStream os = new BufferedOutputStream(new FileOutputStream(photoFile));
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, IMAGE_QUALITY, os);
                        os.close();
                        ivCamera.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        Log.i("TAG", "Picture wasn't selected " + e);
                    }
                    break;
                } else {
                    Toast.makeText(getContext(), "You haven't picked Image", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                Log.e(TAG, "Cannot identify activity result");
        }
    }
}