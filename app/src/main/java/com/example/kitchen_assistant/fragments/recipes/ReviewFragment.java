package com.example.kitchen_assistant.fragments.recipes;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.kitchen_assistant.R;
import com.example.kitchen_assistant.activities.MainActivity;
import com.example.kitchen_assistant.adapters.RecipeAdapter;
import com.example.kitchen_assistant.adapters.ReviewAdapter;
import com.example.kitchen_assistant.databinding.FragmentRecipeBinding;
import com.example.kitchen_assistant.databinding.FragmentReviewBinding;
import com.example.kitchen_assistant.databinding.FragmentReviewComposeBinding;
import com.example.kitchen_assistant.helpers.RecipeEvaluator;
import com.example.kitchen_assistant.models.Recipe;
import com.example.kitchen_assistant.models.Review;
import com.example.kitchen_assistant.storage.CurrentRecipes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class ReviewFragment extends Fragment {

    private static final String TAG = "ReviewFragment";
    public static final String title = "Reviews";
    private static final String KEY_RECIPE = "Recipe";

    private FragmentReviewBinding fragmentReviewBinding;
    private RecyclerView rvReviews;
    private FloatingActionButton btAdd;
    private List<Review> reviews;
    private Recipe recipe;
    private static ReviewAdapter adapter;

    public ReviewFragment() {
    }

    public static ReviewFragment newInstance(Parcelable recipe) {
        ReviewFragment fragment = new ReviewFragment();
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
        fragmentReviewBinding = FragmentReviewBinding.inflate(getLayoutInflater());
        btAdd = fragmentReviewBinding.btAdd;
        rvReviews = fragmentReviewBinding.rvReviews;

        reviews = recipe.getReviews();
        adapter = new ReviewAdapter(getActivity(), reviews);
        rvReviews.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvReviews.setAdapter(adapter);

        return fragmentReviewBinding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_search_toolbar, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        ((MainActivity) getContext()).getSupportActionBar().setTitle(title);
        SearchView searchView = new SearchView(((MainActivity) getContext()).getSupportActionBar().getThemedContext());
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);
        item.setActionView(searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                final List<Review> filteredModelList = filter(reviews, newText);
                adapter.replaceAll(filteredModelList);
                rvReviews.scrollToPosition(0);
                return true;
            }
        });
        searchView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
    }

    private List<Review> filter(List<Review> reviews, String query) {
        final String lowerCaseQuery = query.toLowerCase();
        final List<Review> filteredModelList = new ArrayList<>();
        for (Review review : reviews) {
            final String text = review.getReviewContent().toLowerCase();
            if (text.contains(lowerCaseQuery)) {
                filteredModelList.add(review);
            }
        }
        return filteredModelList;
    }
}