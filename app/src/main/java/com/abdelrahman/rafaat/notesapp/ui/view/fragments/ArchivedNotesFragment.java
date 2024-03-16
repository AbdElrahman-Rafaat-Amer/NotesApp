package com.abdelrahman.rafaat.notesapp.ui.view.fragments;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

        private int buttonWidth = 0;
        private int buttonHeight = 0;

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
            swipedPosition = viewHolder.getAdapterPosition();
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                @NonNull RecyclerView.ViewHolder viewHolder, float dX,
                                float dY, int actionState, boolean isCurrentlyActive) {
            View itemView = viewHolder.itemView;
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                boolean isRightSideToDraw = false;
                String text = "";
                int textLeft = 0;
                int backgroundColor = 0;
                int itemViewHeight = itemView.getHeight() / 2;
                int itemViewCenter = itemView.getTop() + itemViewHeight;
                if (dX > 0) {
                    // Swipe left
                    backgroundColor = getResources().getColor(R.color.mainColor, null);
                    text = getString(R.string.unarchive);
                    textLeft = itemView.getLeft();
                } else if (dX < 0) {
                    // Swipe right
                    isRightSideToDraw = true;
                    backgroundColor = getResources().getColor(R.color.red, null);
                    text = getString(R.string.delete);
                    textLeft = itemView.getRight() - buttonWidth;
                }
                drawBackGround(itemView, c, backgroundColor);
                drawButton(c, itemView.getLeft(), itemView.getRight(), itemViewCenter, isRightSideToDraw);
                drawText(text, c, getResources().getColor(R.color.white, null), textLeft, itemViewCenter);
                dX = dX * buttonWidth / itemView.getWidth();
            }
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

        private void drawText(String text, Canvas canvas, @ColorInt int color, int itemViewLeft, float itemViewCenter) {
            Paint paint = new Paint();
            paint.setColor(color);

            paint.setTextSize(50);
            paint.setAntiAlias(true);

            float textX = itemViewLeft + buttonWidth / 2f - paint.measureText(text) / 2;
            float textY = itemViewCenter + buttonHeight + getResources().getDimension(R.dimen.margin_between_image_text);

            canvas.drawText(text, textX, textY, paint);
        }

        private void drawBackGround(View itemView, Canvas canvas, @ColorInt int color) {
            RectF background = new RectF(itemView.getLeft(), itemView.getTop(), itemView.getRight(), itemView.getBottom());
            Paint paint = new Paint();
            paint.setColor(color);
            canvas.drawRect(background, paint);
        }

        private void drawButton(Canvas canvas, int left, int right, int itemViewCenter, boolean isRightSideToDraw) {
            Drawable icon;
            if (isRightSideToDraw) {
                icon = ResourcesCompat.getDrawable(requireContext().getResources(),
                        R.drawable.ic_delete, requireContext().getTheme());
            } else {
                icon = ResourcesCompat.getDrawable(requireContext().getResources(),
                        R.drawable.ic_unarchive, requireContext().getTheme());
            }
            drawIcon(icon, canvas, left, right, itemViewCenter, isRightSideToDraw);

        }

        private void drawIcon(Drawable icon, Canvas canvas, int left, int right, int itemViewCenter, boolean isRightSideToDraw) {
            if (icon != null) {
                int iconHeight = icon.getIntrinsicHeight();
                buttonHeight = iconHeight;
                int iconWidth = icon.getIntrinsicWidth();
                buttonWidth = iconWidth * 3;
                int margin = iconWidth / 2;
                int iconLeft = isRightSideToDraw ? right - (iconWidth * 2 + margin) : left + margin;
                int iconRight = isRightSideToDraw ? right - margin : iconWidth * 2 + margin;
                Rect rect = new Rect(iconLeft,
                        itemViewCenter - iconHeight,
                        iconRight,
                        itemViewCenter + iconHeight);
                icon.setBounds(rect);
                icon.draw(canvas);
            }
        }
    }
}
