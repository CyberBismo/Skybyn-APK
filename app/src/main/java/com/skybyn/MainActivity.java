package com.skybyn;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.google.android.datatransport.backend.cct.BuildConfig;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;

public class MainActivity extends AppCompatActivity {
    private ImageView qr_scan;
    private LinearLayout website;
    private ImageView logout;
    private ImageView info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);

        // Initialize buttons
        info = findViewById(R.id.info);
        qr_scan = findViewById(R.id.qr_scan);
        website = findViewById(R.id.visitWebsite);
        logout = findViewById(R.id.logoutBtn);

        // Set click listeners
        info.setOnClickListener(view -> {
            checkForUpdates();
        });

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

    private void checkForUpdates() {
        Thread updateCheckThread = new Thread(() -> {
            try {
                URL url = new URL("https://api.skybyn.com/apkUpdate/version.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                String platform = "android";
                String version = BuildConfig.VERSION_NAME;
                String postData = "platform=" + platform + "&version=" + version;

                try (OutputStream os = connection.getOutputStream()) {
                    os.write(postData.getBytes());
                }

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder responseBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        responseBuilder.append(line);
                    }
                    reader.close();

                    JSONObject jsonResponse = new JSONObject(responseBuilder.toString());
                    String status = jsonResponse.getString("status");
                    String message = jsonResponse.getString("message");

                    runOnUiThread(() -> {
                        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                        if (message.contains("newer version available")) {
                            promptUserToUpdate();
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Update check failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
        updateCheckThread.start();
    }

    private void promptUserToUpdate() {
        // Assuming that the URL to the APK is provided in another part of the response or is hardcoded here.
        String apkUrl = "https://api.skybyn.com/apkUpdate/app-debug.apk"; // This URL should be dynamically received ideally.

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Available");
        builder.setMessage("A new version of the app is available. Do you want to update now?");
        builder.setPositiveButton("Update", (dialog, id) -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(apkUrl));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
        builder.setNegativeButton("Later", (dialog, id) -> dialog.dismiss());
        builder.create().show();
    }
}
