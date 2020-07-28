package com.example.kitchen_assistant.fragments.profile;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.kitchen_assistant.R;
import com.example.kitchen_assistant.databinding.FragmentEditProfileBinding;
import com.example.kitchen_assistant.models.User;
import com.parse.ParseUser;

import org.parceler.Parcels;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditProfileFragment extends DialogFragment {
    private static final String KEY_USER = "user";
    private User user;
    private FragmentEditProfileBinding fragmentEditProfileBinding;
    private EditText etName;
    private EditText etUsername;
    private EditText etCaloriesGoal;
    private Button btSave;

    public EditProfileFragment() {
    }

    public static EditProfileFragment newInstance(Parcelable user) {
        EditProfileFragment fragment = new EditProfileFragment();
        Bundle args = new Bundle();
        args.putParcelable(KEY_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = (User) Parcels.unwrap(getArguments().getParcelable(KEY_USER));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentEditProfileBinding = FragmentEditProfileBinding.inflate(getLayoutInflater());
        etName = fragmentEditProfileBinding.etName;
        etUsername = fragmentEditProfileBinding.etUsername;
        etCaloriesGoal = fragmentEditProfileBinding.etCaloriesGoal;
        btSave = fragmentEditProfileBinding.btSave;

        etName.setText(user.getName());
        etUsername.setText(user.getUsername());
        etCaloriesGoal.setText("" + (int) user.getCaloriesGoal());

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveInfo();
            }
        });

        return fragmentEditProfileBinding.getRoot();
    }

    private void saveInfo() {
        String name = etName.getText().toString();
        String username = etUsername.getText().toString();
        Float calories = Float.parseFloat(etCaloriesGoal.getText().toString());

        user.setName(name);
        user.setUsername(username);
        user.setCaloriesGoal(calories);
        user.saveInfo();

        getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_OK, new Intent());
        dismiss();
    }
}