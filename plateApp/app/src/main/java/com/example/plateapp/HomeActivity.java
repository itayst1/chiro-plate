package com.example.plateapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
    }

    public void onTutorialClick(View view){
        GameInstance.getInstance().setMode(true);
        GameInstance.getInstance().setTime(-1);
        GameInstance.getInstance().setIterations(-1);
        GameInstance.getInstance().setProbabilities(new double[]{0.25, 0.25, 0.25, 0.25});
        startActivity(new Intent(HomeActivity.this, GameActivity.class));
    }

    public void onLevelsClick(View view){
        startActivity(new Intent(HomeActivity.this, LevelsActivity.class));
    }

    public void onDisconnectClick(View view){
        BluetoothController.getInstance().disconnect();
        startActivity(new Intent(HomeActivity.this, MainActivity.class));
    }
}