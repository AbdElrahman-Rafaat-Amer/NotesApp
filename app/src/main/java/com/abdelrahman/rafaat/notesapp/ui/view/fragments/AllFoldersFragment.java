package com.abdelrahman.rafaat.notesapp.ui.view.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.abdelrahman.rafaat.notesapp.R;
import com.abdelrahman.rafaat.notesapp.databinding.AddFolderDialogBinding;
import com.abdelrahman.rafaat.notesapp.databinding.FragmentAllFoldersBinding;
import com.abdelrahman.rafaat.notesapp.model.Folder;
import com.abdelrahman.rafaat.notesapp.ui.view.FilesAdapter;
import com.abdelrahman.rafaat.notesapp.ui.view.OnFolderClickListener;
import com.abdelrahman.rafaat.notesapp.ui.view.ViewTypes;
import com.abdelrahman.rafaat.notesapp.ui.viewmodel.NoteViewModel;

import java.util.List;

public class AllFoldersFragment extends Fragment implements OnFolderClickListener {

    private FragmentAllFoldersBinding binding;
    private FilesAdapter filesAdapter;
    private String TAG = "AllFoldersFragment";
    private NoteViewModel noteViewModel;

    private List<Folder> folders;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAllFoldersBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initUI();
        initRecyclerView();
        initViewModel();
        observeViewModel();
    }

    private void initUI() {

        binding.backImageView.setOnClickListener(view -> Navigation.findNavController(view).popBackStack());
        binding.restoreFromTrash.setOnClickListener(view -> Log.d(TAG, "initUI: restoreFromTrash clicked"));
        binding.addNewFolder.setOnClickListener(view -> {
            Log.d(TAG, "initUI: addNewFolder clicked");
            showCustomDialog();
        });

    }

    private void showCustomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),  R.style.CustomAlertDialog);
        AddFolderDialogBinding dialogBinding = AddFolderDialogBinding.inflate(getLayoutInflater());
        builder.setView(dialogBinding.getRoot());

        AlertDialog dialog = builder.create();

        // Set up dialog components
        dialogBinding.okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: okButton pressed");
                noteViewModel.addFolder(dialogBinding.editTextFolderName.getText().toString());
                hideKeyboard();
                dialog.cancel();
            }
        });

        dialogBinding.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: cancelButton pressed");
                hideKeyboard();
                dialog.cancel();
            }
        });

        Window window = dialog.getWindow();
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(window.getAttributes());
        layoutParams.gravity = Gravity.BOTTOM;
        window.setAttributes(layoutParams);

        dialogBinding.editTextFolderName.requestFocus();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        View currentFocus = requireActivity().getCurrentFocus();
        if (currentFocus != null) {
            imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
    }
    private void initRecyclerView() {
        filesAdapter = new FilesAdapter(this, ViewTypes.VIEW_TYPE_ITEM_2);
        LinearLayoutManager manager = new LinearLayoutManager(requireContext());
        binding.foldersRecyclerView.setLayoutManager(manager);
        binding.foldersRecyclerView.setAdapter(filesAdapter);
    }

    private void initViewModel() {
        noteViewModel = new ViewModelProvider(requireActivity()).get(NoteViewModel.class);
    }

    private void observeViewModel() {
        noteViewModel.folders.observe(getViewLifecycleOwner(), folders -> {
            filesAdapter.setList(folders);
            this.folders = folders;
        });
    }

    @Override
    public void onFolderClickListener(Folder folder) {
        Log.d(TAG, "initUI: onFolderClickListener folder------>" + folder.toString());
    }
}
