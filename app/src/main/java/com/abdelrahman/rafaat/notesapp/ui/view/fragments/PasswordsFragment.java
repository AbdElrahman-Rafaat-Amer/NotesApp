package com.abdelrahman.rafaat.notesapp.ui.view.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.abdelrahman.rafaat.notesapp.R;
import com.abdelrahman.rafaat.notesapp.databinding.FragmentPasswordsBinding;
import com.abdelrahman.rafaat.notesapp.interfaces.OnIconClickListener;
import com.abdelrahman.rafaat.notesapp.model.Passwords;
import com.abdelrahman.rafaat.notesapp.ui.view.PasswordsAdapter;

import java.util.ArrayList;
import java.util.List;


public class PasswordsFragment extends BaseFragment implements OnIconClickListener {

    private FragmentPasswordsBinding binding;
    protected PasswordsAdapter passwordsAdapter;
    private List<Passwords> passwords = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPasswordsBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.addPasswordFloatingActionButton.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.add_passwords_fragment)
        );
        initRecyclerView();
        observeViewModel();
    }

    private void initRecyclerView() {
        passwordsAdapter = new PasswordsAdapter(this);
        binding.passwordsRecyclerview.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.passwordsRecyclerview.setAdapter(passwordsAdapter);
        int resId = R.anim.lat;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), resId);
        binding.passwordsRecyclerview.setLayoutAnimation(animation);
    }

    private void observeViewModel() {
        noteViewModel.getAllPasswords();
        noteViewModel.getPasswords().observe(getViewLifecycleOwner(), passwords -> {
            if (passwords.isEmpty()) {
                Log.d("TAG", "observeViewModel: passwords.isEmpty()");
            } else {
                Log.d("TAG", "observeViewModel: passwords.size() " + passwords.size());
            }

            passwordsAdapter.setList(passwords);
            this.passwords = passwords;
        });
    }

    @Override
    protected void onBackPressed() {
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                setEnabled(false);
                requireActivity().getOnBackPressedDispatcher().onBackPressed();
            }
        });
    }

    @Override
    public void onIconClickListener(int position) {
        Log.d("TAG", "onIconClickListener: " + passwords.get(position));
        Navigation.findNavController(binding.getRoot()).navigate(R.id.add_passwords_fragment);
    }
}