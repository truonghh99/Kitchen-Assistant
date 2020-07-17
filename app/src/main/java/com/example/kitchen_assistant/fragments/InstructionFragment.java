package com.example.kitchen_assistant.fragments;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.kitchen_assistant.R;
import com.example.kitchen_assistant.activities.MainActivity;
import com.example.kitchen_assistant.databinding.FragmentInstructionBinding;
import com.example.kitchen_assistant.databinding.FragmentPreviewShoppingItemBinding;
import com.example.kitchen_assistant.helpers.SpinnerHelper;
import com.example.kitchen_assistant.models.FoodItem;
import com.example.kitchen_assistant.models.Product;
import com.example.kitchen_assistant.models.ShoppingItem;
import com.example.kitchen_assistant.storage.CurrentShoppingList;
import com.parse.ParseUser;

import org.parceler.Parcels;

public class InstructionFragment extends DialogFragment {

    private static final String INSTRUCTION_KEY = "Recipe";
    private static final String TAG = "InstructionFragment";

    private FragmentInstructionBinding fragmentInstructionBinding;
    private TextView tvInstruction;
    private String instruction;

    public InstructionFragment() {
    }

    // Initialize with a product to extract & display its food type
    public static InstructionFragment newInstance(String instruction) {
        InstructionFragment fragment = new InstructionFragment();
        Bundle args = new Bundle();
        args.putString(INSTRUCTION_KEY, instruction);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            instruction = getArguments().getString(INSTRUCTION_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentInstructionBinding = FragmentInstructionBinding.inflate(getLayoutInflater());
        tvInstruction = fragmentInstructionBinding.tvInstruction;
        tvInstruction.setText(instruction);

        return fragmentInstructionBinding.getRoot();
    }
}