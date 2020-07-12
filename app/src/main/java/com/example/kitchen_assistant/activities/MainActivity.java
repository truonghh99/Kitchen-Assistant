package com.example.kitchen_assistant.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.kitchen_assistant.R;
import com.example.kitchen_assistant.clients.BarcodeReader;
import com.example.kitchen_assistant.databinding.ActivityMainBinding;
import com.example.kitchen_assistant.fragments.CurrentFoodFragment;
import com.google.android.gms.ads.doubleclick.CustomRenderedAd;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public final String APP_TAG = "MyCustomApp";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public String photoFileName = "barcode_photo.jpg";
    File photoFile;

    private ActivityMainBinding activityMainBinding;
    private BottomNavigationView bottomNavigation;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());

        final Fragment currentFoodFragment = CurrentFoodFragment.newInstance();

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContainer, currentFoodFragment).commit();

//        bottomNavigation = activityMainBinding.bottomNavigation;
//        bottomNavigation.setOnNavigationItemSelectedListener(
//                new BottomNavigationView.OnNavigationItemSelectedListener() {
//                    @Override
//                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                        Fragment fragment;
//                        switch (item.getItemId()) {
//                            case R.id.miHome:
//                                fragment = newsfeedFragment;
//                                break;
//                            case R.id.miCompose:
//                                fragment = composeFragment;
//                                break;
//                            case R.id.miProfile:
//                            default:
//                                fragment = profileFragment;
//                                break;
//                        }
//                        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
//                        return true;
//                    }
//                });
//        bottomNavigation.setSelectedItemId(R.id.miHome);
    }
}