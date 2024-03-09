package com.abdelrahman.rafaat.notesapp.ui.view.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.abdelrahman.rafaat.notesapp.R;
import com.abdelrahman.rafaat.notesapp.databinding.ActivityContactUsBinding;
import com.google.android.material.snackbar.Snackbar;

public class ContactUsActivity extends AppCompatActivity {

    private ActivityContactUsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContactUsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.telegramTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTelegram();
            }
        });

        binding.gmailTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGmail();
            }
        });

        binding.facebookImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLink("https://www.facebook.com/abdo.raafat.amer/");
            }
        });

        binding.instagramImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLink("https://www.instagram.com/abdo.raafat.amer/");
            }
        });

        binding.linkedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLink("https://www.linkedin.com/in/abdelrahman-raafat-anwer-amer/");
            }
        });

    }

    private void openTelegram() {
        Intent telegramIntent = new Intent(Intent.ACTION_VIEW);
        telegramIntent.setData(Uri.parse("tg://user?username=" + getString(R.string.telegram_user_name)));
        try {
            startActivity(telegramIntent);
        } catch (ActivityNotFoundException exception) {
            showSnackBar(binding.getRoot(), exception.getLocalizedMessage());
        }
    }

    private void showSnackBar(View view, String message) {
        Snackbar snackBar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
                .setActionTextColor(Color.WHITE);
        snackBar.getView().setBackgroundColor(Color.RED);
        snackBar.show();
    }

    private void openGmail() {
        String recipientEmail = getString(R.string.email);
        String emailSubject = getString(R.string.email_title);

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        Uri uri = Uri.parse("mailto:" + recipientEmail);
        intent.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
        intent.setData(uri);

        // Check if there's an app to handle the intent
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the activity with the intent
            startActivity(intent);
        } else {
            showSnackBar(binding.getRoot(), "No email app found");
        }
    }

    private void openLink(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}