package com.example.wesocial;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Dashboard extends AppCompatActivity {
    SharedPreferences sharedpreferences;
       @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_page);
        sharedpreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        Button logout = findViewById(R.id.logout);
        logout.setOnClickListener(v -> logOut());


    }

    private void logOut() {
        //DELETE ALL keys
        SharedPreferences.Editor sharedPrefEditor = sharedpreferences.edit();
        sharedPrefEditor.clear();
        sharedPrefEditor.apply();
        Intent intent = new Intent(this, LoginRegisterForgot.class);
        startActivity(intent);
    }
}