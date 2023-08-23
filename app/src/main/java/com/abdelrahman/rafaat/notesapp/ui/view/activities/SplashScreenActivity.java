package com.abdelrahman.rafaat.notesapp.ui.view.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.abdelrahman.rafaat.notesapp.R;
import com.abdelrahman.rafaat.notesapp.databinding.ActivitySplashScreenBinding;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.appupdate.AppUpdateOptions;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;

public class SplashScreenActivity extends AppCompatActivity {
    private ActivitySplashScreenBinding binding;
    private AppUpdateManager appUpdateManager;
    private final int appUpdateType = AppUpdateType.IMMEDIATE;

    private final ActivityResultLauncher<IntentSenderRequest> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartIntentSenderForResult(),
            result -> {
                if (result.getResultCode() == RESULT_CANCELED) {
                    if (appUpdateType == AppUpdateType.IMMEDIATE) {
                        addDialogNote(getString(R.string.immediate_update_message));
                    } else if (appUpdateType == AppUpdateType.FLEXIBLE) {
                        addDialogNote(getString(R.string.flexible_update_message));
                    }
                } else if (result.getResultCode() != RESULT_OK) {
                    checkForAppUpdate();
                }
            });

    private final InstallStateUpdatedListener stateUpdatedListener = installState -> {
        if (installState.installStatus() == InstallStatus.DOWNLOADED) {
            popupSnackbarForCompleteUpdate();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (appUpdateType == AppUpdateType.FLEXIBLE) {
            appUpdateManager.registerListener(stateUpdatedListener);
        }
        checkForAppUpdate();
    }

    private void checkForAppUpdate() {
        appUpdateManager = AppUpdateManagerFactory.create(getApplicationContext());
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
                    boolean isUpdateAvailable = appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE;
                    if (isUpdateAvailable) {
                        startUpdate(appUpdateInfo);
                    }
                })
                .addOnCompleteListener(task -> {
                    boolean isUpdateAvailable = task.getResult().updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE;
                    if (!isUpdateAvailable && task.isComplete()) {
                        initUI();
                    }
                });
    }

    private void startUpdate(AppUpdateInfo appUpdateInfo) {
        appUpdateManager.startUpdateFlowForResult(appUpdateInfo, someActivityResultLauncher, AppUpdateOptions.newBuilder(appUpdateType)
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
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateType == AppUpdateType.IMMEDIATE) {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    startUpdate(appUpdateInfo);
                }
            } else if (appUpdateType == AppUpdateType.FLEXIBLE) {
                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    popupSnackbarForCompleteUpdate();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (appUpdateType == AppUpdateType.FLEXIBLE) {
            appUpdateManager.unregisterListener(stateUpdatedListener);
        }
    }

    private void popupSnackbarForCompleteUpdate() {
        Snackbar snackbar =
                Snackbar.make(binding.getRoot(),
                        getString(R.string.update_complete_message),
                        Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(getString(R.string.restart), view -> appUpdateManager.completeUpdate());
        snackbar.setActionTextColor(ContextCompat.getColor(this, R.color.red));
        snackbar.show();
    }

    private void addDialogNote(String message) {
        View view = getLayoutInflater().inflate(R.layout.dialog_layout, null);
        TextView dialogMessage = view.findViewById(R.id.dialog_message_textView);
        Button updateButton = view.findViewById(R.id.save_button);
        Button exitButton = view.findViewById(R.id.discard_button);
        updateButton.setText(getString(R.string.update));
        exitButton.setText(getString(R.string.exit));

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
        builder.setView(view);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        Rect displayRectangle = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        alertDialog.getWindow().setLayout(
                (int) (displayRectangle.width() * 0.82f),
                ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogMessage.setText(message);

        updateButton.setOnClickListener(v -> {
            checkForAppUpdate();
            alertDialog.dismiss();
        });

        exitButton.setOnClickListener(v -> {
            alertDialog.dismiss();
            System.exit(0);
        });

    }
}