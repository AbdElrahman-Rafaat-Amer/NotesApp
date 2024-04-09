package com.abdelrahman.rafaat.notesapp.ui.view.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import com.abdelrahman.rafaat.notesapp.R;
import com.abdelrahman.rafaat.notesapp.databinding.FragmentUnLockMyPasswordsBinding;
import com.abdelrahman.rafaat.notesapp.utils.SpannableTextBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class UnLockMyPasswordsFragment extends BaseFragment {
    private FragmentUnLockMyPasswordsBinding binding;
    private String password = "";
    private String hint = "";
    private int wrongPasswordCounter = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentUnLockMyPasswordsBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Pair<String, String> passwordAndHint = noteViewModel.getPasswordAndHint();
        password = passwordAndHint.first;
        hint = passwordAndHint.second;
        if (password.isEmpty()) {
            Navigation.findNavController(binding.getRoot()).navigate(R.id.lock_my_passwords_fragment);
        }

        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        initUI();
    }

    private void initUI() {
        binding.passwordLottieAnimation.setMinAndMaxProgress(0.1f, 0.8f);

        addTextWatcher(binding.passwordEditText, binding.passwordInputLayout);

        binding.continueButton.setOnClickListener(v -> {
            if (validatePassword()) {
                Navigation.findNavController(binding.getRoot()).navigate(R.id.passwords_fragment);
            } else {
                if (wrongPasswordCounter == 3) {
                    showHintView();
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
            String enteredPassword = binding.passwordEditText.getText().toString();
            if (enteredPassword.equals(password)) {
                isValid = true;
                binding.passwordInputLayout.setError(null);
            } else {
                wrongPasswordCounter++;
                binding.passwordInputLayout.setError(getString(R.string.wrong_password));
            }
        } else {
            binding.passwordInputLayout.setError(getString(R.string.filed_required));
        }
        return isValid;
    }

    private void showHintView() {
        binding.passwordHintTextView.setVisibility(View.VISIBLE);
        SpannableTextBuilder textBuilder = new SpannableTextBuilder();
        textBuilder.append(binding.passwordHintTextView.getText().toString());
        textBuilder.append(" ");
        textBuilder.appendAndApply(hint, new UnderlineSpan()
                , new ForegroundColorSpan(getResources().getColor(R.color.mainColor, null)));
        binding.passwordHintTextView.setText(textBuilder.getSpannableText());

        Animation bottomAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.splash_screen_bottom_animation);
        binding.passwordHintTextView.setAnimation(bottomAnim);
    }
}