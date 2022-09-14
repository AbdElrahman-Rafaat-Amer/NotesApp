package com.abdelrahman.rafaat.notesapp.ui.view;

import android.app.AlertDialog;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.abdelrahman.rafaat.notesapp.ui.viewmodel.NotesViewModelFactory;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.SearchView;

import com.abdelrahman.rafaat.notesapp.databinding.FragmentHomeBinding;
import com.abdelrahman.rafaat.notesapp.ui.viewmodel.NoteViewModel;
import com.abdelrahman.rafaat.notesapp.model.Note;

import java.util.ArrayList;
import java.util.Collections;
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


public class HomeFragment extends Fragment implements OnNotesClickListener {

    private static final String TAG = "HomeFragment";
    private FragmentHomeBinding binding;
    private NotesAdapter adapter;
    private NoteViewModel noteViewModel;
    private List<Note> noteList;
    private Note selectedNote;
    private boolean isList = false;
    private boolean isSearching = false;
    private boolean isPinned = false;
    private AlertDialog alertDialog;

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

        initRecyclerView();
        initViewModel();
        observeViewModel();
        initMenu();
        search();
        onBackPressed();

    }

    private void search() {
        binding.noteSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (noteList.isEmpty())
                    showSnackBar(getString(R.string.no_notes));
                else {
                    isSearching = true;
                    filter(newText);
                }

                return true;
            }
        });
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

    private void initRecyclerView() {
        adapter = new NotesAdapter(this);
        setupLayoutManger();
        binding.notesRecyclerview.setAdapter(adapter);
        int resId = R.anim.lat;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), resId);
        binding.notesRecyclerview.setLayoutAnimation(animation);
        itemsAnimation();
    }

    private void setupLayoutManger() {
        if (!isList)
            binding.notesRecyclerview.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        else
            binding.notesRecyclerview.setLayoutManager(new LinearLayoutManager(requireContext()));
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
    }

    private void observeViewModel() {
        noteViewModel.notes.observe(this, notes -> {
            if (notes.isEmpty())
                binding.noNotesLayout.noNotesView.setVisibility(View.VISIBLE);
            else
                binding.noNotesLayout.noNotesView.setVisibility(View.GONE);

            binding.noSearchLayout.noFilesView.setVisibility(View.GONE);
            adapter.setList(notes);
            noteList = notes;
        });

        noteViewModel.isList.observe(this, aBoolean -> {
            isList = aBoolean;
            setupLayoutManger();
        });
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
                            showSnackBar(getString(R.string.no_notes));
                        else {
                            isList = !isList;
                            setupLayoutManger();
                            adapter.notifyDataSetChanged();
                            noteViewModel.setLayoutMangerStyle(isList);
                        }
                        break;
                    case R.id.pinned_note:
                        if (noteList.isEmpty())
                            showSnackBar(getString(R.string.no_notes));
                        else
                            showPinnedNotes();
                        break;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    private void showPinnedNotes() {
        isPinned = true;
        List<Note> pinnedNotes = new ArrayList<>();

        for (Note note : noteList) {
            if (note.isPinned()) {
                pinnedNotes.add(note);
            }
        }
        if (pinnedNotes.isEmpty()) {
            showSnackBar(getString(R.string.no_pinnedNotes));
        } else {
            binding.noSearchLayout.noFilesView.setVisibility(View.GONE);
            adapter.setList(pinnedNotes);
        }

    }

    private void showSnackBar(String message) {
        Snackbar snackBar = Snackbar.make(binding.rootView,
                        message, Snackbar.LENGTH_SHORT)
                .setActionTextColor(Color.WHITE);
        snackBar.getView().setBackgroundColor(Color.RED);
        snackBar.show();


    }

    public void onBackPressed() {
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (isSearching) {
                    binding.noteSearchView.setIconified(true);
                    binding.noteSearchView.clearFocus();
                    binding.addNoteFloatingActionButton.setVisibility(View.VISIBLE);
                    isSearching = false;
                } else if (isPinned) {
                    isPinned = false;
                    adapter.setList(noteList);
                } else {
                    setEnabled(false);
                    requireActivity().onBackPressed();
                }
            }
        });
    }

    @Override
    public void onClickListener(Note note) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("NOTE", note);
        if (note.getPassword().isEmpty())
            Navigation.findNavController(getView()).navigate(R.id.show_note_fragment, bundle);
        else
            Navigation.findNavController(getView()).navigate(R.id.password_fragment, bundle);
    }

    private void itemsAnimation() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
