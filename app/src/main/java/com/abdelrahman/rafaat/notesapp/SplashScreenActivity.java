package com.abdelrahman.rafaat.notesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.abdelrahman.rafaat.notesapp.databinding.ActivitySplashScreenBinding;

public class SplashScreenActivity extends AppCompatActivity {
    private ActivitySplashScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //initUI();
        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
            }
        }, 2000);*/

        /*binding.radioGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int selectedOption = binding.radioGroup1.getCheckedRadioButtonId();
                RadioButton radioButton = findViewById(selectedOption);
                binding.textView.setText(radioButton.getTag().toString());

            }
        });*/
        binding.colorPickerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedOption = binding.colorPickerView.getCheckedRadioButtonId();
                RadioButton radioButton = findViewById(selectedOption);
              //  binding.colorView.setBackgroundColor(getResources().getColor(R.array.));
                //setText(radioButton.getTag().toString());
                getColor(R.array.colors[0]);
            }
        });
    }

    private void initUI() {
        //  Animation bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_screen_bottom_animation);
        //  binding.titleTextView.setAnimation(bottomAnimation);
    }
}