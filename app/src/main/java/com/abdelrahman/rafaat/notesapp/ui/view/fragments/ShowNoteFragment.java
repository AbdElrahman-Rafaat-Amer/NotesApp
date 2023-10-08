package com.abdelrahman.rafaat.notesapp.ui.view.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.abdelrahman.rafaat.notesapp.R;
import com.abdelrahman.rafaat.notesapp.databinding.FragmentShowBinding;
import com.abdelrahman.rafaat.notesapp.model.Note;
import com.abdelrahman.rafaat.notesapp.ui.viewmodel.NoteViewModel;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;

import java.util.Locale;

public class ShowNoteFragment extends Fragment {
    private FragmentShowBinding binding;
    private NoteViewModel noteViewModel;
    private Note currentNote;
    private AlertDialog alertDialog;
    private boolean isUnLock = true;
    private AdView adView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentShowBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initFacebookAds();
        initViewModel();
        checkRTL();
        initUi();
        showNoteDetails();
        binding.showNoteBodyTextView.setMovementMethod(new LinkMovementMethod());

    }

    private void initFacebookAds() {
        adView = new AdView(requireContext(), getString(R.string.facebook_banner_ad_replacement), AdSize.BANNER_HEIGHT_50);
        binding.facebookAdsView.addView(adView);
        adView.loadAd(adView.buildLoadAdConfig().build());
    }

    private void initViewModel() {
        noteViewModel = new ViewModelProvider(requireActivity()).get(NoteViewModel.class);
    }

    private void showNoteDetails() {
        currentNote = noteViewModel.getCurrentNote();
        binding.showNoteTitleTextView.setText(currentNote.getTitle());
        int textSize = currentNote.getTextSize();
        int textAlignment = currentNote.getTextAlignment();
        binding.showNoteBodyTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        binding.showNoteBodyTextView.setGravity(textAlignment);

        Html.ImageGetter getter = source -> {
            Bitmap bitmap = BitmapFactory.decodeFile(source);
            BitmapDrawable drawable = new BitmapDrawable(Resources.getSystem(), bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            return drawable;
        };

        Spanned noteBody = Html.fromHtml(currentNote.getBody(), Html.FROM_HTML_MODE_LEGACY, getter, null);
        binding.showNoteBodyTextView.setText(noteBody);
        if (currentNote.getColor() != -1)
            binding.showNoteRootView.setBackgroundColor(currentNote.getColor());
        else {
            int color = getResources().getColor(R.color.defaultBackGround, null);
            binding.showNoteRootView.setBackgroundColor(color);
        }
        if (currentNote.getPassword().isEmpty()) {
            binding.unlockNoteImageView.setImageResource(R.drawable.ic_lock);
            isUnLock = false;
        }
    }

    private void initUi() {
        binding.editNoteImageView.setOnClickListener(v -> Navigation.findNavController(requireView()).navigate(R.id.action_showNote_to_editNote));

        binding.unlockNoteImageView.setOnClickListener(v -> updatePassword());

        binding.shareNote.setOnClickListener(view -> shareNote());

        binding.goBackImageView.setOnClickListener(v -> Navigation.findNavController(requireView()).popBackStack());
    }

    private void checkRTL() {
        String language = Locale.getDefault().getDisplayName();
        int directionality = Character.getDirectionality(language.charAt(0));
        boolean isRTL = directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT || directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC;
        if (isRTL)
            binding.goBackImageView.setImageResource(R.drawable.ic_arrow_right);
    }

    private void updatePassword() {
        if (isUnLock)
            updateNote();
        else {
            Navigation.findNavController(requireView()).navigate(R.id.password_fragment);
        }
    }

    private void shareNote() {
        Intent myIntent = new Intent(Intent.ACTION_SEND);
        myIntent.setType("text/plain");
        String title = currentNote.getTitle();
        Spanned noteBody = Html.fromHtml(currentNote.getBody(), Html.FROM_HTML_MODE_LEGACY, null, null);
        String note = getString(R.string.sharing_note, getString(R.string.title), title, getString(R.string.body), noteBody);
        myIntent.putExtra(Intent.EXTRA_TEXT, note);
        startActivity(Intent.createChooser(myIntent, getString(R.string.share_using)));
    }

    private void updateNote() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);

        builder.setMessage(getString(R.string.remove_password))
                .setPositiveButton(R.string.remove, (dialog, which) -> {
                    currentNote.setPassword("");
                    noteViewModel.updateNote(currentNote);
                    binding.unlockNoteImageView.setImageResource(R.drawable.ic_lock);
                    isUnLock = false;
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> alertDialog.dismiss());

        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

    }

    @Override
    public void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

}