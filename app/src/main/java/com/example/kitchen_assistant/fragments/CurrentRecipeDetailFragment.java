package com.example.kitchen_assistant.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kitchen_assistant.R;
import com.example.kitchen_assistant.databinding.FragmentCurrentRecipeDetailBinding;
import com.example.kitchen_assistant.databinding.FragmentNewRecipeDetailBinding;
import com.example.kitchen_assistant.helpers.GlideHelper;
import com.example.kitchen_assistant.models.Recipe;

import org.parceler.Parcels;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewRecipeDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CurrentRecipeDetailFragment extends Fragment {

    private static final String KEY_RECIPE = "Key Recipe";
    public static final String title = "Your Recipe Detail";

    private Recipe recipe;
    private FragmentCurrentRecipeDetailBinding fragmentCurrentRecipeDetailBinding;
    private ImageView ivImage;
    private TextView tvName;

    public CurrentRecipeDetailFragment() {
    }

    // TODO: Rename and change types and number of parameters
    public static CurrentRecipeDetailFragment newInstance(Parcelable recipe) {
        CurrentRecipeDetailFragment fragment = new CurrentRecipeDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(KEY_RECIPE, recipe);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            recipe = Parcels.unwrap(getArguments().getParcelable(KEY_RECIPE));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentCurrentRecipeDetailBinding = FragmentCurrentRecipeDetailBinding.inflate(getLayoutInflater());
        ivImage = fragmentCurrentRecipeDetailBinding.ivImage;
        tvName = fragmentCurrentRecipeDetailBinding.tvName;

        GlideHelper.loadImage(recipe.getImageUrl(), getContext(), ivImage);
        tvName.setText(recipe.getName());

        return fragmentCurrentRecipeDetailBinding.getRoot();
    }
}