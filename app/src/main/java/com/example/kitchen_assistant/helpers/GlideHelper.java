package com.example.kitchen_assistant.helpers;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.kitchen_assistant.R;
import com.parse.ParseException;
import com.parse.ParseFile;

public class GlideHelper {

    private static final String TAG = "Glide";
    private static final int IMAGE_WIDTH = 300;
    private static final int IMAGE_HEIGHT = 300;

    public static void loadAvatar(String url, Context context, ImageView imgView) {
        if (url == null || url == "default") {
            loadDefaultAvatar(context, imgView);
            Log.e(TAG, "Couldn't load image");
            return;
        }
        try {
            Glide.with(context)
                    .load(url.replaceAll("http:", "https:"))
                    .circleCrop()
                    .into(imgView);
        } catch (Exception e) {
            loadDefaultImage(context, imgView);
        }
    }

    private static void loadDefaultAvatar(Context context, ImageView imgView) {
        Glide.with(context)
                .load(R.drawable.default_avatar_file)
                .circleCrop()
                .into(imgView);
    }

    public static void loadImage(String url, Context context, ImageView imgView) {
        if (url == null || url == "default") {
            loadDefaultImage(context, imgView);
            Log.e(TAG, "Using default image");
            return;
        }
        try {
            Glide.with(context)
                    .load(url.replaceAll("http:", "https:"))
                    .override(IMAGE_WIDTH, IMAGE_HEIGHT)
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(50)))
                    .into(imgView);
        } catch (Exception e) {
            loadDefaultImage(context, imgView);
        }
        Log.e(TAG, "LOADED");
    }

    private static void loadDefaultImage(Context context, ImageView imgView) {
        Glide.with(context)
                .load(R.drawable.default_food)
                .override(IMAGE_WIDTH, IMAGE_HEIGHT)
                .into(imgView);
    }
}
