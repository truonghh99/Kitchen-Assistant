package com.example.kitchen_assistant.fragments.recipes;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.example.kitchen_assistant.databinding.FragmentRecipeBinding;
import com.example.kitchen_assistant.fragments.profile.ProfileFragment;
import com.example.kitchen_assistant.helpers.FabAnimationHelper;
import com.example.kitchen_assistant.models.Recipe;
import com.example.kitchen_assistant.storage.CurrentRecipes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class RecipeFragment extends Fragment {

    private static final String TAG = "RecipeFragment";
    public static final String title = "My Recipes";
    private final String DEFAULT_CODE = "Default code";

    private FragmentRecipeBinding fragmentRecipeBinding;
    private RecyclerView rvRecipe;
    private FloatingActionButton btSearch;
    private FloatingActionButton btAdd;
    private FloatingActionButton btMenuOpen;
    private static List<Recipe> recipes;
    private static RecipeAdapter adapter;
    private Boolean fabMenuOpen;

    public RecipeFragment() {
    }

    public static RecipeFragment newInstance() {
        RecipeFragment fragment = new RecipeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentRecipeBinding = FragmentRecipeBinding.inflate(getLayoutInflater());
        btAdd = fragmentRecipeBinding.btAdd;
        btSearch = fragmentRecipeBinding.btSearch;
        rvRecipe = fragmentRecipeBinding.rvRecipe;
        btMenuOpen = fragmentRecipeBinding.btMenuOpen;
        fabMenuOpen = false;

        // Set up recycler view & adapter
        recipes = CurrentRecipes.recipes;
        adapter = new RecipeAdapter(getActivity(), recipes);
        rvRecipe.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvRecipe.setAdapter(adapter);

        setUpSlideToRemove();

        // Open or close floating menu
        btMenuOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openOrCloseFabMenu();
            }
        });

        // Add new recipe to library (manual input)
        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Recipe recipe = new Recipe();
                recipe.setCode(DEFAULT_CODE);
                RecipeComposeFragment recipeComposeFragment = RecipeComposeFragment.newInstance(Parcels.wrap(recipe));
                MainActivity.switchFragment(recipeComposeFragment);
            }
        });

        return fragmentRecipeBinding.getRoot();
    }

    // Slide to remove recipe from library
    private void setUpSlideToRemove() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                Recipe recipe = recipes.get(viewHolder.getAdapterPosition());
                CurrentRecipes.removeRecipe(recipe);
                Toast.makeText(getContext(), "Recipe removed from your library", Toast.LENGTH_SHORT).show();
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(rvRecipe);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_main_toolbar, menu);
        ((MainActivity) getContext()).getSupportActionBar().setTitle(title);
        setUpSearchView(menu);
        setUpProfile(menu);
    }

    // Allow user to narrow down list of recipes
    private void setUpSearchView(Menu menu) {
        MenuItem item = menu.findItem(R.id.miSearch);
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
                final List<Recipe> filteredModelList = filter(recipes, newText);
                adapter.replaceAll(filteredModelList);
                rvRecipe.scrollToPosition(0);
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

    // Modify list of recipes based on given query in search bar
    private List<Recipe> filter(List<Recipe> recipes, String query) {
        if (query == "") {
            adapter.replaceAll(this.recipes);
        }
        final String lowerCaseQuery = query.toLowerCase();
        final List<Recipe> filteredModelList = new ArrayList<>();
        for (Recipe recipe : recipes) {
            final String text = recipe.getName().toLowerCase();
            if (text.contains(lowerCaseQuery)) {
                filteredModelList.add(recipe);
            }
        }
        return filteredModelList;
    }

    private void setUpProfile(Menu menu) {
        MenuItem miProfile = menu.findItem(R.id.miProfile);
        miProfile.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                goToProfile();
                return true;
            }
        });
    }

    private void goToProfile() {
        ProfileFragment profileFragment = ProfileFragment.newInstance();
        MainActivity.switchFragment(profileFragment);
    }

    private void openOrCloseFabMenu() {
        if (!fabMenuOpen) {
            FabAnimationHelper.showFab(btAdd, getContext());
            FabAnimationHelper.showFab(btSearch, getContext());
            FabAnimationHelper.rotateOpenFab(btMenuOpen, getContext());
            fabMenuOpen = true;
        } else {
            FabAnimationHelper.hideFab(btAdd, getContext());
            FabAnimationHelper.hideFab(btSearch, getContext());
            FabAnimationHelper.rotateCloseFab(btMenuOpen, getContext());
            fabMenuOpen = false;
        }
    }

    public static void notifyDataChange() {
        if (adapter != null) {
            adapter.replaceAll(recipes);
            adapter.notifyDataSetChanged();
        }
    }
}