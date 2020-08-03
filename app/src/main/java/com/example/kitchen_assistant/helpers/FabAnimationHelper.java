package com.example.kitchen_assistant.helpers;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.kitchen_assistant.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class FabAnimationHelper {


    public static void showFab(FloatingActionButton button, Context context) {
        Animation showAnimation = AnimationUtils.loadAnimation(context, R.anim.fab_show_animation);
        button.startAnimation(showAnimation);
    }


    public static void hideFab(FloatingActionButton button, Context context) {
        Animation hideAnimation = AnimationUtils.loadAnimation(context, R.anim.fab_hide_animation);
        button.startAnimation(hideAnimation);
    }

    public static void rotateOpenFab(FloatingActionButton button, Context context) {
        Animation rotateAnimation = AnimationUtils.loadAnimation(context, R.anim.fab_rotate_open_animation);
        button.startAnimation(rotateAnimation);
    }

    public static void rotateCloseFab(FloatingActionButton button, Context context) {
        Animation rotateAnimation = AnimationUtils.loadAnimation(context, R.anim.fab_rotate_close_animation);
        button.startAnimation(rotateAnimation);
    }

}
