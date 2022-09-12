package com.abdelrahman.rafaat.notesapp.home.view;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.abdelrahman.rafaat.notesapp.home.viewmodel.NotesViewModelFactory;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.SearchView;

import com.abdelrahman.rafaat.notesapp.databinding.FragmentHomeBinding;
import com.abdelrahman.rafaat.notesapp.home.viewmodel.NoteViewModel;
import com.abdelrahman.rafaat.notesapp.model.Note;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;


import android.util.Log;

import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.abdelrahman.rafaat.notesapp.R;

import com.abdelrahman.rafaat.notesapp.database.LocalSource;
import com.abdelrahman.rafaat.notesapp.model.Repository;
import com.google.android.material.snackbar.Snackbar;


public class HomeFragment extends Fragment implements OnNotesClickListener, PopupMenu.OnMenuItemClickListener {

    private static final String TAG = "HomeFragment";
    private FragmentHomeBinding binding;
    private NotesAdapter adapter;
    private NoteViewModel noteViewModel;
    private List<Note> noteList;
    private Note selectedNote;
    private boolean isList = false;


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
        initMenu();
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
        setupLayoutManger();
        binding.notesRecyclerview.setAdapter(adapter);
        int resId = R.anim.lat;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), resId);
        binding.notesRecyclerview.setLayoutAnimation(animation);
    }

    private void setupLayoutManger() {
        Log.i(TAG, "setupLayoutManger: ----------------");
        if (!isList)
            binding.notesRecyclerview.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        else
            binding.notesRecyclerview.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void initMenu() {
        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.setting_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.list_note:
                        if (noteList.isEmpty())
                            showSnackBar();
                        else {
                            isList = !isList;
                            setupLayoutManger();
                            adapter.notifyDataSetChanged();
                            noteViewModel.setLayoutMangerStyle(isList);
                        }
                        break;
                    case R.id.pinned_note:
                        if (noteList.isEmpty())
                            showSnackBar();
                        else
                            showPinnedNotes();
                        break;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    private void initViewModel() {
        NotesViewModelFactory viewModelFactory = new NotesViewModelFactory(
                Repository.getInstance(
                        LocalSource.getInstance(getContext()), getActivity().getApplication()
                ), getActivity().getApplication()
        );

        noteViewModel = new ViewModelProvider(
                this,
                viewModelFactory
        ).get(NoteViewModel.class);
        noteViewModel.getLayoutMangerStyle();
        noteViewModel.getAllNotes();
        Log.i(TAG, "initViewModel: ------------------------");
    }

    private void observeViewModel() {
        noteViewModel.notes.observe(this, notes -> {
            Log.i(TAG, "notes.observe------------: " + notes.size());
            if (notes.isEmpty())
                binding.noNotesLayout.noNotesView.setVisibility(View.VISIBLE);
            else
                binding.noNotesLayout.noNotesView.setVisibility(View.GONE);

            binding.noSearchLayout.noFilesView.setVisibility(View.GONE);
            adapter.setList(notes);
            noteList = notes;
        });

        noteViewModel.isList.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                Log.i(TAG, "isList.observe: ------------------------" + aBoolean);
                isList = aBoolean;
                setupLayoutManger();
            }
        });
    }

    @Override
    public void onClickListener(Note note) {
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

    private void showPinnedNotes() {
        List<Note> pinnedNotes = new ArrayList<>();

        for (Note note : noteList) {
            if (note.isPinned()) {
                pinnedNotes.add(note);
            }
        }
        if (pinnedNotes.isEmpty()) {
            binding.noNotesLayout.noNotesView.setVisibility(View.GONE);
            binding.noSearchLayout.noFilesView.setVisibility(View.VISIBLE);
            binding.noSearchLayout.noFilesTextView.setText(R.string.no_pinnedNotes);
        } else {
            binding.noSearchLayout.noFilesView.setVisibility(View.GONE);
        }
        adapter.setList(pinnedNotes);
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
                selectedNote.setPinned(!selectedNote.isPinned());
                noteViewModel.updateNote(selectedNote);
                break;
            case R.id.delete_note:
                noteViewModel.deleteNote(selectedNote.getId());
                break;


        }
        return false;
    }

    private void showSnackBar() {
        Snackbar snackBar = Snackbar.make(binding.rootView,
                getString(R.string.no_notes),
                Snackbar.LENGTH_SHORT
        ).setActionTextColor(Color.WHITE);
        snackBar.getView().setBackgroundColor(Color.RED);
        snackBar.show();
    }
}