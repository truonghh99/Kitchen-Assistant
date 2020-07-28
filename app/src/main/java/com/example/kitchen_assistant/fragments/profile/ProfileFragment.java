package com.example.kitchen_assistant.fragments.profile;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kitchen_assistant.R;
import com.example.kitchen_assistant.activities.LoginActivity;
import com.example.kitchen_assistant.activities.MainActivity;
import com.example.kitchen_assistant.databinding.FragmentProfileBinding;
import com.example.kitchen_assistant.fragments.nutrition.RecipeNutritionFragment;
import com.example.kitchen_assistant.fragments.recipes.InstructionFragment;
import com.example.kitchen_assistant.helpers.GlideHelper;
import com.example.kitchen_assistant.models.User;
import com.example.kitchen_assistant.storage.CurrentProducts;
import com.example.kitchen_assistant.storage.CurrentRecipes;
import com.example.kitchen_assistant.storage.CurrentShoppingList;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.parceler.Parcels;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    private static final int REQUEST_CODE = 100;
    private final String TAG = "ProfileFragment";
    private final String TITLE = "Profile";
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
    private ImageView ivEdit;

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
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        ((MainActivity) getContext()).getSupportActionBar().setTitle(TITLE);
        inflater.inflate(R.menu.menu_profile_toolbar, menu);
        MenuItem miLogout = menu.findItem(R.id.miLogout);
        miLogout.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                logOut();
                return true;
            }
        });
    }

    private void logOut() {
        ParseUser.logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.i(TAG, String.valueOf(e));
                    return;
                }
                Log.i(TAG, "Logged out!");
                Toast.makeText(getContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
                goLogIn();
            }
        });
    }

    private void goLogIn() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
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
        ivEdit = fragmentProfileBinding.ivEdit;

        bindFromUserInfo();

        tvNumRecipes.setText(numRecipes + " recipes");
        tvNumProducts.setText(numProducts + " products");
        tvShoppingItems.setText(numShoppingItems + " items in shopping list");

        cvProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToProducts();
            }
        });

        cvRecipes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToRecipe();
            }
        });

        cvShoppingList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToShoppingList();
            }
        });

        ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToEdit();
            }
        });
        return fragmentProfileBinding.getRoot();
    }

    private void goToProducts() {
        MainActivity.bottomNavigation.setSelectedItemId(R.id.miCurrentFood);
    }

    private void goToRecipe() {
        MainActivity.bottomNavigation.setSelectedItemId(R.id.miRecipe);
    }

    private void goToShoppingList() {
        MainActivity.bottomNavigation.setSelectedItemId(R.id.miShoppingList);
    }

    private void goToEdit() {
        DialogFragment dialogFragment = EditProfileFragment.newInstance(Parcels.wrap(user));
        dialogFragment.setTargetFragment(this, REQUEST_CODE);
        dialogFragment.show(getActivity().getSupportFragmentManager(), "Dialog");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        bindFromUserInfo();
    }

    private void bindFromUserInfo() {
        Log.e(TAG, "BINDING");
        GlideHelper.loadAvatar(user.getProfileImage().getUrl(), getContext(), ivProfileImage);
        tvUsername.setText("@" + user.getUsername());
        tvName.setText(user.getName());
        tvCaloriesGoal.setText(user.getCaloriesGoal() + " kcal");
    }
}