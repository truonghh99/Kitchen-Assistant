package com.example.kitchen_assistant.fragments.recipes;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kitchen_assistant.databinding.FragmentInstructionBinding;

public class InstructionFragment extends DialogFragment {

    private static final String INSTRUCTION_KEY = "Recipe";
    private static final String TAG = "InstructionFragment";

    private FragmentInstructionBinding fragmentInstructionBinding;
    private TextView tvInstruction;
    private String instruction;

    public InstructionFragment() {
    }

    // Initialize with an instruction to display
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
        tvInstruction.setText(Html.fromHtml(instruction));

        return fragmentInstructionBinding.getRoot();
    }
}