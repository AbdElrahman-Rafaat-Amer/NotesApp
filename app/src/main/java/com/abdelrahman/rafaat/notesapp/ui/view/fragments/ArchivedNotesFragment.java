package com.abdelrahman.rafaat.notesapp.ui.view.fragments;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abdelrahman.rafaat.notesapp.R;
import com.abdelrahman.rafaat.notesapp.databinding.FragmentHomeBinding;
import com.abdelrahman.rafaat.notesapp.model.Note;
import com.abdelrahman.rafaat.notesapp.ui.view.NotesAdapter;
import com.abdelrahman.rafaat.notesapp.ui.view.OnNotesClickListener;
import com.abdelrahman.rafaat.notesapp.ui.viewmodel.NoteViewModel;

public class ArchivedNotesFragment extends BaseFragment implements OnNotesClickListener {

    private static final String TAG = "ArchivedNotesFragment";
    private FragmentHomeBinding binding;
    private NoteViewModel noteViewModel;
    private NotesAdapter adapter;
    private int swipedPosition = -1;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.addNoteFloatingActionButton.setOnClickListener(v -> adapter.notifyDataSetChanged());
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
        ArchiveItemTouchHelper archiveItemTouchHelper = new ArchiveItemTouchHelper();
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

    @Override
    public void onNoteClickListener(Note note) {

    }

    class ArchiveItemTouchHelper extends ItemTouchHelper.Callback {

        private final int BUTTON_WIDTH = screenWidth / 4;

        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            return makeMovementFlags(0, swipeFlags);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            if (direction == ItemTouchHelper.LEFT) {
                Log.d(TAG, "onSwiped: ItemTouchHelper.LEFT");
            } else if (direction == ItemTouchHelper.RIGHT) {
                Log.d(TAG, "onSwiped: ItemTouchHelper.RIGHT");
            }
            swipedPosition = viewHolder.getAdapterPosition();
        }

        @Override
        public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
            return 0.4F;
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                @NonNull RecyclerView.ViewHolder viewHolder, float dX,
                                float dY, int actionState, boolean isCurrentlyActive) {
            View itemView = viewHolder.itemView;
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                if (dX > 0) {
                    // Swipe left
                    drawBackGround(itemView, c, getResources().getColor(R.color.red, null));
                    drawLeftButtons(itemView, c);
                } else if (dX < 0) {
                    // Swipe right
                    drawBackGround(itemView, c, getResources().getColor(R.color.mainColor, null));
                    drawRightButtons(itemView, c);
                }
                int maxDisplacement = screenWidth / 2;
                if (Math.abs(dX) > maxDisplacement) {
                    dX = Math.signum(dX) * maxDisplacement;
                }
            }
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

        private void drawBackGround(View itemView, Canvas canvas, @ColorInt int color) {
            RectF background = new RectF(itemView.getLeft(), itemView.getTop(), itemView.getRight(), itemView.getBottom());
            Paint paint = new Paint();
            paint.setColor(color);
            canvas.drawRect(background, paint);
        }

        private void drawRightButtons(View itemView, Canvas canvas) {
            Drawable deleteIcon = ResourcesCompat.getDrawable(requireContext().getResources(),
                    R.drawable.ic_delete, requireContext().getTheme());
            if (deleteIcon != null) {
                Log.d(TAG, "drawRightButtons: deleteIcon.getIntrinsicWidth() : " + deleteIcon.getIntrinsicWidth());
                Log.d(TAG, "drawRightButtons: deleteIcon.getIntrinsicHeight(): " + deleteIcon.getIntrinsicHeight());
                int itemViewHeight = itemView.getHeight() / 2;
                int itemViewCenter = itemView.getTop() + itemViewHeight;
                int iconHeight = deleteIcon.getIntrinsicHeight() / 2;
                Rect rect = new Rect(itemView.getRight() - itemView.getWidth() / 4,
                        itemViewCenter - iconHeight,
                        itemView.getRight(),
                        itemViewCenter + iconHeight);
                deleteIcon.setBounds(rect);
                deleteIcon.draw(canvas);
            }
        }

        private void drawLeftButtons(View itemView, Canvas canvas) {
            Drawable unArchiveIcon = ResourcesCompat.getDrawable(requireContext().getResources(),
                    R.drawable.ic_unarchive, requireContext().getTheme());
            if (unArchiveIcon != null) {
                int itemViewHeight = itemView.getHeight() / 2;
                int itemViewCenter = itemView.getBottom() - itemViewHeight;
                int iconHeight = unArchiveIcon.getIntrinsicHeight() / 2;
                Rect rect = new Rect(itemView.getLeft(),
                        itemViewCenter - iconHeight,
                        unArchiveIcon.getIntrinsicWidth(),
                        itemViewCenter + iconHeight);
                unArchiveIcon.setBounds(rect);
                unArchiveIcon.draw(canvas);
            }
        }
    }
}
