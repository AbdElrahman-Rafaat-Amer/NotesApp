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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.abdelrahman.rafaat.notesapp.R;
import com.abdelrahman.rafaat.notesapp.databinding.FragmentHomeBinding;
import com.abdelrahman.rafaat.notesapp.model.Note;
import com.abdelrahman.rafaat.notesapp.ui.view.NotesAdapter;
import com.abdelrahman.rafaat.notesapp.ui.view.OnNotesClickListener;
import com.abdelrahman.rafaat.notesapp.ui.viewmodel.NoteViewModel;

import java.util.ArrayList;
import java.util.List;

public class ArchivedNotesFragment extends BaseFragment implements OnNotesClickListener {

    private FragmentHomeBinding binding;
    private NoteViewModel noteViewModel;
    private NotesAdapter adapter;
    private List<Note> noteList = new ArrayList<>();
    private boolean isList = false;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initRecyclerView();
        initViewModel();
        observeViewModel();
        onBackPressed();
    }

    private void initRecyclerView() {
        adapter = new NotesAdapter(this);
        setupLayoutManger();
        binding.notesRecyclerview.setAdapter(adapter);
        int resId = R.anim.lat;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), resId);
        binding.notesRecyclerview.setLayoutAnimation(animation);
//        swipeRecyclerView();
    }

    private void initViewModel() {
        noteViewModel = new ViewModelProvider(requireActivity()).get(NoteViewModel.class);
        noteViewModel.getLayoutMangerStyle();
        noteViewModel.getArchivedNotes();
    }

    private void observeViewModel() {
        noteViewModel.archivedNotes.observe(getViewLifecycleOwner(), notes -> {
            List<Note> nonArchivedNotes = notes;
            Log.i("ARCHIVED_NOTES", "observeViewModel:  HomeFragment.notes" + + notes.size());
            if (notes.isEmpty()) {
                binding.noNotesLayout.noNotesView.setVisibility(View.VISIBLE);
            } else {
//                nonArchivedNotes = notes.stream().filter(note -> !note.isArchived()).collect(Collectors.toList());
                binding.noNotesLayout.noNotesView.setVisibility(View.GONE);
            }

            binding.noSearchLayout.noFilesView.setVisibility(View.GONE);
            adapter.setList(nonArchivedNotes);
            noteList = notes;
        });

        noteViewModel.isList.observe(getViewLifecycleOwner(), aBoolean -> {
            isList = aBoolean;
            setupLayoutManger();
        });
    }

    private void setupLayoutManger() {
        if (!isList)
            binding.notesRecyclerview.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        else
            binding.notesRecyclerview.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    public void onBackPressed() {
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
//                if (isSearching) {
//                    binding.noteSearchView.setIconified(true);
//                    binding.noteSearchView.clearFocus();
//                    binding.addNoteFloatingActionButton.setVisibility(View.VISIBLE);
//                    isSearching = false;
//                } else if (isPinned) {
//                    isPinned = false;
//                    adapter.setList(noteList);
//                } else {
                    setEnabled(false);
                    requireActivity().getOnBackPressedDispatcher().onBackPressed();
//                }
            }
        });
    }

    @Override
    public void onClickListener(Note note) {

    }
}
