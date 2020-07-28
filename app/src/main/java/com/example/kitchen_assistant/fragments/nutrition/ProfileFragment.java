package com.example.kitchen_assistant.fragments.nutrition;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kitchen_assistant.R;
import com.example.kitchen_assistant.databinding.FragmentProfileBinding;
import com.example.kitchen_assistant.helpers.GlideHelper;
import com.example.kitchen_assistant.models.Product;
import com.example.kitchen_assistant.models.User;
import com.example.kitchen_assistant.storage.CurrentProducts;
import com.example.kitchen_assistant.storage.CurrentRecipes;
import com.example.kitchen_assistant.storage.CurrentShoppingList;
import com.parse.ParseUser;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    private final String TAG = "ProfileFragment";
    private User user;

    private int numProducts;
    private int numRecipes;
    private int numShoppingItems;
    private int usingDays;

    private FragmentProfileBinding fragmentProfileBinding;
    private ImageView ivProfileImage;
    private TextView tvName;
    private TextView tvUsername;
    private TextView tvCaloriesGoal;
    private CardView cvProducts;
    private CardView cvRecipes;
    private CardView cvHistory;
    private CardView cvShoppingList;
    private TextView tvNumProducts;
    private TextView tvNumRecipes;
    private TextView tvHistory;
    private TextView tvShoppingItems;

    public ProfileFragment() {
    }

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        String userId = ParseUser.getCurrentUser().getObjectId();
        user = User.fetchFromUserId(userId);
        numProducts = CurrentProducts.getCurrentNumProducts();
        numRecipes = CurrentRecipes.getCurrentNumRecipes();
        numShoppingItems = CurrentShoppingList.getCurrentNumItems();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentProfileBinding = FragmentProfileBinding.inflate(getLayoutInflater());
        ivProfileImage = fragmentProfileBinding.ivProfileImage;
        tvName = fragmentProfileBinding.tvName;
        tvUsername = fragmentProfileBinding.tvUsername;
        tvCaloriesGoal = fragmentProfileBinding.tvCaloriesGoal;
        cvProducts = fragmentProfileBinding.cvProducts;
        cvRecipes = fragmentProfileBinding.cvRecipe;
        cvHistory = fragmentProfileBinding.cvHistory;
        cvShoppingList = fragmentProfileBinding.cvShoppingList;
        tvNumRecipes = fragmentProfileBinding.tvNumRecipes;
        tvNumProducts = fragmentProfileBinding.tvNumProducts;
        tvHistory = fragmentProfileBinding.tvHistory;
        tvShoppingItems = fragmentProfileBinding.tvShoppingItems;

        GlideHelper.loadAvatar(user.getProfileImage().getUrl(), getContext(), ivProfileImage);
        tvUsername.setText(user.getUsername());
        tvName.setText(user.getName());
        tvCaloriesGoal.setText("" + user.getCaloriesGoal());

        tvNumRecipes.setText(numRecipes + " recipes");
        tvNumProducts.setText(numProducts + " products");
        tvShoppingItems.setText(numShoppingItems + " items in shopping list");

        return inflater.inflate(R.layout.fragment_profile, container, false);
    }
}