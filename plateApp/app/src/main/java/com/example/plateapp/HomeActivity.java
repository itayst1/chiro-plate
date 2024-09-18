package com.example.plateapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
    }

    public void onTutorialClick(View view){
        startActivity(new Intent(HomeActivity.this, GameActivity.class));
    }

    public void onLevelsClick(View view){
        startActivity(new Intent(HomeActivity.this, LevelsActivity.class));
    }
}