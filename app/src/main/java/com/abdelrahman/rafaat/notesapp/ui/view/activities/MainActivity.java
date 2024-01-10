package com.abdelrahman.rafaat.notesapp.ui.view.activities;

import androidx.fragment.app.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.abdelrahman.rafaat.notesapp.R;
import com.abdelrahman.rafaat.notesapp.databinding.ActivityMainBinding;
import com.abdelrahman.rafaat.notesapp.model.Note;
import com.abdelrahman.rafaat.notesapp.ui.view.fragments.HomeFragment;
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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.navigation.fragment.NavHostFragment;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private NavigationIconClickListener navigationClickListener;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initAds();
        setUpToolbar();

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        // Add a destination changed listener
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(NavController controller, androidx.navigation.NavDestination destination, Bundle arguments) {
                // Check which fragment is the current destination
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
                Log.i("FRAGMENT_TAG", "onDestinationChanged: currentFragment " + currentFragment);
                Log.i("FRAGMENT_TAG", "onDestinationChanged: destination " + destination);
                Log.i("FRAGMENT_TAG", "onDestinationChanged: controller " + controller);

                int destinationId = destination.getId();
                Log.i("FRAGMENT_TAG", "onDestinationChanged: destinationId " + destinationId);
                Log.i("FRAGMENT_TAG", "onDestinationChanged: getDisplayName " + destination.getDisplayName());
                Log.i("FRAGMENT_TAG", "onDestinationChanged: destination.toString " + destination.toString());
                Log.i("FRAGMENT_TAG", "onDestinationChanged: getNavigatorName " + destination.getNavigatorName());
                Log.i("FRAGMENT_TAG", "onDestinationChanged: getClass " + destination.getClass());
                Log.i("FRAGMENT_TAG", "onDestinationChanged: getLabel " + destination.getLabel());
//                boolean isHome = ((FragmentNavigator.Destination) destination)._className.equals(HomeFragment.class.getName());
                if (destinationId == R.id.home_fragment || destinationId == R.id.archived_fragment) {
                    Log.i("FRAGMENT_TAG", "onDestinationChanged: HomeFragment");
                    binding.toolBar.setVisibility(View.VISIBLE);
                    binding.drawerLinearLayout.setVisibility(View.VISIBLE);
                } else {
                    Log.i("FRAGMENT_TAG", "onDestinationChanged: AnotherFragment");
                    binding.toolBar.setVisibility(View.GONE);
                    binding.drawerLinearLayout.setVisibility(View.GONE);
                }
//                if (isHome) {
//                    Log.i("FRAGMENT_TAG", "onDestinationChanged: HomeFragment");
//                    binding.toolBar.setVisibility(View.VISIBLE);
//                    binding.drawerLinearLayout.setVisibility(View.VISIBLE);
//                } else {
//                    Log.i("FRAGMENT_TAG", "onDestinationChanged: AnotherFragment");
//                    binding.toolBar.setVisibility(View.GONE);
//                    binding.drawerLinearLayout.setVisibility(View.GONE);
//                }

//                if (currentFragment instanceof HomeFragment) {
//                    Log.i("FRAGMENT_TAG", "onDestinationChanged: HomeFragment");
//                    binding.toolBar.setVisibility(View.VISIBLE);
//                    binding.drawerLinearLayout.setVisibility(View.VISIBLE);
//                } else {
//                    Log.i("FRAGMENT_TAG", "onDestinationChanged: AnotherFragment");
//                    binding.toolBar.setVisibility(View.GONE);
//                    binding.drawerLinearLayout.setVisibility(View.GONE);
//                }
            }
        });
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
    }

    private void closeMenu() {
        navigationClickListener.startAnimation();
        binding.toolBar.setNavigationIcon(R.drawable.ic_menu);
    }
}