package com.abdelrahman.rafaat.notesapp.home.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.abdelrahman.rafaat.notesapp.home.view.OnNotesClickListener;
import com.abdelrahman.rafaat.notesapp.home.viewmodel.NotesViewModelFactory;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.SearchView;

import com.abdelrahman.rafaat.notesapp.databinding.FragmentHomeBinding;
import com.abdelrahman.rafaat.notesapp.home.view.NotesAdapter;
import com.abdelrahman.rafaat.notesapp.home.viewmodel.NoteViewModel;
import com.abdelrahman.rafaat.notesapp.model.Note;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;

import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.abdelrahman.rafaat.notesapp.R;

import com.abdelrahman.rafaat.notesapp.database.LocalSource;
import com.abdelrahman.rafaat.notesapp.model.Repository;


public class HomeFragment extends Fragment implements OnNotesClickListener, PopupMenu.OnMenuItemClickListener {

    private String TAG = "MainActivity";
    private FragmentHomeBinding binding;
    private NotesAdapter adapter;
    private NoteViewModel noteViewModel;
    private NotesViewModelFactory viewModelFactory;
    private List<Note> noteList;
    private Note selectedNote;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
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
        initViewModel();
        observeViewModel();
    }

    private void search() {
        binding.noteSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });
    }

    private void initRecyclerView() {
        adapter = new NotesAdapter(this);
        binding.notesRecyclerview.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        binding.notesRecyclerview.setAdapter(adapter);
        int resId = R.anim.lat;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), resId);
        binding.notesRecyclerview.setLayoutAnimation(animation);
    }

    private void initViewModel() {
        viewModelFactory = new NotesViewModelFactory(
                Repository.getInstance(
                        LocalSource.getInstance(getContext()), getActivity().getApplication()
                ), getActivity().getApplication()
        );

        noteViewModel = new ViewModelProvider(
                this,
                viewModelFactory
        ).get(NoteViewModel.class);

        noteViewModel.getAllNotes();
    }

    private void observeViewModel() {
        noteViewModel.notes.observe(this, notes -> {
            Log.i(TAG, "onChanged: " + notes.size());
            if (notes.isEmpty())
                binding.noNotesLayout.noNotesView.setVisibility(View.VISIBLE);
            else
                binding.noNotesLayout.noNotesView.setVisibility(View.GONE);

            binding.noSearchLayout.noFilesView.setVisibility(View.GONE);
            adapter.setList(notes);
            noteList = notes;

        });
    }

    @Override
    public void onClickListener(Note note) {
        // Navigation.findNavController(getView()).navigate(R.id.show_note_fragment);
        Bundle bundle = new Bundle();
        bundle.putSerializable("NOTE", note);
        Navigation.findNavController(getView()).navigate(R.id.show_note_fragment, bundle);
    }

    @Override
    public void onLongClick(Note note, CardView cardView) {
        selectedNote = note;
        showPopupMenu(cardView);
    }

    private void filter(String title) {
        List<Note> filteredList = new ArrayList<>();

        for (Note note : noteList) {
            if (note.getTitle().toLowerCase().contains(title.toLowerCase())) {
                filteredList.add(note);
            }
        }
        if (filteredList.isEmpty()) {
            binding.noNotesLayout.noNotesView.setVisibility(View.GONE);
            binding.noSearchLayout.noFilesView.setVisibility(View.VISIBLE);
        } else {
            binding.noSearchLayout.noFilesView.setVisibility(View.GONE);
        }
        adapter.setList(filteredList);
    }

    private void showPopupMenu(CardView cardView) {
        PopupMenu popupMenu = new PopupMenu(getContext(), cardView);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.popup_menu);
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.pin_note:
                if (selectedNote.isPinned()) {
                    selectedNote.setPinned(false);
                } else {
                    selectedNote.setPinned(true);
                }
                noteViewModel.updateNote(selectedNote);
                break;
            case R.id.delete_note:
                noteViewModel.deleteNote(selectedNote.getId());
                break;


        }
        return false;
    }
}