package com.skybyn;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class MainActivity extends AppCompatActivity {
    private Button qr_scan;
    private Button website;
    private Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);

        // Initialize buttons
        qr_scan = findViewById(R.id.qr_scan);
        website = findViewById(R.id.visitWebsite);
        logout = findViewById(R.id.logoutBtn);

        // Set click listeners
        qr_scan.setOnClickListener(view -> {
            Intent qr_scanner = new Intent(this, QRScanner.class);
            startActivity(qr_scanner);
        });

        website.setOnClickListener(view -> {
            String url = "https://skybyn.no/";
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        });

        logout.setOnClickListener(view -> {
            Intent login = new Intent(this, Login.class);
            clearSavedLoginDetails();
            startActivity(login);
            finish();
        });
    }

    private SharedPreferences getEncryptedSharedPreferences() throws GeneralSecurityException, IOException {
        MasterKey masterKey = new MasterKey.Builder(this)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build();

        return EncryptedSharedPreferences.create(
                this,
                "skybyn_creds",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );
    }

    private void clearSavedLoginDetails() {
        try {
            SharedPreferences sharedPreferences = getEncryptedSharedPreferences();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("username");
            editor.remove("password");
            editor.apply();
            Log.d("AutoLogin", "Cleared saved login details");
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }
}
