package com.abdelrahman.rafaat.notesapp.ui.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.SearchView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.abdelrahman.rafaat.notesapp.R;
import com.abdelrahman.rafaat.notesapp.databinding.FragmentHomeBinding;
import com.abdelrahman.rafaat.notesapp.interfaces.OnNotesClickListener;
import com.abdelrahman.rafaat.notesapp.model.Note;
import com.abdelrahman.rafaat.notesapp.ui.view.NotesAdapter;
import com.abdelrahman.rafaat.notesapp.utils.MyItemTouchHelperCallback;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends BaseFragment implements OnNotesClickListener {

    private FragmentHomeBinding binding;
    private List<Note> noteList = new ArrayList<>();
    private boolean isSearching = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.addNoteFloatingActionButton.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_home_to_addNote)
        );

        search();
        initRecyclerView();
        observeViewModel();
        onBackPressed();
        noteViewModel.setCurrentNote(null);

    }

    private void search() {
        binding.noteSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (noteList.isEmpty() && !newText.trim().isEmpty()) {
                    showSnackBar(binding.rootView, getString(R.string.no_notes));
                } else {
                    isSearching = true;
                    filter(newText);
                }
                return false;
            }
        });
    }

    private void filter(String title) {
        List<Note> filteredList = new ArrayList<>();

        for (Note note : noteList) {
            if (note.getTitle().toLowerCase().contains(title.toLowerCase()))
                filteredList.add(note);
        }

        if (filteredList.isEmpty()) {
            binding.noNotesLayout.noNotesView.setVisibility(View.GONE);
            binding.noSearchLayout.noFilesView.setVisibility(View.VISIBLE);
        } else
            binding.noSearchLayout.noFilesView.setVisibility(View.GONE);
        adapter.setList(filteredList);

    }

    private void initRecyclerView() {
        adapter = new NotesAdapter(this);
        setupLayoutManger(true);
        binding.notesRecyclerview.setAdapter(adapter);
        int resId = R.anim.lat;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), resId);
        binding.notesRecyclerview.setLayoutAnimation(animation);
        swipeRecyclerView();
    }

    private void setupLayoutManger(boolean isList) {
        if (isList) {
            binding.notesRecyclerview.setLayoutManager(new LinearLayoutManager(requireContext()));
        } else {
            binding.notesRecyclerview.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        }
    }

    private void observeViewModel() {
        noteViewModel.getAllNotes();
        noteViewModel.getNotes().observe(getViewLifecycleOwner(), notes -> {
            if (notes.isEmpty()) {
                binding.noNotesLayout.noNotesView.setVisibility(View.VISIBLE);
            } else {
                binding.noNotesLayout.noNotesView.setVisibility(View.GONE);
            }

            binding.noSearchLayout.noFilesView.setVisibility(View.GONE);
            adapter.setList(notes);
            noteList = notes;
        });

        noteViewModel.isListView.observe(getViewLifecycleOwner(), this::setupLayoutManger);
    }

    @Override
    protected void onBackPressed() {
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (isSearching) {
                    binding.noteSearchView.setIconified(true);
                    binding.noteSearchView.clearFocus();
                    binding.addNoteFloatingActionButton.setVisibility(View.VISIBLE);
                    isSearching = false;
                } else {
                    setEnabled(false);
                    requireActivity().getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
    }

    private void swipeRecyclerView() {
        MyItemTouchHelperCallback simpleCallback = new MyItemTouchHelperCallback(requireContext(), binding.notesRecyclerview) {
            @Override
            public void deleteButtonPressed(int position) {
                selectedNote = noteList.get(position);
                showAlertDialog(position);
            }

            @Override
            public void archiveButtonPressed(int position) {
                selectedNote = noteList.get(position);
                selectedNote.setArchived(!selectedNote.isArchived());
                noteViewModel.updateNote(selectedNote);
            }

            @Override
            public void pinButtonPressed(int position) {
                selectedNote = noteList.get(position);
                selectedNote.setPinned(!selectedNote.isPinned());
                noteViewModel.updateNote(selectedNote);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(binding.notesRecyclerview);
    }

    @Override
    public void onNoteClickListener(Note note) {
        noteViewModel.setCurrentNote(note);
        if (note.getPassword().isEmpty())
            Navigation.findNavController(requireView()).navigate(R.id.show_note_fragment);
        else
            Navigation.findNavController(requireView()).navigate(R.id.password_fragment);
    }

}