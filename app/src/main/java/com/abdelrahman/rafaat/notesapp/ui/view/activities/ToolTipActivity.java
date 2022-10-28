package com.abdelrahman.rafaat.notesapp.ui.view.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;

import com.abdelrahman.rafaat.notesapp.R;
import com.abdelrahman.rafaat.notesapp.databinding.ActivityToolTipBinding;
import com.abdelrahman.rafaat.notesapp.model.Note;
import com.abdelrahman.rafaat.notesapp.ui.view.NotesAdapter;
import com.abdelrahman.rafaat.notesapp.ui.view.OnNotesClickListener;

import java.util.ArrayList;
import java.util.List;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

public class ToolTipActivity extends AppCompatActivity implements OnNotesClickListener {

    private ActivityToolTipBinding binding;
    private int next = 0;
    private MaterialTapTargetPrompt.Builder builder;
    private LinearLayoutManager linerLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityToolTipBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initRecyclerView();

        builder = new MaterialTapTargetPrompt.Builder(this)
                .setTarget(binding.addNoteFloatingActionButton)
                .setPrimaryText(getString(R.string.new_note))
                .setSecondaryText(getString(R.string.tooltip_add_note))
                .setAutoDismiss(false)
                .setPromptStateChangeListener((prompt, state) -> {
                    if (state == MaterialTapTargetPrompt.STATE_FINISHING) {
                        callNextTip();
                    }
                });
        builder.show();


        binding.continueButton.setOnClickListener(v -> {
            boolean willShowed = binding.checkbox.isChecked();
            PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("IsFirstTime", !willShowed).commit();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }

    private void initRecyclerView() {
        NotesAdapter adapter = new NotesAdapter(this);
        linerLayoutManager = new LinearLayoutManager(this);
        binding.notesRecyclerview.setLayoutManager(linerLayoutManager);
        binding.notesRecyclerview.setAdapter(adapter);
        adapter.setList(setList());
    }

    private List<Note> setList() {
        List<Note> notes = new ArrayList<>();
        String noteBody = getString(R.string.dummy_body);
        notes.add(new Note(getString(R.string.dummy_title), noteBody, getString(R.string.dummy_date), getColor(R.color.color1), 18, Gravity.TOP | Gravity.START));
        notes.add(new Note(getString(R.string.dummy_title), noteBody, getString(R.string.dummy_date), getColor(R.color.color2), 18, Gravity.TOP | Gravity.START));
        notes.add(new Note(getString(R.string.dummy_title), noteBody, getString(R.string.dummy_date), getColor(R.color.color3), 18, Gravity.TOP | Gravity.START));
        return notes;
    }

    private void callNextTip() {
        next++;
        switch (next) {
            case 1:
                Rect rect = new Rect();
                binding.noteSearchView.getGlobalVisibleRect(rect);
                int top = (rect.top + rect.bottom) / 2;
                builder.setTarget((float) rect.left, (float) top)
                        .setPrimaryText(getString(R.string.search_note))
                        .setSecondaryText(getString(R.string.tooltip_search))
                        .show();
                break;
            case 2:
                CardView card = (CardView) linerLayoutManager.findViewByPosition(0);
                if (card != null) {
                    //   NotesAdapter.ViewHolder viewHolder = (NotesAdapter.ViewHolder) binding.notesRecyclerview.getChildViewHolder(card);
                    Rect rect1 = new Rect();
                    card.getGlobalVisibleRect(rect1);
                    int top1 = (rect1.top + rect1.bottom) / 2;
                    builder
                            .setTarget((float) rect1.left, (float) top1)
                            .setPrimaryText(R.string.pin_note)
                            .setSecondaryText(R.string.swipe_right)
                            .setClipToView(card.getChildAt(2))
                            .show();
                }
                break;
            case 3:
                CardView card1 = (CardView) linerLayoutManager.findViewByPosition(1);
                if (card1 != null) {
                    Rect rect2 = new Rect();
                    card1.getGlobalVisibleRect(rect2);
                    int top2 = (rect2.top + rect2.bottom) / 2;
                    builder
                            .setTarget((float) rect2.right, (float) top2)
                            .setPrimaryText(R.string.delete_note)
                            .setSecondaryText(R.string.swipe_left)
                            .setClipToView(card1.getChildAt(2))
                            .show();
                }
                break;
            case 4:
                CardView card2 = (CardView) linerLayoutManager.findViewByPosition(2);
                if (card2 != null) {
                    NotesAdapter.ViewHolder viewHolder = (NotesAdapter.ViewHolder) binding.notesRecyclerview.getChildViewHolder(card2);
                    builder
                            .setTarget(viewHolder.binding.noteBodyTextView)
                            .setPrimaryText(R.string.show_details)
                            .setSecondaryText(R.string.click_on_note)
                            .setClipToView(card2.getChildAt(2))
                            .show();

                }
                binding.checkbox.setVisibility(View.VISIBLE);
                binding.continueButton.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onClickListener(Note note) {

    }
}