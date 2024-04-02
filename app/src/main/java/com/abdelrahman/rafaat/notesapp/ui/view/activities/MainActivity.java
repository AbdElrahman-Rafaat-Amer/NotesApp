package com.abdelrahman.rafaat.notesapp.ui.view.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;

import com.abdelrahman.rafaat.notesapp.R;
import com.abdelrahman.rafaat.notesapp.databinding.ActivityMainBinding;
import com.abdelrahman.rafaat.notesapp.ui.viewmodel.NoteViewModel;
import com.abdelrahman.rafaat.notesapp.utils.NavigationIconClickListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class MainActivity extends AppCompatActivity implements NavController.OnDestinationChangedListener {
    private ActivityMainBinding binding;
    private NavigationIconClickListener navigationClickListener;
    private NavController navController;
    private NoteViewModel noteViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);
        initAds();
        setUpToolbar();

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        navController.addOnDestinationChangedListener(this);
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

    private void setUpToolbar() {

        setSupportActionBar(binding.toolBar);

        navigationClickListener = new NavigationIconClickListener(
                this, binding.navHostFragment,
                new AccelerateDecelerateInterpolator(),
                ContextCompat.getDrawable(this, R.drawable.ic_menu),
                ContextCompat.getDrawable(this, R.drawable.ic_close_menu));
        binding.toolBar.setNavigationOnClickListener(navigationClickListener); // Menu close icon

        binding.rootView.findViewById(R.id.allNotesButton).setOnClickListener(view -> {
            if (navController.getCurrentDestination().getId() != R.id.home_fragment) {
                navController.popBackStack();
                navController.navigate(R.id.home_fragment);
            }
            closeMenu(true);
        });

        binding.rootView.findViewById(R.id.favoritesNotesButton).setOnClickListener(view -> {
            if (navController.getCurrentDestination().getId() != R.id.favorites_fragment) {
                navController.popBackStack();
                navController.navigate(R.id.favorites_fragment);
            }
            closeMenu(true);
        });

        binding.rootView.findViewById(R.id.archivedNotesButton).setOnClickListener(view -> {
            if (navController.getCurrentDestination().getId() != R.id.archived_fragment) {
                navController.popBackStack();
                navController.navigate(R.id.archived_fragment);
            }
            closeMenu(true);
        });

        binding.rootView.findViewById(R.id.settingButton).setOnClickListener(view -> {
            startActivity(new Intent(this, SettingsActivity.class));
            closeMenu(true);
        });

        binding.rootView.findViewById(R.id.contactUsButton).setOnClickListener(view -> {
            startActivity(new Intent(this, ContactUsActivity.class));
            closeMenu(true);
        });

        binding.rootView.findViewById(R.id.ourAppsButton).setOnClickListener(view -> {
            openGooglePlay();
            closeMenu(true);
        });
    }

    private void closeMenu(boolean isAnimated) {
        if (isAnimated){
            navigationClickListener.startAnimation();
        }else {
            navigationClickListener.close();
        }
        binding.toolBar.setNavigationIcon(R.drawable.ic_menu);
    }

    private void openGooglePlay() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.google_play_link)));
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        noteViewModel.refreshSettings();
    }

    @Override
    public void onDestinationChanged(@NonNull NavController navController, @NonNull NavDestination navDestination, @Nullable Bundle bundle) {
        int destinationId = navDestination.getId();
        if (destinationId == R.id.home_fragment || destinationId == R.id.archived_fragment || destinationId == R.id.favorites_fragment) {
            binding.toolBar.setVisibility(View.VISIBLE);
            binding.drawerLinearLayout.setVisibility(View.VISIBLE);
        } else {
            closeMenu(false);
            binding.toolBar.setVisibility(View.GONE);
            binding.drawerLinearLayout.setVisibility(View.GONE);
        }
    }
}