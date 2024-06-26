package com.abdelrahman.rafaat.notesapp.ui.view.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.SearchView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.abdelrahman.rafaat.notesapp.R;
import com.abdelrahman.rafaat.notesapp.databinding.FragmentHomeBinding;
import com.abdelrahman.rafaat.notesapp.model.Note;
import com.abdelrahman.rafaat.notesapp.ui.view.NotesAdapter;
import com.abdelrahman.rafaat.notesapp.ui.view.OnNotesClickListener;
import com.abdelrahman.rafaat.notesapp.ui.viewmodel.NoteViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HomeFragment extends BaseFragment implements OnNotesClickListener {

    private FragmentHomeBinding binding;
    private NotesAdapter adapter;
    private NoteViewModel noteViewModel;
    private List<Note> noteList = new ArrayList<>();
    private Note selectedNote;
    private boolean isSearching = false;
    private boolean isPinned = false;
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

        binding.addNoteFloatingActionButton.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_home_to_addNote)
        );

        search();
        initRecyclerView();
        initViewModel();
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

    private void initViewModel() {
        noteViewModel = new ViewModelProvider(requireActivity()).get(NoteViewModel.class);
        noteViewModel.getAllNotes();
    }

    private void observeViewModel() {
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

    private void showPinnedNotes() {
        if (noteList.isEmpty()) {
            showSnackBar(binding.rootView, getString(R.string.no_notes));
        } else {
            isPinned = true;
            List<Note> pinnedNotes = noteList.stream().filter(Note::isPinned).collect(Collectors.toList());
            if (pinnedNotes.isEmpty()) {
                showSnackBar(binding.rootView, getString(R.string.no_pinnedNotes));
            } else {
                binding.noSearchLayout.noFilesView.setVisibility(View.GONE);
                adapter.setList(pinnedNotes);
            }
        }
    }

    public void onBackPressed() {
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
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
        noteViewModel.setCurrentNote(note);
        if (note.getPassword().isEmpty())
            Navigation.findNavController(requireView()).navigate(R.id.show_note_fragment);
        else
            Navigation.findNavController(requireView()).navigate(R.id.password_fragment);
    }

}

abstract class MyItemTouchHelperCallback extends ItemTouchHelper.Callback {
    private int buttonWidth;
    private int buttonHeight;
    private final Context context;
    private float deleteButtonLeft;
    private float deleteButtonRight;
    private float archiveButtonLeft;
    private float archiveButtonRight;
    private float pinButtonRight;
    private float pinButtonLeft;
    private View itemView;
    private int swipedPosition = -1;
    private final GestureDetector gestureDetector;

    private final GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            float x = e.getX();
            float y = e.getY();

            if (isTapOnDeleteButton(x, y)) {
                deleteButtonPressed(swipedPosition);
                return true;
            }

            if (isTapOnArchiveButton(x, y)) {
                archiveButtonPressed(swipedPosition);
                return true;
            }

            if (isTapOnPinButton(x, y)) {
                pinButtonPressed(swipedPosition);
                return true;
            }

