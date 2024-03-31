package com.abdelrahman.rafaat.notesapp.ui.view.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.abdelrahman.rafaat.notesapp.R;
import com.abdelrahman.rafaat.notesapp.databinding.FragmentHomeBinding;
import com.abdelrahman.rafaat.notesapp.interfaces.OnItemSwipedListener;
import com.abdelrahman.rafaat.notesapp.model.Note;
import com.abdelrahman.rafaat.notesapp.ui.view.NotesAdapter;
import com.abdelrahman.rafaat.notesapp.ui.view.OnNotesClickListener;
import com.abdelrahman.rafaat.notesapp.ui.viewmodel.NoteViewModel;
import com.abdelrahman.rafaat.notesapp.utils.SwipeItemTouchHelper;

import java.util.ArrayList;
import java.util.List;

public class ArchivedNotesFragment extends BaseFragment implements OnNotesClickListener, OnItemSwipedListener {
    private FragmentHomeBinding binding;
    private NoteViewModel noteViewModel;
    private NotesAdapter adapter;
    private List<Note> noteList = new ArrayList<>();
    private Note selectedNote;
    private AlertDialog alertDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.addNoteFloatingActionButton.setVisibility(View.GONE);
        binding.noNotesLayout.noNotesTextView.setText(R.string.not_archived_notes);
        initRecyclerView();
        initViewModel();
        observeViewModel();
        onBackPressed();
    }

    private void initRecyclerView() {
        adapter = new NotesAdapter(this);
        binding.notesRecyclerview.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.notesRecyclerview.setAdapter(adapter);
        int resId = R.anim.lat;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), resId);
        binding.notesRecyclerview.setLayoutAnimation(animation);
        swipeRecyclerview();
    }

    private void swipeRecyclerview() {
        SwipeItemTouchHelper archiveItemTouchHelper = new SwipeItemTouchHelper(requireContext(), this);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(archiveItemTouchHelper);
        itemTouchHelper.attachToRecyclerView(binding.notesRecyclerview);
    }

    private void initViewModel() {
        noteViewModel = new ViewModelProvider(requireActivity()).get(NoteViewModel.class);
        noteViewModel.getArchivedNotes();
    }

    private void observeViewModel() {
        noteViewModel.archivedNotes.observe(getViewLifecycleOwner(), notes -> {
            if (notes.isEmpty()) {
                binding.noNotesLayout.noNotesView.setVisibility(View.VISIBLE);
            } else {
                binding.noNotesLayout.noNotesView.setVisibility(View.GONE);
            }

            binding.noSearchLayout.noFilesView.setVisibility(View.GONE);
            noteList = notes;
            adapter.setList(notes);
        });
    }

    public void onBackPressed() {
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                setEnabled(false);
                requireActivity().getOnBackPressedDispatcher().onBackPressed();
            }
        });
    }

    private void showAlertDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);

        builder.setMessage(getString(R.string.remove_note))
                .setPositiveButton(R.string.remove, (dialog, which) -> noteViewModel.deleteNote(selectedNote.getId()))
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    alertDialog.dismiss();
                    adapter.notifyItemChanged(position);
                });

        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    @Override
    public void onNoteClickListener(Note note) {

    }

    @Override
    public void onSwiped(int swipedPosition, int direction) {
        selectedNote = noteList.get(swipedPosition);
        if (direction == ItemTouchHelper.LEFT) {
            showAlertDialog(swipedPosition);
        } else if (direction == ItemTouchHelper.RIGHT) {
            selectedNote.setArchived(false);
            noteViewModel.updateNote(selectedNote);
            adapter.notifyItemChanged(swipedPosition);
        }
    }
}
