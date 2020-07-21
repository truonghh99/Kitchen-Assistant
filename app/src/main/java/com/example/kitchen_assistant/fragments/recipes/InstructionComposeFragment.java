package com.example.kitchen_assistant.fragments.recipes;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.os.Parcelable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.kitchen_assistant.databinding.FragmentInstructionBinding;
import com.example.kitchen_assistant.databinding.FragmentInstructionComposeBinding;
import com.example.kitchen_assistant.databinding.FragmentRecipeComposeBinding;
import com.example.kitchen_assistant.models.Recipe;

import org.parceler.Parcels;

public class InstructionComposeFragment extends DialogFragment {

    private static final String TAG = "InstructionCompose";
    private static final String KEY_RECIPE = "RECIPE";

    private FragmentInstructionComposeBinding fragmentInstructionComposeBinding;
    private EditText etInstruction;
    private Button btSave;
    private Recipe recipe;

    public InstructionComposeFragment() {
    }

    // Initialize with a product to extract & display its food type
    public static InstructionComposeFragment newInstance(Parcelable recipe) {
        InstructionComposeFragment fragment = new InstructionComposeFragment();
        Bundle args = new Bundle();
        args.putParcelable(KEY_RECIPE, recipe);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getArguments() != null) {
            recipe = Parcels.unwrap(getArguments().getParcelable(KEY_RECIPE));
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentInstructionComposeBinding = FragmentInstructionComposeBinding.inflate(getLayoutInflater());
        etInstruction = fragmentInstructionComposeBinding.etInstruction;
        btSave = fragmentInstructionComposeBinding.btSave;

        etInstruction.setText(recipe.getInstructions());
        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String instructions = etInstruction.getText().toString();
                recipe.setInstructions(instructions);
                dismiss();
            }
        });

        return fragmentInstructionComposeBinding.getRoot();
    }
}