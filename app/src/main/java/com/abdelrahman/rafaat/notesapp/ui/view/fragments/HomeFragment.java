package com.abdelrahman.rafaat.notesapp.ui.view.fragments;

import static androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE;

import android.app.AlertDialog;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.SearchView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.lifecycle.Lifecycle;
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
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
public class HomeFragment extends BaseFragment implements OnNotesClickListener {

    private FragmentHomeBinding binding;
    private NotesAdapter adapter;
    private NoteViewModel noteViewModel;
    private List<Note> noteList = new ArrayList<>();
    private Note selectedNote;
    private boolean isList = false;
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
        initMenu();
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
        setupLayoutManger();
        binding.notesRecyclerview.setAdapter(adapter);
        int resId = R.anim.lat;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), resId);
        binding.notesRecyclerview.setLayoutAnimation(animation);
        swipeRecyclerView();
    }

    private void setupLayoutManger() {
        if (!isList)
            binding.notesRecyclerview.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        else
            binding.notesRecyclerview.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void initViewModel() {
        noteViewModel = new ViewModelProvider(requireActivity()).get(NoteViewModel.class);
        noteViewModel.getLayoutMangerStyle();
        noteViewModel.getAllNotes();
    }

    private void observeViewModel() {
        noteViewModel.notes.observe(getViewLifecycleOwner(), notes -> {
            if (notes.isEmpty())
                binding.noNotesLayout.noNotesView.setVisibility(View.VISIBLE);
            else
                binding.noNotesLayout.noNotesView.setVisibility(View.GONE);

            binding.noSearchLayout.noFilesView.setVisibility(View.GONE);
            adapter.setList(notes);
            noteList = notes;
        });

        noteViewModel.isList.observe(getViewLifecycleOwner(), aBoolean -> {
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
                if (menuItem.getItemId() == R.id.list_note) {
                    if (noteList.isEmpty()) {
                        showSnackBar(binding.rootView, getString(R.string.no_notes));
                    } else {
                        isList = !isList;
                        setupLayoutManger();
                        adapter.notifyDataSetChanged();
                        noteViewModel.setLayoutMangerStyle(isList);
                    }
                } else if (menuItem.getItemId() == R.id.pinned_note) {
                    if (noteList.isEmpty()) {
                        showSnackBar(binding.rootView, getString(R.string.no_notes));
                    } else {
                        if (menuItem.getTitle() == getString(R.string.all_notes)) {
                            adapter.setList(noteList);
                            menuItem.setTitle(getString(R.string.pinned_note));
                        } else {
                            showPinnedNotes();
                            menuItem.setTitle(getString(R.string.all_notes));
                        }
                    }
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
            showSnackBar(binding.rootView, getString(R.string.no_pinnedNotes));
        } else {
            binding.noSearchLayout.noFilesView.setVisibility(View.GONE);
            adapter.setList(pinnedNotes);
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
//        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,
//                ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
//
//            private boolean swipeBack = false;
//            private final int buttonWidth = 300;
//            private ButtonsState buttonShowedState = ButtonsState.GONE;
//
//
//
//
//            @Override
//            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
//                                  @NonNull RecyclerView.ViewHolder target) {
//                Log.d("Swipe_RecyclerView", "onMove: ");
//                return false;
//            }
//
//            @Override
//            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//                Log.d("Swipe_RecyclerView", "onSwiped: ");
//                int notePosition = viewHolder.getAdapterPosition();
//                selectedNote = noteList.get(notePosition);
//                if (direction == ItemTouchHelper.LEFT) {
//                    showAlertDialog(notePosition);
//                } else if (direction == ItemTouchHelper.RIGHT) {
//                    selectedNote.setPinned(!selectedNote.isPinned());
//                    noteViewModel.updateNote(selectedNote);
//                }
//            }
//
//            @Override
//            public void onChildDraw(@NonNull Canvas canvas, @NonNull RecyclerView recyclerView,
//                                    @NonNull RecyclerView.ViewHolder viewHolder,
//                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {
//                if (actionState == ACTION_STATE_SWIPE) {
////                    Log.d("Swipe_RecyclerView", "onChildDraw: ACTION_STATE_SWIPE");
////                    setTouchListener(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
//                }
//
//                if (dX < 0) {
//                    //swapLeft
//                    canvas.clipRect(viewHolder.itemView.getRight() + (int) dX, viewHolder.itemView.getTop(),
//                            viewHolder.itemView.getRight(), viewHolder.itemView.getBottom());
//
//                    drawBackGround(R.color.red, viewHolder.itemView.getLeft(), viewHolder.itemView.getTop(), viewHolder.itemView.getRight(), viewHolder.itemView.getBottom(), canvas);
//
//                    Drawable icon = ContextCompat.getDrawable(recyclerView.getContext(), R.drawable.ic_delete);
//                    int halfIcon = icon.getIntrinsicHeight() / 2;
//                    int top = viewHolder.itemView.getTop() + ((viewHolder.itemView.getBottom() - viewHolder.itemView.getTop()) / 2 - halfIcon);
//                    int imgLeft = viewHolder.itemView.getRight() - halfIcon * 2;
//                    icon.setBounds(imgLeft, top, viewHolder.itemView.getRight(), top + icon.getIntrinsicHeight());
//                    icon.draw(canvas);
//
//                }
//                else if (dX > 0) {
//                    //swapRight
//                    canvas.clipRect(viewHolder.itemView.getLeft(), viewHolder.itemView.getTop(),
//                            viewHolder.itemView.getLeft() + (int) dX, viewHolder.itemView.getBottom());
//
//                    drawBackGround(R.color.mainColor, viewHolder.itemView.getRight(), viewHolder.itemView.getTop(), viewHolder.itemView.getLeft(), viewHolder.itemView.getBottom(), canvas);
//
//                    Drawable icon = ContextCompat.getDrawable(recyclerView.getContext(), R.drawable.ic_pin);
//                    int halfIcon = 90;
//                    int top = viewHolder.itemView.getTop() + ((viewHolder.itemView.getBottom() - viewHolder.itemView.getTop()) / 2 - halfIcon);
//                    icon.setBounds(viewHolder.itemView.getLeft(), top, viewHolder.itemView.getLeft() + halfIcon * 2, top + halfIcon * 2);
//                    icon.draw(canvas);
//                }
//
//                super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
//            }
//
//            @Override
//            public int convertToAbsoluteDirection(int flags, int layoutDirection) {
//
//                if (swipeBack) {
//                    swipeBack = false;
//                    Log.d("Swipe_RecyclerView", "convertToAbsoluteDirection: 0");
//                    return 0;
//                }
//                Log.d("Swipe_RecyclerView", "convertToAbsoluteDirection: super");
//                return super.convertToAbsoluteDirection(flags, layoutDirection);
//            }
//
//            private void setTouchListener(Canvas c,
//                                          RecyclerView recyclerView,
//                                          RecyclerView.ViewHolder viewHolder,
//                                          float dX, float dY,
//                                          int actionState, boolean isCurrentlyActive) {
//
//                recyclerView.setOnTouchListener((v, event) -> {
//                        swipeBack = event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP;
//                    if (swipeBack) {
//                        if (dX < -buttonWidth){
//                            buttonShowedState = ButtonsState.RIGHT_VISIBLE;
//                        }
//                        else if (dX > buttonWidth){
//                            buttonShowedState  = ButtonsState.LEFT_VISIBLE;
//                        }
//
//                        if (buttonShowedState != ButtonsState.GONE) {
//                            setTouchDownListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
//                            setItemsClickable(recyclerView, false);
//                        }
//                    }
//                    return false;
//                });
//            }
//
//            private void setTouchDownListener(final Canvas c,
//                                              final RecyclerView recyclerView,
//                                              final RecyclerView.ViewHolder viewHolder,
//                                              final float dX, final float dY,
//                                              final int actionState, final boolean isCurrentlyActive) {
//                recyclerView.setOnTouchListener((v, event) -> {
//                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                        setTouchUpListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
//                    }
//                    return false;
//                });
//            }
//
//            private void setTouchUpListener(final Canvas c,
//                                            final RecyclerView recyclerView,
//                                            final RecyclerView.ViewHolder viewHolder,
//                                            final float dX, final float dY,
//                                            final int actionState, final boolean isCurrentlyActive) {
//                recyclerView.setOnTouchListener((v, event) -> {
//                    if (event.getAction() == MotionEvent.ACTION_UP) {
//                        onChildDraw(c, recyclerView, viewHolder, 0F, dY, actionState, isCurrentlyActive);
//                        recyclerView.setOnTouchListener(new View.OnTouchListener() {
//                            @Override
//                            public boolean onTouch(View v, MotionEvent event) {
//                                return false;
//                            }
//                        });
//                        setItemsClickable(recyclerView, true);
//                        swipeBack = false;
//                        buttonShowedState = ButtonsState.GONE;
//                    }
//                    return false;
//                });
//            }
//
//            private void setItemsClickable(RecyclerView recyclerView,
//                                           boolean isClickable) {
//                for (int i = 0; i < recyclerView.getChildCount(); ++i) {
//                    recyclerView.getChildAt(i).setClickable(isClickable);
//                }
//            }
//        };
//        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
//        itemTouchHelper.attachToRecyclerView(binding.notesRecyclerview);

        ItemTouchHelper.SimpleCallback simpleCallback = new SwipeHelper(requireContext(), binding.notesRecyclerview, true) {

            @Override
            public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {

                underlayButtons.add(new UnderlayButton(
                        "Archive",
                        AppCompatResources.getDrawable(requireContext(), R.drawable.ic_delete
                    ),
                Color.parseColor("#000000"), Color.parseColor("#ffffff"), new UnderlayButtonClickListener(){

                            @Override
                            public void onClick(int pos) {

                            }
                        }
                ));

                underlayButtons.add(new UnderlayButton(
                        "Flag",
                        AppCompatResources.getDrawable(requireContext(), R.drawable.ic_delete
                        ),
                        Color.parseColor("#000000"), Color.parseColor("#ffffff"), new UnderlayButtonClickListener(){

                    @Override
                    public void onClick(int pos) {

                    }
                }
                ));

                underlayButtons.add(new UnderlayButton(
                        "More",
                        AppCompatResources.getDrawable(requireContext(), R.drawable.ic_delete
                        ),
                        Color.parseColor("#000000"), Color.parseColor("#ffffff"), new UnderlayButtonClickListener(){

                    @Override
                    public void onClick(int pos) {

                    }
                }
                ));
            }
        };
    }

    private void drawBackGround(int color, int left, int top, int right, int bottom, Canvas canvas) {
        final ColorDrawable background = new ColorDrawable(getResources().getColor(color, null));
        background.setBounds(left, top, right, bottom);
        background.draw(canvas);
    }

    private void showAlertDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);

        builder.setMessage(getString(R.string.remove_note))
                .setPositiveButton(R.string.remove, (dialog, which) -> noteViewModel.deleteNote(selectedNote.getId()))
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    alertDialog.dismiss();
                    adapter.notifyItemRemoved(position);
                });

        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    @Override
    public void onClickListener(Note note) {
        noteViewModel.setCurrentNote(note);
        if (note.getPassword().isEmpty())
            Navigation.findNavController(requireView()).navigate(R.id.show_note_fragment);
        else
            Navigation.findNavController(requireView()).navigate(R.id.password_fragment);
    }

    enum ButtonsState {
        GONE,
        LEFT_VISIBLE,
        RIGHT_VISIBLE
    }



}

