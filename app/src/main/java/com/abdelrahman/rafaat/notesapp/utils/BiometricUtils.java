package com.abdelrahman.rafaat.notesapp.utils;

import android.content.Context;

import androidx.biometric.BiometricManager;

public class BiometricUtils {

    private BiometricUtils() {
    }

    public static int checkBiometricAuthenticationAvailability(Context context) {
        int result;
        BiometricManager biometricManager = BiometricManager.from(context);
        switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.BIOMETRIC_WEAK | BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                result = BiometricManager.BIOMETRIC_SUCCESS;
                break;

            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                result = BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE;
                break;

            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                result = BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE;
                break;

            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                result = BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED;
                break;

            case BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED:
                result = BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED;
                break;

            case BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED:
                result = BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED;
                break;

            case BiometricManager.BIOMETRIC_STATUS_UNKNOWN:
            default:
                result = BiometricManager.BIOMETRIC_STATUS_UNKNOWN;
                break;
        }
        return result;
    }

}
