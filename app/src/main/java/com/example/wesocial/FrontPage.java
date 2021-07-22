package com.example.wesocial;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FrontPage extends AppCompatActivity {
    SharedPreferences sharedpreferences;
    private Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_page);
        SharedPreferences sharedpreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        logout = findViewById(R.id.logout);
        logout.setOnClickListener(v -> logOut());


    }


    private void logOut() {
        /**
         * DELETE ALL keys
         */
         SharedPreferences.Editor sharedPrefEditor = sharedpreferences.edit();
         sharedPrefEditor.clear();
         sharedPrefEditor.commit();
        Intent intent = new Intent(this, LoginRegisterForgot.class);
        startActivity(intent);
    }
}