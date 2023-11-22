package com.abdelrahman.rafaat.notesapp.ui.view.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
        binding.addNewFolder.setOnClickListener(view -> Log.d(TAG, "initUI: addNewFolder clicked"));

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

    }
}
