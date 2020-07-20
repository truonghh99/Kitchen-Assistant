package com.example.kitchen_assistant.fragments.recipes;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.kitchen_assistant.adapters.RecipeAdapter;
import com.example.kitchen_assistant.databinding.FragmentRecipeBinding;
import com.example.kitchen_assistant.models.Recipe;
import com.example.kitchen_assistant.storage.CurrentRecipes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class RecipeFragment extends Fragment {

    private static final String TAG = "RecipeFragment";
    public static final String title = "My Recipes";

    private FragmentRecipeBinding fragmentRecipeBinding;
    private RecyclerView rvRecipe;
    private FloatingActionButton btSearch;
    private FloatingActionButton btAdd;
    private FloatingActionButton btMenuOpen;
    private List<Recipe> recipes;
    private static RecipeAdapter adapter;

    public RecipeFragment() {
    }

    public static RecipeFragment newInstance() {
        RecipeFragment fragment = new RecipeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentRecipeBinding = FragmentRecipeBinding.inflate(getLayoutInflater());
        btAdd = fragmentRecipeBinding.btAdd;
        btSearch = fragmentRecipeBinding.btSearch;
        rvRecipe = fragmentRecipeBinding.rvRecipe;
        btMenuOpen = fragmentRecipeBinding.btMenuOpen;

        recipes = CurrentRecipes.recipes;
        adapter = new RecipeAdapter(getActivity(), recipes);
        rvRecipe.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvRecipe.setAdapter(adapter);

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

        btMenuOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openOrCloseFabMenu();
            }
        });

        return fragmentRecipeBinding.getRoot();
    }

    private void openOrCloseFabMenu() {
        if (btAdd.getVisibility() == View.INVISIBLE) {
            btAdd.setVisibility(View.VISIBLE);
            btSearch.setVisibility(View.VISIBLE);
        } else {
            btAdd.setVisibility(View.INVISIBLE);
            btSearch.setVisibility(View.INVISIBLE);
        }
    }

    public static void notifyDataChange() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}