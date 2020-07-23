package com.example.kitchen_assistant.fragments.reviews;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import com.example.kitchen_assistant.databinding.FragmentReviewComposeBinding;
import com.example.kitchen_assistant.models.Recipe;

import org.parceler.Parcels;

public class ReviewComposeFragment extends DialogFragment {

    private static final String TAG = "InstructionCompose";
    private static final String KEY_RECIPE = "RECIPE";

    private FragmentReviewComposeBinding fragmentReviewComposeBinding;
    private EditText etReview;
    private EditText etTitle;
    private Button btPost;
    private Recipe recipe;
    private RatingBar ratingBar;

    public ReviewComposeFragment() {
    }

    public static ReviewComposeFragment newInstance(Parcelable recipe) {
        ReviewComposeFragment fragment = new ReviewComposeFragment();
        Bundle args = new Bundle();
        args.putParcelable(KEY_RECIPE, recipe);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getArguments() != null) {
            recipe = Parcels.unwrap(getArguments().getParcelable(KEY_RECIPE));
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentReviewComposeBinding = FragmentReviewComposeBinding.inflate(getLayoutInflater());
        etReview = fragmentReviewComposeBinding.etReview;
        etTitle = fragmentReviewComposeBinding.etTitle;
        btPost = fragmentReviewComposeBinding.btPost;
        ratingBar = fragmentReviewComposeBinding.ratingBar;


        btPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String reviewContent = etReview.getText().toString();
                String reviewTitle = etTitle.getText().toString();
                Float rating = ratingBar.getRating();
                recipe.addReview(reviewContent, reviewTitle, rating);
                getTargetFragment().onActivityResult(getTargetRequestCode(), 0, new Intent());
                dismiss();
            }
        });

        return fragmentReviewComposeBinding.getRoot();
    }
}