abstract class SwipeHelper extends ItemTouchHelper.SimpleCallback {

    public static final int BUTTON_WIDTH = 200;
    private RecyclerView recyclerView;
    private List<UnderlayButton> buttons;
    private GestureDetector gestureDetector;
    private int swipedPos = -1;
    private float swipeThreshold = 0.5f;
    private Map<Integer, List<UnderlayButton>> buttonsBuffer;
    private Queue<Integer> recoverQueue;
    private static Boolean animate;

    private GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            for (UnderlayButton button : buttons) {
                if (button.onClick(e.getX(), e.getY()))
                    break;
            }

            return true;
        }
    };

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent e) {
            if (swipedPos < 0) return false;
            Point point = new Point((int) e.getRawX(), (int) e.getRawY());

            RecyclerView.ViewHolder swipedViewHolder = recyclerView.findViewHolderForAdapterPosition(swipedPos);
            View swipedItem = swipedViewHolder.itemView;
            Rect rect = new Rect();
            swipedItem.getGlobalVisibleRect(rect);

            if (e.getAction() == MotionEvent.ACTION_DOWN || e.getAction() == MotionEvent.ACTION_UP || e.getAction() == MotionEvent.ACTION_MOVE) {
                if (rect.top < point.y && rect.bottom > point.y)
                    gestureDetector.onTouchEvent(e);
                else {
                    recoverQueue.add(swipedPos);
                    swipedPos = -1;
                    recoverSwipedItem();
                }
            }
            return false;
        }
    };

    public SwipeHelper(Context context, RecyclerView recyclerView, Boolean animate) {
        super(0, ItemTouchHelper.LEFT);
        this.animate = animate;
        this.recyclerView = recyclerView;
        this.buttons = new ArrayList<>();
        this.gestureDetector = new GestureDetector(context, gestureListener);
        this.recyclerView.setOnTouchListener(onTouchListener);
        buttonsBuffer = new HashMap<>();
        recoverQueue = new LinkedList<Integer>() {
            @Override
            public boolean add(Integer o) {
                if (contains(o))
                    return false;
                else
                    return super.add(o);
            }
        };

        attachSwipe();
    }


    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int pos = viewHolder.getAdapterPosition();

        if (swipedPos != pos)
            recoverQueue.add(swipedPos);

        swipedPos = pos;

        if (buttonsBuffer.containsKey(swipedPos))
            buttons = buttonsBuffer.get(swipedPos);
        else
            buttons.clear();

        buttonsBuffer.clear();
        swipeThreshold = 0.5f * buttons.size() * BUTTON_WIDTH;
        recoverSwipedItem();
    }

    @Override
    public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
        return swipeThreshold;
    }

    @Override
    public float getSwipeEscapeVelocity(float defaultValue) {
        return 0.1f * defaultValue;
    }

    @Override
    public float getSwipeVelocityThreshold(float defaultValue) {
        return 5.0f * defaultValue;
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        int pos = viewHolder.getAdapterPosition();
        float translationX = dX;
        View itemView = viewHolder.itemView;

        if (pos < 0) {
            swipedPos = pos;
            return;
        }

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if (dX < 0) {
                List<UnderlayButton> buffer = new ArrayList<>();

                if (!buttonsBuffer.containsKey(pos)) {
                    instantiateUnderlayButton(viewHolder, buffer);
                    buttonsBuffer.put(pos, buffer);
                } else {
                    buffer = buttonsBuffer.get(pos);
                }

                translationX = dX * buffer.size() * BUTTON_WIDTH / itemView.getWidth();
                drawButtons(c, itemView, buffer, pos, translationX);
            }
        }

        super.onChildDraw(c, recyclerView, viewHolder, translationX, dY, actionState, isCurrentlyActive);
    }

    private synchronized void recoverSwipedItem() {
        while (!recoverQueue.isEmpty()) {
            int pos = recoverQueue.poll();
            if (pos > -1) {
                recyclerView.getAdapter().notifyItemChanged(pos);
            }
        }
    }

    private void drawButtons(Canvas c, View itemView, List<UnderlayButton> buffer, int pos, float dX) {
        float right = itemView.getRight();
        float dButtonWidth = (-1) * dX / buffer.size();

        for (UnderlayButton button : buffer) {
            float left = right - dButtonWidth;
            button.onDraw(
                    c,
                    new RectF(
                            left,
                            itemView.getTop(),
                            right,
                            itemView.getBottom()
                    ),
                    pos
            );

            right = left;
        }
    }

    public void attachSwipe() {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(this);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    public abstract void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons);

    public static class UnderlayButton {
        private String text;
        private Drawable imageResId;
        private int buttonBackgroundcolor;
        private int textColor;
        private int pos;
        private RectF clickRegion;
        private UnderlayButtonClickListener clickListener;

        public UnderlayButton(String text, Drawable imageResId, int buttonBackgroundcolor, int textColor, UnderlayButtonClickListener clickListener) {
            this.text = text;
            this.imageResId = imageResId;
            this.buttonBackgroundcolor = buttonBackgroundcolor;
            this.textColor = textColor;
            this.clickListener = clickListener;
        }

        public boolean onClick(float x, float y) {
            if (clickRegion != null && clickRegion.contains(x, y)) {
                clickListener.onClick(pos);
                return true;
            }

            return false;
        }

        private void onDraw(Canvas canvas, RectF rect, int pos) {

            Paint p = new Paint();
            // Draw background
            p.setColor(buttonBackgroundcolor);
            canvas.drawRect(rect, p);

            if (!animate) {
                // Draw Text
//                p.setColor(Color.BLACK);
                p.setColor(textColor);
                p.setTextSize(40);
                Rect r = new Rect();
                float cHeight = rect.height();
                float cWidth = rect.width();
                p.setTextAlign(Paint.Align.LEFT);
                p.getTextBounds(text, 0, text.length(), r);
                float x = cWidth / 2f - r.width() / 2f - r.left;
                float y = cHeight / 2f + r.height() / 2f - r.bottom - 40;
                canvas.drawText(text, rect.left + x, rect.top + y, p);

                if (imageResId != null) {
                    imageResId.setBounds((int) (rect.left + 50), (int) (rect.top + (cHeight / 2f)), (int) (rect.right - 50), (int) (rect.bottom - ((cHeight / 10f))));
                    imageResId.draw(canvas);
                }

            } else {
                //animate
                // Draw Text
                TextPaint textPaint = new TextPaint();
                textPaint.setTextSize(40);
                textPaint.setColor(textColor);
                StaticLayout sl = new StaticLayout(text, textPaint, (int) rect.width(),
                        Layout.Alignment.ALIGN_CENTER, 1, 1, false);

                if (imageResId != null) {
                    imageResId.setBounds((int) (rect.left + 50), (int) (rect.top + (rect.height() / 2f)), (int) (rect.right - 50), (int) (rect.bottom - ((rect.height() / 10f))));
                    imageResId.draw(canvas);
                }

                canvas.save();
                Rect r = new Rect();
                float y = (rect.height() / 2f) + (r.height() / 2f) - r.bottom - (sl.getHeight() / 2);

                if (imageResId == null)
                    canvas.translate(rect.left, rect.top + y);
                else
                    canvas.translate(rect.left, rect.top + y - 30);


                sl.draw(canvas);
                canvas.restore();
            }

            clickRegion = rect;
            this.pos = pos;
        }

    }

    public interface UnderlayButtonClickListener {
        void onClick(int pos);
    }
}