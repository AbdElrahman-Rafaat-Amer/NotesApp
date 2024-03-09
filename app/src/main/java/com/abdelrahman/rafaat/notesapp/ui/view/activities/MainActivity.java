package com.abdelrahman.rafaat.notesapp.ui.view.activities;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.abdelrahman.rafaat.notesapp.R;
import com.abdelrahman.rafaat.notesapp.databinding.ActivityMainBinding;
import com.abdelrahman.rafaat.notesapp.model.Note;
import com.abdelrahman.rafaat.notesapp.ui.view.fragments.HomeFragment;
import com.abdelrahman.rafaat.notesapp.ui.viewmodel.NoteViewModel;
import com.abdelrahman.rafaat.notesapp.utils.NavigationIconClickListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.List;
import java.util.stream.Collectors;

import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.navigation.fragment.NavHostFragment;

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
                navController.popBackStack(R.id.archived_fragment, true);
                navController.navigate(R.id.home_fragment);
            }
            closeMenu();
        });

        binding.rootView.findViewById(R.id.pinnedNotesButton).setOnClickListener(view -> {
            closeMenu();
        });

        binding.rootView.findViewById(R.id.archivedNotesButton).setOnClickListener(view -> {
            if (navController.getCurrentDestination().getId() != R.id.archived_fragment) {
                navController.popBackStack(R.id.home_fragment, true);
                navController.navigate(R.id.archived_fragment);
            }
            closeMenu();
        });

        binding.rootView.findViewById(R.id.settingButton).setOnClickListener(view -> {
            startActivity(new Intent(this, SettingsActivity.class));
            closeMenu();
        });

        binding.rootView.findViewById(R.id.contactUsButton).setOnClickListener(view -> {
            startActivity(new Intent(this, ContactUsActivity.class));
            closeMenu();
        });

        binding.rootView.findViewById(R.id.ourAppsButton).setOnClickListener(view -> {
            openGooglePlay();
            closeMenu();
        });
    }

    private void closeMenu() {
        navigationClickListener.startAnimation();
        binding.toolBar.setNavigationIcon(R.drawable.ic_menu);
    }

    private void openGooglePlay(){
        String url = "https://play.google.com/store/apps/developer?id=Abdelrahman+Rafaat";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
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
        if (destinationId == R.id.home_fragment || destinationId == R.id.archived_fragment) {
            Log.i("FRAGMENT_TAG", "onDestinationChanged: HomeFragment");
            binding.toolBar.setVisibility(View.VISIBLE);
            binding.drawerLinearLayout.setVisibility(View.VISIBLE);
        } else {
            Log.i("FRAGMENT_TAG", "onDestinationChanged: AnotherFragment");
            binding.toolBar.setVisibility(View.GONE);
            binding.drawerLinearLayout.setVisibility(View.GONE);
        }
    }
}