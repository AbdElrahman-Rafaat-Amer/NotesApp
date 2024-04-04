package com.abdelrahman.rafaat.notesapp.ui.view.activities;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.abdelrahman.rafaat.notesapp.R;
import com.abdelrahman.rafaat.notesapp.databinding.ActivityToolTipBinding;
import com.abdelrahman.rafaat.notesapp.model.Note;
import com.abdelrahman.rafaat.notesapp.ui.view.NotesAdapter;
import com.abdelrahman.rafaat.notesapp.interfaces.OnNotesClickListener;

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
                showTip(getString(R.string.search_note), getString(R.string.tooltip_search), rect.centerX(), rect.centerY());
                break;
            case 2:
                showTip(getString(R.string.pin_note), getString(R.string.swipe_right), 0);
                break;
            case 3:
                showTip(getString(R.string.delete_note), getString(R.string.swipe_left), 1);
                break;
            case 4:
                showTip(getString(R.string.show_details), getString(R.string.click_on_note), 2);
                binding.checkbox.setVisibility(View.VISIBLE);
                binding.continueButton.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void showTip(String primaryText, String secondaryText, int position) {
        binding.notesRecyclerview.scrollToPosition(position);
        CardView card = (CardView) linerLayoutManager.findViewByPosition(position);
        if (card != null) {
            Rect rect = new Rect();
            card.getGlobalVisibleRect(rect);
            float target = rect.left;
            if (next == 3) {
                target = rect.right;
            } else if (next == 4) {
                target = rect.centerX();
            }
            showTip(primaryText, secondaryText, target, rect.centerY());
        }
    }

    private void showTip(String primaryText, String secondaryText, float targetLeftPoint, float targetTopPoint) {
        builder.setTarget(targetLeftPoint, targetTopPoint)
                .setPrimaryText(primaryText)
                .setSecondaryText(secondaryText)
                .show();

    }

    @Override
    public void onNoteClickListener(Note note) {

    }

    @Override
    public void onFavoriteButtonClickListener(Note note, int position) {

    }
}