            return false;
        }

        private boolean isTapOnDeleteButton(float x, float y) {
            return x >= deleteButtonLeft && x <= deleteButtonRight && y >= itemView.getTop() && y <= itemView.getBottom();
        }

        private boolean isTapOnArchiveButton(float x, float y) {
            return x >= archiveButtonLeft && x <= archiveButtonRight && y >= itemView.getTop() && y <= itemView.getBottom();
        }

        private boolean isTapOnPinButton(float x, float y) {
            return x >= pinButtonLeft && x <= pinButtonRight && y >= itemView.getTop() && y <= itemView.getBottom();
        }
    };

    private final View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent e) {

            if (itemView != null) {
                Point point = new Point((int) e.getRawX(), (int) e.getRawY());
                Rect rect = new Rect();
                itemView.getGlobalVisibleRect(rect);

                if (e.getAction() == MotionEvent.ACTION_DOWN || e.getAction() == MotionEvent.ACTION_UP || e.getAction() == MotionEvent.ACTION_MOVE) {
                    if (rect.top < point.y || rect.bottom > point.y)
                        gestureDetector.onTouchEvent(e);
                }
            }
            return false;
        }
    };

    public MyItemTouchHelperCallback(Context context, RecyclerView recyclerView) {
        this.context = context;
        gestureDetector = new GestureDetector(context, gestureListener);
        recyclerView.setOnTouchListener(onTouchListener);
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        return makeMovementFlags(0, swipeFlags);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                          @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

    }

    @Override
    public void onChildDraw(@NonNull Canvas canvas, @NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {
        float translationX = dX;
        View itemView = viewHolder.itemView;
        swipedPosition = viewHolder.getAdapterPosition();
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if (dX < 0) {
                // swapLeft
                drawButtonsOnRight(canvas, itemView);
                translationX = dX * 2 * buttonWidth / itemView.getWidth();
            } else if (dX > 0) {
                // swapRight
                drawButtonsOnLeft(canvas, itemView);
                translationX = dX * buttonWidth / itemView.getWidth();
            }
        }
        super.onChildDraw(canvas, recyclerView, viewHolder, translationX, dY, actionState, isCurrentlyActive);
    }

    private void drawButtonsOnLeft(Canvas canvas, View itemView) {
        // Calculate the button width based on your requirements
        this.itemView = itemView;
        pinButtonLeft = itemView.getLeft();
        pinButtonRight = pinButtonLeft + buttonWidth;
        // Draw the first button
        drawButton(canvas, itemView, AppCompatResources.getDrawable(context, R.drawable.ic_pin), context.getString(R.string.pin), pinButtonLeft, pinButtonRight, ContextCompat.getColor(context, R.color.mainColor));
    }

    private void drawButtonsOnRight(Canvas canvas, View itemView) {
        // Calculate the button width based on your requirements
        this.itemView = itemView;

        deleteButtonRight = itemView.getRight();
        deleteButtonLeft = deleteButtonRight - buttonWidth;

        archiveButtonRight = deleteButtonLeft;
        archiveButtonLeft = archiveButtonRight - buttonWidth;
        // Calculate the positions of the buttons on the right side

        // Draw the first button
        drawButton(canvas, itemView, AppCompatResources.getDrawable(context, R.drawable.ic_archive), context.getString(R.string.archive), archiveButtonLeft, deleteButtonLeft, ContextCompat.getColor(context, R.color.mainColor));

        // Draw the second button
        drawButton(canvas, itemView, AppCompatResources.getDrawable(context, R.drawable.ic_delete), context.getString(R.string.delete), deleteButtonLeft, deleteButtonRight, ContextCompat.getColor(context, R.color.red));
    }

    private void drawButton(Canvas canvas, View itemView, Drawable icon, String text, float left, float right, int color) {

        // Draw the button background
        drawBackGround(itemView, canvas, color, left, right);

        int itemViewHeight = itemView.getHeight() / 2;
        int itemViewCenter = itemView.getTop() + itemViewHeight;
        // Draw the button image
        drawIcon(icon, canvas, left, right, itemViewCenter);
        // Draw the button text
        drawText(text, canvas, context.getResources().getColor(R.color.white, null), left, right, itemViewCenter);

    }

    private void drawBackGround(View itemView, Canvas canvas, @ColorInt int color, float left, float right) {
        RectF background = new RectF(left, itemView.getTop(), right, itemView.getBottom());
        Paint paint = new Paint();
        paint.setColor(color);
        canvas.drawRect(background, paint);
    }

    private void drawText(String text, Canvas canvas, @ColorInt int color,
                          float itemViewLeft, float right, int itemViewCenter) {
        Paint paint = new Paint();
        paint.setColor(color);
        double textSize = buttonWidth * 0.25;
        paint.setTextSize((float) textSize);
        paint.setAntiAlias(true);

        paint.setColor(ContextCompat.getColor(context, R.color.white));
        float textX = itemViewLeft + (right - itemViewLeft) / 2f - paint.measureText(text) / 2f;
        float textY = itemViewCenter + buttonHeight + context.getResources().getDimension(R.dimen.margin_between_image_text);
        canvas.drawText(text, textX, textY, paint);
    }

    private void drawIcon(Drawable icon, Canvas canvas, float left, float right, int itemViewCenter) {
        if (icon != null) {
            int iconHeight = icon.getIntrinsicHeight();
            buttonHeight = iconHeight;
            int iconWidth = icon.getIntrinsicWidth();
            buttonWidth = iconWidth * 3;
            int margin = iconWidth / 2;
            int iconLeft = (int) (left + margin);
            int iconRight = (int) (right - margin);
            Rect rect = new Rect(iconLeft,
                    itemViewCenter - iconHeight,
                    iconRight,
                    itemViewCenter + iconHeight);
            icon.setBounds(rect);
            icon.draw(canvas);
        }
    }

    public abstract void deleteButtonPressed(int position);

    public abstract void archiveButtonPressed(int position);

    public abstract void pinButtonPressed(int position);
}