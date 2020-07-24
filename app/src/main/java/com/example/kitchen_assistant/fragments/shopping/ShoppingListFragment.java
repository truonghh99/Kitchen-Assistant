package com.example.kitchen_assistant.fragments.shopping;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.DialogFragment;
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
import com.example.kitchen_assistant.adapters.ShoppingListAdapter;
import com.example.kitchen_assistant.databinding.FragmentShoppingListBinding;
import com.example.kitchen_assistant.models.ShoppingItem;
import com.example.kitchen_assistant.storage.CurrentShoppingList;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
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
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentShoppingListBinding = FragmentShoppingListBinding.inflate(getLayoutInflater());
        btAdd = fragmentShoppingListBinding.btAdd;
        btSearch = fragmentShoppingListBinding.btSearch;
        rvShoppingList = fragmentShoppingListBinding.rvShoppingList;

        // Set up recycler view & adapter
        items = CurrentShoppingList.items;
        adapter = new ShoppingListAdapter(getActivity(), items);
        rvShoppingList.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvShoppingList.setAdapter(adapter);

        setUpSlideToRemove();

        // Add new shopping item
        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToPreviewItem();
            }
        });

        return fragmentShoppingListBinding.getRoot();
    }

    private void setUpSlideToRemove() {
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
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_search_toolbar, menu);
        ((MainActivity) getContext()).getSupportActionBar().setTitle(title);
        setUpSearchView(menu);
    }

    // Allow user to narrow down shopping list using search bar
    private void setUpSearchView(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_search);
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
                final List<ShoppingItem> filteredModelList = filter(items, newText);
                adapter.replaceAll(filteredModelList);
                rvShoppingList.scrollToPosition(0);
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

    // Filter shopping list based on query in search bar
    private List<ShoppingItem> filter(List<ShoppingItem> items, String query) {
        final String lowerCaseQuery = query.toLowerCase();
        final List<ShoppingItem> filteredModelList = new ArrayList<>();
        for (ShoppingItem item : items) {
            final String text = item.getName().toLowerCase();
            if (text.contains(lowerCaseQuery)) {
                filteredModelList.add(item);
            }
        }
        return filteredModelList;
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