package com.abdelrahman.rafaat.notesapp.ui.view.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.abdelrahman.rafaat.notesapp.R;
import com.abdelrahman.rafaat.notesapp.databinding.ActivitySplashScreenBinding;

public class SplashScreenActivity extends AppCompatActivity {
    private ActivitySplashScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        boolean showToolTip = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("IsFirstTime", true);
        initUI();
        new Handler().postDelayed(() -> {
            if (showToolTip)
                startActivity(new Intent(SplashScreenActivity.this, ToolTipActivity.class));
            else
                startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
        }, 2000);

    }

    private void initUI() {
        Animation bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_screen_bottom_animation);
        binding.descriptionTextView.setAnimation(bottomAnimation);

        Animation topAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_screen_top_animation);
        binding.titleTextView.setAnimation(topAnimation);
    }
}