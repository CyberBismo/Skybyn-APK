package com.skybyn;

import static com.skybyn.QRScanner.CAMERA_REQUEST_CODE;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;

public class Loading extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_screen);
        checkAndAutoLogin();

        // Request camera permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission();
        }
    }

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            Toast.makeText(this, "Camera access is required to display the camera preview.", Toast.LENGTH_SHORT).show();
        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
    }

    private void checkAndAutoLogin() {
        try {
            SharedPreferences sharedPreferences = getEncryptedSharedPreferences();
            String username = sharedPreferences.getString("username", null);
            String password = sharedPreferences.getString("password", null);

            if (username != null && password != null) {
                Log.d("AutoLogin", "Attempting auto-login with username: " + username);
                performLogin(username, password);
            } else {
                Log.d("AutoLogin", "No saved login credentials, showing login screen");
                showLoginScreen();
            }
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            showLoginScreen();
        }
    }

    private void showLoginScreen() {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
    }

    private void performLogin(String username, String password) {
        Thread thread = new Thread(() -> {
            try {
                URL url = new URL("https://api.skybyn.com/login.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                String data = "user=" + URLEncoder.encode(username, "UTF-8") + "&password=" + URLEncoder.encode(password, "UTF-8");
                OutputStream os = conn.getOutputStream();
                os.write(data.getBytes());
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    JSONObject jsonResponse = new JSONObject(response.toString());

                    runOnUiThread(() -> {
                        try {
                            if (jsonResponse.has("responseCode")) {
                                int code = jsonResponse.getInt("responseCode");
                                if (code == 1) {
                                    Intent intent = new Intent(this, QRScanner.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    clearSavedLoginDetails();
                                    String message = jsonResponse.optString("message", "Login failed. Please try again.");
                                    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(this, "No response code provided", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(this, "Error parsing response: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
        thread.start();
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
