package com.example.kitchen_assistant.fragments.profile;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.kitchen_assistant.R;
import com.example.kitchen_assistant.databinding.FragmentEditProfileBinding;
import com.example.kitchen_assistant.models.User;
import com.parse.ParseUser;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditProfileFragment extends DialogFragment {

    private User user;
    private FragmentEditProfileBinding fragmentEditProfileBinding;
    private EditText etName;
    private EditText etUsername;
    private EditText etCaloriesGoal;

    public EditProfileFragment() {
    }

    public static EditProfileFragment newInstance() {
        EditProfileFragment fragment = new EditProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = User.fetchFromUserId(ParseUser.getCurrentUser().getObjectId());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentEditProfileBinding = FragmentEditProfileBinding.inflate(getLayoutInflater());
        etName = fragmentEditProfileBinding.etName;
        etUsername = fragmentEditProfileBinding.etUsername;
        etCaloriesGoal = fragmentEditProfileBinding.etCaloriesGoal;

        etName.setText(user.getName());
        etUsername.setText(user.getUsername());
        etCaloriesGoal.setText((int) user.getCaloriesGoal());

        return fragmentEditProfileBinding.getRoot();
    }
}