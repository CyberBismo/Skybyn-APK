package com.skybyn;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class MainActivity extends AppCompatActivity {

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);

        getWindow().setNavigationBarColor(
                ContextCompat.getColor(this, R.color.main)
        );

        // Initialize buttons
        ImageView house = findViewById(R.id.house);
        ImageView qr_scan = findViewById(R.id.qr_scan);
        ImageView logout = findViewById(R.id.logoutBtn);

        // Set click listeners
        house.setOnClickListener(view -> {
            Intent home = new Intent(this, MainActivity.class);
            startActivity(home);
        });

        qr_scan.setOnClickListener(view -> {
            Intent qr_scanner = new Intent(this, QRScanner.class);
            startActivity(qr_scanner);
        });

        logout.setOnClickListener(view -> {
            Intent login = new Intent(this, Login.class);
            clearSavedLoginDetails();
            startActivity(login);
            finish();
        });

        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // API 31+
        //    RenderEffect blurEffect = RenderEffect.createBlurEffect(20f, 20f, Shader.TileMode.CLAMP);
        //    View bottom_nav = findViewById(R.id.bottom_nav);
        //    bottom_nav.setRenderEffect(blurEffect);
        //}
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
