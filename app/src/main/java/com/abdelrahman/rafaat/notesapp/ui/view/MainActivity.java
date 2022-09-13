package com.abdelrahman.rafaat.notesapp.ui.view;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.abdelrahman.rafaat.notesapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity /*implements OnNotesClickListener, PopupMenu.OnMenuItemClickListener*/ {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

}