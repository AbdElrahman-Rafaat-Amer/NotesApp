package com.abdelrahman.rafaat.notesapp.ui.view.activities;

import android.animation.Animator;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.abdelrahman.rafaat.notesapp.R;
import com.abdelrahman.rafaat.notesapp.database.LocalSource;
import com.abdelrahman.rafaat.notesapp.databinding.ActivitySplashScreenBinding;
import com.abdelrahman.rafaat.notesapp.model.Repository;
import com.abdelrahman.rafaat.notesapp.model.RepositoryInterface;
import com.abdelrahman.rafaat.notesapp.ui.viewmodel.NoteViewModel;
import com.abdelrahman.rafaat.notesapp.utils.BiometricUtils;
import com.abdelrahman.rafaat.notesapp.utils.RootUtil;
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

import java.util.concurrent.Executor;

public class SplashScreenActivity extends AppCompatActivity {
    private ActivitySplashScreenBinding binding;
    private AppUpdateManager appUpdateManager;
    private final int appUpdateType = AppUpdateType.IMMEDIATE;

    private boolean isDeviceRooted = false;
    private boolean isEmulator = false;
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
            showSnackBar(getString(R.string.update_complete_message), Snackbar.LENGTH_INDEFINITE, true);
        }
    };

    private NoteViewModel noteViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);
        updateTheme();
        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.getRoot().setVisibility(View.INVISIBLE);

        isDeviceRooted = RootUtil.isDeviceRooted(this);
        isEmulator = RootUtil.isEmulator(this);
        if (isDeviceRooted || isEmulator) {
            checkBiometricAuthenticationAvailability();
        } else {
            if (appUpdateType == AppUpdateType.FLEXIBLE) {
                appUpdateManager.registerListener(stateUpdatedListener);
            }
            checkForAppUpdate();
        }
    }

    private void updateTheme(){
        int theme = noteViewModel.getTheme();
        switch (theme) {
            case AppCompatDelegate.MODE_NIGHT_YES:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case AppCompatDelegate.MODE_NIGHT_NO:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
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
                    boolean isTaskSuccessful = task.isSuccessful();
                    if (isTaskSuccessful) {
                        boolean isUpdateAvailable = task.getResult().updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE;
                        if (!isUpdateAvailable && task.isComplete()) {
                            checkBiometricAuthenticationAvailability();
                        }
                    }
                })
                .addOnFailureListener(exception -> {
                    showSnackBar(exception.getLocalizedMessage(), Snackbar.LENGTH_LONG, false);
                    checkBiometricAuthenticationAvailability();
                });
    }

    private void startUpdate(AppUpdateInfo appUpdateInfo) {
        appUpdateManager.startUpdateFlowForResult(appUpdateInfo, someActivityResultLauncher, AppUpdateOptions.newBuilder(appUpdateType)
                .setAllowAssetPackDeletion(true)
                .build());
    }

    private void checkBiometricAuthenticationAvailability() {

        int result = BiometricUtils.checkBiometricAuthenticationAvailability(this);
        boolean isBiometricEnabled = Repository.getInstance(
                LocalSource.getInstance(getApplicationContext()), getApplicationContext()).isBiometricEnabled();
        if (result == BiometricManager.BIOMETRIC_SUCCESS && isBiometricEnabled) {
            showBiometricAuthentication();
        } else {
            initUI();
        }
    }

    private void showBiometricAuthentication() {

        Executor executor = ContextCompat.getMainExecutor(this);

        BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                if (errorCode == 10) {
                    finish();
                } else {
                    showSnackBar(errString.toString(), Snackbar.LENGTH_LONG, false);
                }
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                initUI();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(getString(R.string.biometric_title))
                .setSubtitle(getString(R.string.biometric_subtitle))
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.BIOMETRIC_WEAK | BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                .setConfirmationRequired(false)
                .build();

        biometricPrompt.authenticate(promptInfo);

    }

    private void initUI() {
        binding.getRoot().setVisibility(View.VISIBLE);
        binding.splashAnimation.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animator) {
            }

            @Override
            public void onAnimationEnd(@NonNull Animator animator) {
                boolean showToolTip = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("IsFirstTime", true);
                if (showToolTip)
                    startActivity(new Intent(SplashScreenActivity.this, ToolTipActivity.class));
                else
                    startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animator) {
            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animator) {
            }
        });
        binding.splashAnimation.playAnimation();
        Animation bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_screen_bottom_animation);
        binding.descriptionTextView.setAnimation(bottomAnimation);

        Animation topAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_screen_top_animation);
        binding.titleTextView.setAnimation(topAnimation);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isDeviceRooted && !isEmulator) {
            appUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
                if (appUpdateType == AppUpdateType.IMMEDIATE) {
                    if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                        startUpdate(appUpdateInfo);
                    }
                } else if (appUpdateType == AppUpdateType.FLEXIBLE) {
                    if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                        showSnackBar(getString(R.string.update_complete_message), Snackbar.LENGTH_INDEFINITE, true);
                    }
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (appUpdateType == AppUpdateType.FLEXIBLE) {
            appUpdateManager.unregisterListener(stateUpdatedListener);
        }
    }

    private void showSnackBar(String message, int length, boolean isActionNeeded) {
        Snackbar snackbar = Snackbar.make(binding.getRoot(), message, length);
        if (isActionNeeded) {
            snackbar.setAction(getString(R.string.restart), view -> appUpdateManager.completeUpdate());
            snackbar.setActionTextColor(ContextCompat.getColor(this, R.color.red));
        }
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
        dialogMessage.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        dialogMessage.setText(message);

        updateButton.setOnClickListener(v -> {
            checkForAppUpdate();
            alertDialog.dismiss();
        });

        exitButton.setOnClickListener(v -> {
            alertDialog.dismiss();
            if (appUpdateType == AppUpdateType.IMMEDIATE) {
                System.exit(0);
            } else if (appUpdateType == AppUpdateType.FLEXIBLE) {
                initUI();
            }
        });

    }
}