/*
                int fromPosition = viewHolder.getAdapterPosition();
                int targetPosition = target.getAdapterPosition();
                Collections.swap(noteList, fromPosition, targetPosition);
                recyclerView.getAdapter().notifyItemMoved(fromPosition, targetPosition);
                Log.i(TAG, "onMove: ---------------------->");*/
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                selectedNote = noteList.get(viewHolder.getAdapterPosition());
                if (direction == ItemTouchHelper.LEFT) {
                    showAlertDialog();
                } else if (direction == ItemTouchHelper.RIGHT) {
                    selectedNote.setPinned(!selectedNote.isPinned());
                    noteViewModel.updateNote(selectedNote);
                    adapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas canvas, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {

                if (dX < 0) {
                    //swapLeft
                    canvas.clipRect(viewHolder.itemView.getRight() + (int) dX, viewHolder.itemView.getTop(),
                            viewHolder.itemView.getRight(), viewHolder.itemView.getBottom());

                    final ColorDrawable background = new ColorDrawable(getResources().getColor(R.color.red, null));
                    background.setBounds(viewHolder.itemView.getLeft(), viewHolder.itemView.getTop(),
                            viewHolder.itemView.getRight(), viewHolder.itemView.getBottom());
                    background.draw(canvas);

                    Drawable icon = ContextCompat.getDrawable(recyclerView.getContext(), R.drawable.ic_delete);
                    int halfIcon = icon.getIntrinsicHeight() / 2;
                    int top = viewHolder.itemView.getTop() + ((viewHolder.itemView.getBottom() - viewHolder.itemView.getTop()) / 2 - halfIcon);
                    int imgLeft = viewHolder.itemView.getRight() - halfIcon * 2;
                    icon.setBounds(imgLeft, top, viewHolder.itemView.getRight(), top + icon.getIntrinsicHeight());
                    icon.draw(canvas);
                } else if (dX > 0) {
                    //swapRight
                    canvas.clipRect(viewHolder.itemView.getLeft(), viewHolder.itemView.getTop(),
                            viewHolder.itemView.getLeft() + (int) dX, viewHolder.itemView.getBottom());
                    final ColorDrawable background = new ColorDrawable(getResources().getColor(R.color.mainColor, null));
                    background.setBounds(viewHolder.itemView.getRight(), viewHolder.itemView.getTop(),
                            viewHolder.itemView.getLeft(), viewHolder.itemView.getBottom());
                    background.draw(canvas);

                    int x = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, recyclerView.getContext().getResources().getDisplayMetrics());
                    Drawable icon = ContextCompat.getDrawable(recyclerView.getContext(), R.drawable.pin);
                    int halfIcon = 82;
                    int top = viewHolder.itemView.getTop() + ((viewHolder.itemView.getBottom() - viewHolder.itemView.getTop()) / 2 - halfIcon);
                    icon.setBounds(viewHolder.itemView.getLeft() + x, top, viewHolder.itemView.getLeft() + x + halfIcon * 2, top + halfIcon * 2);
                    icon.draw(canvas);
                }

                super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(binding.notesRecyclerview);
    }

    private void showAlertDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);

        builder.setMessage(getString(R.string.remove_note))
                .setPositiveButton(R.string.remove, (dialog, which) -> {
                    noteViewModel.deleteNote(selectedNote.getId());
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    alertDialog.dismiss();
                    adapter.notifyDataSetChanged();
                });

        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }
}