package com.abdelrahman.rafaat.notesapp.ui.view.activities;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.abdelrahman.rafaat.notesapp.databinding.ActivityMainBinding;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private final String ADS_TAG = "MobileAds";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initAds();
    }

    private void initAds() {
        MobileAds.initialize(this, initializationStatus ->
                Log.i(ADS_TAG, "initAds:success ")
        );

        initTopAdsView();
        initBottomAdsView();
    }

    private void initTopAdsView() {
        AdView topAdView = binding.topAdView;
        AdRequest adRequest = new AdRequest.Builder().build();
        topAdView.loadAd(adRequest);
        topAdView.setAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
                super.onAdClicked();
                Log.i(ADS_TAG, "topAdView onAdClicked: ");
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                Log.i(ADS_TAG, "topAdView onAdClosed: ");
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                super.onAdFailedToLoad(adError);
                Log.i(ADS_TAG, "topAdView onAdFailedToLoad: errorMessage " + adError.getMessage());
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
                Log.i(ADS_TAG, "topAdView onAdImpression: ");
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Log.i(ADS_TAG, "topAdView onAdLoaded: ");
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                Log.i(ADS_TAG, "topAdView onAdOpened: ");
            }

            @Override
            public void onAdSwipeGestureClicked() {
                super.onAdSwipeGestureClicked();
                Log.i(ADS_TAG, "topAdView onAdSwipeGestureClicked: ");
            }
        });
    }

    private void initBottomAdsView() {
        AdView bottomAdView = binding.bottomAdView;
        AdRequest adRequest = new AdRequest.Builder().build();
        bottomAdView.loadAd(adRequest);
        bottomAdView.setAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
                super.onAdClicked();
                Log.i(ADS_TAG, "bottomAdView onAdClicked: ");
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                Log.i(ADS_TAG, "bottomAdView onAdClosed: ");
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                Log.i(ADS_TAG, "bottomAdView onAdFailedToLoad: errorMessage " + loadAdError.getMessage());
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
                Log.i(ADS_TAG, "bottomAdView onAdImpression: ");
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Log.i(ADS_TAG, "bottomAdView onAdLoaded: ");
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                Log.i(ADS_TAG, "bottomAdView onAdOpened: ");
            }

            @Override
            public void onAdSwipeGestureClicked() {
                super.onAdSwipeGestureClicked();
                Log.i(ADS_TAG, "bottomAdView onAdSwipeGestureClicked: ");
            }
        });
    }
}