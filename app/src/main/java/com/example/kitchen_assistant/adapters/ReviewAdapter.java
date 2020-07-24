package com.example.kitchen_assistant.adapters;

import android.content.ClipData;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kitchen_assistant.R;
import com.example.kitchen_assistant.activities.MainActivity;
import com.example.kitchen_assistant.databinding.ItemRecipeBinding;
import com.example.kitchen_assistant.databinding.ItemReviewBinding;
import com.example.kitchen_assistant.fragments.recipes.CurrentRecipeDetailFragment;
import com.example.kitchen_assistant.fragments.recipes.NewRecipeDetailFragment;
import com.example.kitchen_assistant.helpers.GlideHelper;
import com.example.kitchen_assistant.models.Product;
import com.example.kitchen_assistant.models.Recipe;
import com.example.kitchen_assistant.models.Review;
import com.example.kitchen_assistant.storage.CurrentRecipes;

import org.parceler.Parcels;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private final String TAG = "ReviewAdapter";
    private Context context;
    private List<Review> reviews;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemReviewBinding itemReviewBinding = ItemReviewBinding.inflate(layoutInflater, parent, false);
        return new ViewHolder(itemReviewBinding);
    }

    public ReviewAdapter(Context context, List<Review> reviews) {
        this.context = context;
        this.reviews = reviews;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Review review = reviews.get(position);
        holder.bind(review);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public void replaceAll(List<Review> filteredReviews) {
        reviews = filteredReviews;
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvReviewTitle;
        private TextView tvReviewContent;
        private ImageView ivProfileImage;
        private RatingBar ratingBar;
        private ImageView ivImg;

        public ViewHolder(@NonNull ItemReviewBinding itemReviewBinding) {
            super(itemReviewBinding.getRoot());
            tvReviewTitle = itemReviewBinding.tvReviewTitle;
            tvReviewContent = itemReviewBinding.tvReviewContent;
            ivProfileImage = itemReviewBinding.ivProfileImage;
            ratingBar = itemReviewBinding.ratingBar;
            ivImg = itemReviewBinding.ivImg;
        }

        public void bind(final Review review) {
            review.fetchInfo();
            tvReviewTitle.setText(review.getTitle());
            tvReviewContent.setText(review.getReviewContent());
            ratingBar.setRating(review.getRating());
            loadImage(review);
        }

        public void loadImage(Review review) {
            if (review.getParseFile() != null) {
                Log.e(TAG, review.getImageUrl());
                GlideHelper.loadImage(review.getImageUrl(), context, ivImg);
            } else {
                Log.e(TAG, "No image to load");
            }
        }
    }

}