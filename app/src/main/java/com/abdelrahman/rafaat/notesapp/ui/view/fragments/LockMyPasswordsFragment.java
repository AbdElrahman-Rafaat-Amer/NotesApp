package com.abdelrahman.rafaat.notesapp.ui.view.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import com.abdelrahman.rafaat.notesapp.R;
import com.abdelrahman.rafaat.notesapp.databinding.FragmentLockMyPasswordsBinding;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LockMyPasswordsFragment extends BaseFragment {
    private FragmentLockMyPasswordsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentLockMyPasswordsBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        initUI();
    }

    private void initUI() {
        binding.passwordLottieAnimation.setMinAndMaxProgress(0.1f, 0.8f);

        addTextWatcher(binding.passwordEditText, binding.passwordInputLayout);
        addTextWatcher(binding.confirmPasswordEditText, binding.confirmPasswordInputLayout);
        addTextWatcher(binding.passwordHintEditText, binding.passwordHintInputLayout);

        binding.createButton.setOnClickListener(v -> {
            if (validatePassword() & validateConfirmPassword()) {
                if (validateHintPassword()) {
                    String password = binding.passwordEditText.getText().toString();
                    String hint = binding.passwordHintEditText.getText().toString();
                    noteViewModel.savePassword(password, hint);
                    Navigation.findNavController(binding.getRoot()).navigate(R.id.unlock_my_passwords_fragment);
                }
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

    private boolean validatePassword() {
        boolean isValid = false;
        Editable text = binding.passwordEditText.getText();
        if (text != null && !text.toString().isEmpty()) {
            String password = binding.passwordEditText.getText().toString();
            if (password.length() < 6) {
                binding.passwordInputLayout.setError(getString(R.string.password_error_message));
            } else {
                isValid = true;
                binding.passwordInputLayout.setError(null);
            }
        } else {
            binding.passwordInputLayout.setError(getString(R.string.filed_required));
        }
        return isValid;
    }

    private boolean validateConfirmPassword() {
        boolean isValid = false;
        Editable text = binding.confirmPasswordEditText.getText();
        if (text != null && !text.toString().isEmpty()) {
            String password = binding.passwordEditText.getText().toString();
            String confirmPassword = binding.confirmPasswordEditText.getText().toString();
            if (!confirmPassword.equals(password)) {
                binding.confirmPasswordInputLayout.setError(getString(R.string.confirm_password_error));
            } else {
                isValid = true;
                binding.confirmPasswordInputLayout.setError(null);
            }
        } else {
            binding.confirmPasswordInputLayout.setError(getString(R.string.filed_required));
        }
        return isValid;
    }

    private boolean validateHintPassword() {
        boolean isValid = false;
        Editable text = binding.passwordHintEditText.getText();
        if (text != null && !text.toString().isEmpty()) {
            isValid = true;
        } else {
            if (binding.passwordHintInputLayout.getHelperText() == getString(R.string.hint_password_empty_confirm)) {
                isValid = true;
            } else {
                binding.passwordHintInputLayout.setHelperTextTextAppearance(R.style.MyHelperTextStyle);
                binding.passwordHintInputLayout.setHelperText(getString(R.string.hint_password_empty_confirm));
            }
        }
        return isValid;
    }


}