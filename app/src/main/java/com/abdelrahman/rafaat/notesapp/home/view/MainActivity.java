package com.abdelrahman.rafaat.notesapp.home.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.abdelrahman.rafaat.notesapp.R;
import com.abdelrahman.rafaat.notesapp.addnote.view.AddNoteActivity;
import com.abdelrahman.rafaat.notesapp.database.LocalSource;
import com.abdelrahman.rafaat.notesapp.databinding.ActivityMainBinding;
import com.abdelrahman.rafaat.notesapp.model.Note;
import com.abdelrahman.rafaat.notesapp.model.Repository;
import com.abdelrahman.rafaat.notesapp.home.viewmodel.NoteViewModel;
import com.abdelrahman.rafaat.notesapp.home.viewmodel.NotesViewModelFactory;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnNotesClickListener, PopupMenu.OnMenuItemClickListener {

    private String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private NotesAdapter adapter;
    private NoteViewModel noteViewModel;
    private NotesViewModelFactory viewModelFactory;
    private List<Note> noteList;
    private Note selectedNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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


        binding.addNoteFloatingActionButton.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, AddNoteActivity.class)));


        initRecyclerView();
        initViewModel();
        observeViewModel();
    }

    private void initRecyclerView() {
        adapter = new NotesAdapter(this);
        binding.notesRecyclerview.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        binding.notesRecyclerview.setAdapter(adapter);
        int resId = R.anim.lat;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(this, resId);
        binding.notesRecyclerview.setLayoutAnimation(animation);
    }

    private void initViewModel() {
        viewModelFactory = new NotesViewModelFactory(
                Repository.getInstance(
                        LocalSource.getInstance(this), this.getApplication()
                ), this.getApplication()
        );

        noteViewModel = new ViewModelProvider(
                this,
                viewModelFactory
        ).get(NoteViewModel.class);

        noteViewModel.getAllNotes();
    }

    private void observeViewModel() {
        noteViewModel.notes.observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                Log.i(TAG, "onChanged: " + notes.size());
                adapter.setList(notes);
                noteList = notes;
            }
        });
    }

    @Override
    public void onClickListener(Note note) {
        Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
        intent.putExtra("NOTE", note);
        startActivity(intent);
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
        adapter.setList(filteredList);
    }

    private void showPopupMenu(CardView cardView) {
        PopupMenu popupMenu = new PopupMenu(this, cardView);
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
                //    adapter.notifyDataSetChanged();
                break;
            case R.id.delete_note:
                noteViewModel.deleteNote(selectedNote.getId());
                break;


        }
        return false;
    }
}