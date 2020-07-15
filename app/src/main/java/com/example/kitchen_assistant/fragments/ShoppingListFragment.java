package com.example.kitchen_assistant.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.os.Parcel;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kitchen_assistant.R;
import com.example.kitchen_assistant.activities.MainActivity;
import com.example.kitchen_assistant.adapters.CurrentFoodAdapter;
import com.example.kitchen_assistant.adapters.RecipeAdapter;
import com.example.kitchen_assistant.adapters.ShoppingListAdapter;
import com.example.kitchen_assistant.clients.BarcodeReader;
import com.example.kitchen_assistant.clients.OpenFoodFacts;
import com.example.kitchen_assistant.databinding.FragmentCurrentFoodBinding;
import com.example.kitchen_assistant.databinding.FragmentRecipeBinding;
import com.example.kitchen_assistant.databinding.FragmentShoppingListBinding;
import com.example.kitchen_assistant.models.Product;
import com.example.kitchen_assistant.models.Recipe;
import com.example.kitchen_assistant.models.ShoppingItem;
import com.example.kitchen_assistant.storage.CurrentProducts;
import com.example.kitchen_assistant.storage.CurrentRecipes;
import com.example.kitchen_assistant.storage.CurrentShoppingList;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.parceler.Parcels;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ShoppingListFragment extends Fragment {

    private static final String TAG = "ShoppingListFragment";

    private FragmentShoppingListBinding fragmentShoppingListBinding;
    private RecyclerView rvShoppingList;
    private FloatingActionButton btSearch;
    private FloatingActionButton btAdd;
    private List<ShoppingItem> items;
    private static ShoppingListAdapter adapter;

    public ShoppingListFragment () {
    }

    public static ShoppingListFragment newInstance() {
        ShoppingListFragment fragment = new ShoppingListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentShoppingListBinding = FragmentShoppingListBinding.inflate(getLayoutInflater());
        btAdd = fragmentShoppingListBinding.btAdd;
        btSearch = fragmentShoppingListBinding.btSearch;
        rvShoppingList = fragmentShoppingListBinding.rvShoppingList;

        items = CurrentShoppingList.items;
        Log.e(TAG, "Displaying " + String.valueOf(items.size()) + " items");

        adapter = new ShoppingListAdapter(getActivity(), items);
        rvShoppingList.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvShoppingList.setAdapter(adapter);

        return fragmentShoppingListBinding.getRoot();
    }


    public static void notifyDataChange() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}