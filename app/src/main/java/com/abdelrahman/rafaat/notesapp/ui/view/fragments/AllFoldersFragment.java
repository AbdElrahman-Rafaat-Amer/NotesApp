package com.abdelrahman.rafaat.notesapp.ui.view.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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
        View customView = getLayoutInflater().inflate(R.layout.add_folder_dialog, null);
        builder.setView(customView);

        // Set up dialog components
        TextView textViewTitle = customView.findViewById(R.id.textViewTitle);
        EditText editTextFolderName = customView.findViewById(R.id.editTextFolderName);
        Button buttonOk = customView.findViewById(R.id.ok_button);
        Button buttonCancel = customView.findViewById(R.id.cancel_button);

        // Customize as needed

        // Create and show the dialog
        AlertDialog dialog = builder.create();

        // Set dialog to appear at the bottom
        Window window = dialog.getWindow();
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(window.getAttributes());
        layoutParams.gravity = Gravity.BOTTOM;
        window.setAttributes(layoutParams);

        // Show the keyboard and focus on the EditText
        editTextFolderName.requestFocus();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
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
