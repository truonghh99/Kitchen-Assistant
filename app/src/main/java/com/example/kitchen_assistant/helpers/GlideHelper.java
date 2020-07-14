package com.example.kitchen_assistant.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;
import com.example.kitchen_assistant.R;

public class GlideHelper {

    private static final String TAG = "Glide";

    public static void loadAvatar(String url, Context context, ImageView imgView) {
        if (url == null) {
            Log.e(TAG, "Couldn't load image");
            return;
        }
        if (url != "default") {
            try {
                Glide.with(context)
                        .load(url.replaceAll("http:", "https:"))
                        .circleCrop()
                        .into(imgView);
            } catch (Exception e) {
                loadDefaultImage(context, imgView);
            }
        } else {
            loadDefaultAvatar(context, imgView);
        }
    }

    public static void loadImage(String url, Context context, ImageView imgView) {
        if (url == null) {
            Log.e(TAG, "Couldn't load image");
            return;
        }
        if (url != "default") {
            try {
                Glide.with(context)
                        .load(url.replaceAll("http:", "https:"))
                        .override(350, 350)
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(50)))
                        .into(imgView);
            } catch (Exception e) {
                loadDefaultImage(context, imgView);
            }
        } else {
            loadDefaultImage(context, imgView);
        }
    }

    private static void loadDefaultAvatar(Context context, ImageView imgView) {
        Glide.with(context)
                .load(R.drawable.default_avatar_file)
                .circleCrop()
                .into(imgView);
    }

    private static void loadDefaultImage(Context context, ImageView imgView) {
        Glide.with(context)
                .load(R.drawable.default_food)
                .override(350,350)
                .into(imgView);
    }
}
