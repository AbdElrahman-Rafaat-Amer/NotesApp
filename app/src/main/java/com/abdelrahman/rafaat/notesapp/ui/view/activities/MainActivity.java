package com.abdelrahman.rafaat.notesapp.ui.view.activities;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.abdelrahman.rafaat.notesapp.databinding.ActivityMainBinding;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initAds();
    }

    private void initAds() {
        MobileAds.initialize(this);

        initTopAdsView();
        initBottomAdsView();
    }

    private void initTopAdsView() {
        AdView topAdView = binding.topAdView;
        AdRequest adRequest = new AdRequest.Builder().build();
        topAdView.loadAd(adRequest);
    }

    private void initBottomAdsView() {
        AdView bottomAdView = binding.bottomAdView;
        AdRequest adRequest = new AdRequest.Builder().build();
        bottomAdView.loadAd(adRequest);
    }
}