package com.abdelrahman.rafaat.notesapp.ui.view.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.abdelrahman.rafaat.notesapp.R;
import com.abdelrahman.rafaat.notesapp.databinding.FragmentPasswordsBinding;
import com.abdelrahman.rafaat.notesapp.interfaces.OnIconClickListener;
import com.abdelrahman.rafaat.notesapp.model.Passwords;
import com.abdelrahman.rafaat.notesapp.ui.view.IconsAdapter;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class PasswordsFragment extends BaseFragment implements OnIconClickListener {

    private FragmentPasswordsBinding binding;
    private int iconsSize = 0;
    private int selectedIcon = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPasswordsBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initUI();
        initRecyclerView();
    }

    private void initUI() {
        addTextWatcher(binding.userNameEditText, binding.userNameInputLayout);
        addTextWatcher(binding.passwordEditText, binding.passwordInputLayout);

        binding.saveButton.setOnClickListener(v -> {
            if (validateUserName() & validatePassword()) {
                String userName = binding.userNameEditText.getText().toString().trim();
                String password = binding.passwordEditText.getText().toString().trim();
                String website = binding.websiteNameEditText.getText().toString().trim();
                Passwords passwords = new Passwords(userName, password, website, selectedIcon);
                noteViewModel.savePassword(passwords);
            }
        });
    }

    private void addTextWatcher(TextInputEditText editText, TextInputLayout textInputLayout) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private boolean validateUserName() {
        boolean isValid = false;
        Editable text = binding.userNameEditText.getText();
        if (text != null && !text.toString().trim().isEmpty()) {
            isValid = true;
            binding.userNameInputLayout.setError(null);
        } else {
            binding.userNameInputLayout.setError(getString(R.string.filed_required));
        }
        return isValid;
    }

    private boolean validatePassword() {
        boolean isValid = false;
        Editable text = binding.passwordEditText.getText();
        if (text != null && !text.toString().trim().isEmpty()) {
            isValid = true;
            binding.passwordInputLayout.setError(null);
        } else {
            binding.passwordInputLayout.setError(getString(R.string.filed_required));
        }
        return isValid;
    }

    private void initRecyclerView() {
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(requireContext());
        layoutManager.setFlexDirection(FlexDirection.ROW);
        binding.iconsRecyclerview.setLayoutManager(layoutManager);
        List<Integer> icons = getIconsList();
        iconsSize = icons.size();
        binding.iconsRecyclerview.setAdapter(new IconsAdapter(this, icons));
    }

    private List<Integer> getIconsList() {
        ArrayList<Integer> icons = new ArrayList<>();
        icons.add(R.drawable.ic_adobe_xd);
        icons.add(R.drawable.ic_apple);
        icons.add(R.drawable.ic_behance);
        icons.add(R.drawable.ic_chatgpt);
        icons.add(R.drawable.ic_discord);
        icons.add(R.drawable.ic_dribbble);
        icons.add(R.drawable.ic_drive);
        icons.add(R.drawable.ic_facebook);
        icons.add(R.drawable.ic_figma);
        icons.add(R.drawable.ic_github);
        icons.add(R.drawable.ic_gmail);
        icons.add(R.drawable.ic_instagram);
        icons.add(R.drawable.ic_linkedin);
        icons.add(R.drawable.ic_medium);
        icons.add(R.drawable.ic_messenger);
        icons.add(R.drawable.ic_pinterest);
        icons.add(R.drawable.ic_quora);
        icons.add(R.drawable.ic_snapchat);
        icons.add(R.drawable.ic_stack_overflow);
        icons.add(R.drawable.ic_telegram);
        icons.add(R.drawable.ic_twitter);
        icons.add(R.drawable.ic_whatsapp);
        icons.add(R.drawable.ic_more);
        return icons;
    }

    @Override
    public void onIconClickListener(int icon, int position) {
        selectedIcon = icon;
        if (position == iconsSize - 1) {
            binding.websiteNameInputLayout.setVisibility(View.VISIBLE);
        } else {
            binding.websiteNameInputLayout.setVisibility(View.GONE);
            binding.websiteNameEditText.setText("");
        }
    }
}