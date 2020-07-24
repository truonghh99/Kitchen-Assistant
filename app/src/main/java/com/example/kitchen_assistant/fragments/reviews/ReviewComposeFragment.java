package com.example.kitchen_assistant.fragments.reviews;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.kitchen_assistant.activities.MainActivity;
import com.example.kitchen_assistant.databinding.FragmentReviewComposeBinding;
import com.example.kitchen_assistant.fragments.products.PhotoFragment;
import com.example.kitchen_assistant.helpers.GlideHelper;
import com.example.kitchen_assistant.models.Product;
import com.example.kitchen_assistant.models.Recipe;
import com.example.kitchen_assistant.models.Review;

import org.parceler.Parcels;

public class ReviewComposeFragment extends Fragment {

    private static final String TAG = "InstructionCompose";
    private static final String KEY_RECIPE = "RECIPE";
    private static final String KEY_REVIEW = "REVIEW";
    private static final int REQUEST_CODE = 0;

    private FragmentReviewComposeBinding fragmentReviewComposeBinding;
    private EditText etReview;
    private EditText etTitle;
    private Button btPost;
    private ImageView ivImage;
    private TextView tvPhoto;
    private RatingBar ratingBar;

    private Recipe recipe;
    private Review review;

    public ReviewComposeFragment() {
    }

    public static ReviewComposeFragment newInstance(Parcelable recipe, Parcelable review) {
        ReviewComposeFragment fragment = new ReviewComposeFragment();
        Bundle args = new Bundle();
        args.putParcelable(KEY_RECIPE, recipe);
        args.putParcelable(KEY_REVIEW,review);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getArguments() != null) {
            recipe = Parcels.unwrap(getArguments().getParcelable(KEY_RECIPE));
            review = Parcels.unwrap(getArguments().getParcelable(KEY_REVIEW));
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
        ivImage = fragmentReviewComposeBinding.ivImage;
        tvPhoto = fragmentReviewComposeBinding.tvPhoto;

        loadImage();

        btPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String reviewContent = etReview.getText().toString();
                String reviewTitle = etTitle.getText().toString();
                Float rating = ratingBar.getRating();

                review.setReviewContent(reviewContent);
                review.setTitle(reviewTitle);
                review.setRating(rating);

                recipe.addReview(review);
                getTargetFragment().onActivityResult(getTargetRequestCode(), 0, new Intent());
                getFragmentManager().popBackStack();
            }
        });

        tvPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToPhoto();
            }
        });

        return fragmentReviewComposeBinding.getRoot();
    }

    private void goToPhoto() {
        Fragment fragment = PhotoFragment.newInstance(Parcels.wrap(review), Review.TAG);
        fragment.setTargetFragment(this, REQUEST_CODE);
        MainActivity.switchFragment(fragment);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loadImage();
    }

    public void loadImage() {
        if (review.getParseFile() != null) {
            Log.e(TAG, review.getImageUrl());
            GlideHelper.loadImage(review.getImageUrl(), getContext(), ivImage);
            tvPhoto.setText("Click here to replace photo");
        } else {
            Log.e(TAG, "No image to load");
            tvPhoto.setText("Want to include a photo?");
        }
    }
}