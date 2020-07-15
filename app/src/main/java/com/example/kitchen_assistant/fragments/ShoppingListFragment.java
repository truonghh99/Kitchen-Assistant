package com.example.kitchen_assistant.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kitchen_assistant.adapters.ShoppingListAdapter;
import com.example.kitchen_assistant.databinding.FragmentShoppingListBinding;
import com.example.kitchen_assistant.models.ShoppingItem;
import com.example.kitchen_assistant.storage.CurrentShoppingList;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

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