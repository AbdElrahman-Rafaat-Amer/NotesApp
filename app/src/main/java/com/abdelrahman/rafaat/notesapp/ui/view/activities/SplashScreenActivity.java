package com.abdelrahman.rafaat.notesapp.ui.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.abdelrahman.rafaat.notesapp.BuildConfig;
import com.abdelrahman.rafaat.notesapp.R;
import com.abdelrahman.rafaat.notesapp.databinding.ActivitySplashScreenBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.appupdate.AppUpdateOptions;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallErrorCode;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;

public class SplashScreenActivity extends AppCompatActivity {
    private ActivitySplashScreenBinding binding;
    private AppUpdateManager appUpdateManager;
    private final String TAG = "UPDATE_MANAGER";
    private final int APP_UPDATE_REQUEST_CODE = 123456;

    private final ActivityResultLauncher<IntentSenderRequest> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartIntentSenderForResult(),
                // handle callback
                    System.exit(0);
                } else if (result.getResultCode() != RESULT_OK) {
                    Log.i(TAG, "onActivityResult: the update is cancelled or fails you can request to start the update again.");
                    Log.i(TAG, "onActivityResult: result.toString()---->" + result.toString());

                    checkForAppUpdate();
                }
            });

    private final InstallStateUpdatedListener stateUpdatedListener = new InstallStateUpdatedListener() {
        @Override
        public void onStateUpdate(@NonNull InstallState installState) {
            switch (installState.installStatus()) {
                case InstallStatus.DOWNLOADING:
                    Log.i(TAG, "onStateUpdate:  InstallStatus.DOWNLOADING");
                    break;
                case InstallStatus.DOWNLOADED:
                    Log.i(TAG, "onStateUpdate:  InstallStatus.DOWNLOADED");
                    break;
                case InstallStatus.INSTALLING:
                    Log.i(TAG, "onStateUpdate:  InstallStatus.INSTALLING");
                    break;
                case InstallStatus.INSTALLED:
                    Log.i(TAG, "onStateUpdate:  InstallStatus.INSTALLED");
                    break;
                case InstallStatus.PENDING:
                    Log.i(TAG, "onStateUpdate:  InstallStatus.PENDING");
                    break;
                case InstallStatus.FAILED:
                    Log.i(TAG, "onStateUpdate:  InstallStatus.FAILED");
                    Log.i(TAG, "onStateUpdate:  installState.installErrorCode()---> " + installState.installErrorCode());
                    break;
                case InstallStatus.CANCELED:
                    Log.i(TAG, "onStateUpdate:  InstallStatus.CANCELED");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        checkForAppUpdate();
    }

    private void checkForAppUpdate() {
        appUpdateManager = AppUpdateManagerFactory.create(getApplicationContext());
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
                    if (isUpdateAvailable) {
                        checkUpdateType(appUpdateInfo);
                    }
                })
                .addOnFailureListener(error -> {
                    Log.e(TAG, "onFailure: error.LocalizedMessage----------> " + error.getLocalizedMessage());
                    Log.e(TAG, "onFailure: error.getMessage----------------> " + error.getMessage());
                })
                .addOnCanceledListener(() -> Log.w(TAG, "onCanceled: "))
                        initUI();

                });
    }

    private void checkUpdateType(AppUpdateInfo appUpdateInfo) {
        int DAYS_FOR_FLEXIBLE_UPDATE = 2;
        boolean isFlexibleUpdate = appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE);
        if (appUpdateInfo.clientVersionStalenessDays() != null && appUpdateInfo.clientVersionStalenessDays() >= DAYS_FOR_FLEXIBLE_UPDATE) {
        }
        appUpdateManager.registerListener(stateUpdatedListener);
        appUpdateManager.startUpdateFlowForResult(appUpdateInfo, someActivityResultLauncher, AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE)
                .setAllowAssetPackDeletion(true)
                .build());


    }

    private void initUI() {
        Animation bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_screen_bottom_animation);
        binding.descriptionTextView.setAnimation(bottomAnimation);

        Animation topAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_screen_top_animation);
        binding.titleTextView.setAnimation(topAnimation);
        startAnimation();
    }

    private void startAnimation() {
        boolean showToolTip = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("IsFirstTime", true);
        new Handler().postDelayed(() -> {
            if (showToolTip)
                startActivity(new Intent(SplashScreenActivity.this, ToolTipActivity.class));
            else
                startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
        }, 2000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {
                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    initUI();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        appUpdateManager.unregisterListener(stateUpdatedListener);
    }
}