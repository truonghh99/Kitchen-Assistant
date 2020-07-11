package com.example.kitchen_assistant.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import com.example.kitchen_assistant.R;
import com.example.kitchen_assistant.clients.BarcodeReader;
import com.example.kitchen_assistant.clients.OpenFoodFacts;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        OpenFoodFacts.getProductInfo(thisCode.rawValue);
    }
}