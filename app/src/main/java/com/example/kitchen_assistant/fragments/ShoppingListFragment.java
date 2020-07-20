package com.example.kitchen_assistant.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.kitchen_assistant.adapters.ShoppingListAdapter;
import com.example.kitchen_assistant.databinding.FragmentShoppingListBinding;
import com.example.kitchen_assistant.models.Recipe;
import com.example.kitchen_assistant.models.ShoppingItem;
import com.example.kitchen_assistant.storage.CurrentRecipes;
import com.example.kitchen_assistant.storage.CurrentShoppingList;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.parceler.Parcels;

import java.util.List;

public class ShoppingListFragment extends Fragment {

    private static final String TAG = "ShoppingListFragment";
    public static final String title = "Shopping List";

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

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                ShoppingItem item = items.get(viewHolder.getAdapterPosition());
                CurrentShoppingList.removeItem(item);
                Toast.makeText(getContext(), "Item removed from your shopping list", Toast.LENGTH_SHORT).show();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(rvShoppingList);


        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToPreviewItem();
            }
        });

        return fragmentShoppingListBinding.getRoot();
    }


    public static void notifyDataChange() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public void goToPreviewItem() {
        DialogFragment dialogFragment = PreviewShoppingItemFragment.newInstance("", 0, "unit");
        dialogFragment.show(getActivity().getSupportFragmentManager(), "Dialog");
    }
}