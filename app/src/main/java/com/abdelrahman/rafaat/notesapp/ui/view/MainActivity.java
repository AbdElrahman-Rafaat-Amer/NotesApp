package com.abdelrahman.rafaat.notesapp.ui.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import com.abdelrahman.rafaat.notesapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private int PERMISSION_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


  /*      final int maxNumPhotosAndVideos = 10;
        Intent intent = new Intent("android.provider.action.PICK_IMAGES");
        intent.putExtra("android.provider.extra.PICK_IMAGES_MAX", maxNumPhotosAndVideos);
        startActivityForResult(intent, 2);*/

        requestPermissions();
    }

    private void requestPermissions() {
        String[] premissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.MANAGE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_MEDIA_LOCATION,
                Manifest.permission.MANAGE_MEDIA, Manifest.permission.MEDIA_CONTENT_CONTROL, Manifest.permission.MANAGE_DOCUMENTS
        };
        ActivityCompat.requestPermissions(
                this,
                premissions,
                PERMISSION_REQUEST
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this, Manifest.permission.READ_EXTERNAL_STORAGE
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                        this, Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                        this, Manifest.permission.MANAGE_EXTERNAL_STORAGE
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                        this, Manifest.permission.ACCESS_MEDIA_LOCATION
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                        this, Manifest.permission.MANAGE_MEDIA
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                        this, Manifest.permission.MEDIA_CONTENT_CONTROL
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                        this, Manifest.permission.MANAGE_DOCUMENTS
                )
                ) {
                    requestPermissions();
                }
            }

        }
    }

    /*@Override
        void onRequestPermissionsResult(
                int requestCode,
                String[] permissions,
                int[] grantResults,
                ) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            Log.i(TAG, "onRequestPermissionsResult: -------------------->")
            for (grantResult in grantResults) {
                Log.i(TAG, "onRequestPermissionsResult: for -------------------->")
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "onRequestPermissionsResult: if2 -------------------->")
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            this, Manifest.permission.READ_EXTERNAL_STORAGE,
                            ) || ActivityCompat.shouldShowRequestPermissionRationale(
                            this, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            ) || ActivityCompat.shouldShowRequestPermissionRationale(
                            this, Manifest.permission.RECORD_AUDIO,
                            )
                    ) {
                        Log.i(TAG, "onRequestPermissionsResult: if3 -------------------->")
                        binding.waitingAnimation.visibility = View.VISIBLE
                        binding.givePermission.visibility = View.VISIBLE
                        requestPermissions()
                    } else {
                        Log.i(TAG, "onRequestPermissionsResult: else if3-------------------->")
                        showSnackBar()
                        isFirstTime = false
                    }
                } else {
                    Log.i(TAG, "onRequestPermissionsResult: else if2-------------------->")
                    startActivity(Intent(this, MainActivity:: class.java))
                    finish()
                }

            }
        }
    */
  /*  private Boolean hasStoragePermission() {
        return ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }*/

}