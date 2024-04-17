package com.abdelrahman.rafaat.notesapp.ui.view.fragments;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import com.abdelrahman.rafaat.notesapp.R;
import com.abdelrahman.rafaat.notesapp.databinding.FragmentAddPasswordsBinding;
import com.abdelrahman.rafaat.notesapp.interfaces.OnIconClickListener;
import com.abdelrahman.rafaat.notesapp.model.Passwords;
import com.abdelrahman.rafaat.notesapp.ui.view.IconsAdapter;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class AddPasswordsFragment extends BaseFragment implements OnIconClickListener {

    private FragmentAddPasswordsBinding binding;
    private final List<Integer> icons = new ArrayList<>();
    private IconsAdapter iconsAdapter;
    private int selectedIcon = R.drawable.ic_more;
    private Passwords currentPassword = null;
    public static final String EXTRA_PASSWORD = "EXTRA_PASSWORD";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddPasswordsBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initUI();
        initRecyclerView();
        loadPasswordFromBundle();
    }

    private void initUI() {
        addTextWatcher(binding.userNameEditText, binding.userNameInputLayout);
        addTextWatcher(binding.passwordEditText, binding.passwordInputLayout);

        binding.saveButton.setOnClickListener(v -> {
            if (validateUserName() & validatePassword()) {
                String userName = binding.userNameEditText.getText().toString().trim();
                String password = binding.passwordEditText.getText().toString().trim();
                String website = binding.websiteNameEditText.getText().toString().trim();
                if (currentPassword == null) {
                    Passwords passwords = new Passwords(userName, password, website, selectedIcon);
                    noteViewModel.savePassword(passwords);
                } else {
                    currentPassword.setUserName(userName);
                    currentPassword.setPassword(password);
                    currentPassword.setWebsiteName(website);
                    currentPassword.setIcon(selectedIcon);
                    noteViewModel.updatePassword(currentPassword);
                }
                Navigation.findNavController(requireView()).popBackStack();
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
        layoutManager.setJustifyContent(JustifyContent.CENTER);
        binding.iconsRecyclerview.setLayoutManager(layoutManager);
        setupIconsList();
        iconsAdapter = new IconsAdapter(this, icons);
        binding.iconsRecyclerview.setAdapter(iconsAdapter);
    }

    private void setupIconsList() {
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
    }

    private void loadPasswordFromBundle() {
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(AddPasswordsFragment.EXTRA_PASSWORD)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                currentPassword = bundle.getSerializable(AddPasswordsFragment.EXTRA_PASSWORD, Passwords.class);
            } else {
                currentPassword = (Passwords) bundle.getSerializable(AddPasswordsFragment.EXTRA_PASSWORD);
            }

            if (currentPassword != null) {
                binding.userNameEditText.setText(currentPassword.getUserName());
                binding.passwordEditText.setText(currentPassword.getPassword());
                binding.websiteNameEditText.setText(currentPassword.getWebsiteName());
                selectedIcon = currentPassword.getIcon();
                iconsAdapter.setIconSelected(selectedIcon);
            }
        }
    }

    @Override
    public void onIconClickListener(int position) {
        selectedIcon = icons.get(position);
        if (position == icons.size() - 1) {
            binding.websiteNameInputLayout.setVisibility(View.VISIBLE);
        } else {
            binding.websiteNameInputLayout.setVisibility(View.GONE);
            binding.websiteNameEditText.setText("");
        }
    }

    @Override
    public void onDeleteClickListener(int position) {

    }
}