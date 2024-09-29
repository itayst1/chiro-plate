package com.example.plateapp;

import android.content.Intent;
import android.hardware.biometrics.BiometricManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewParent;
import android.view.autofill.AutofillValue;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.HashMap;

public class LevelsActivity extends AppCompatActivity {

    private TableLayout levels;
    private HashMap<Integer, String> levelsData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_levels);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        levels = findViewById(R.id.levels);

        levelsData = new HashMap<>();

        levelsData.put(1, "undone,30,10");
        levelsData.put(2, "done,20,15");
        levelsData.put(3, "undone,10,10");

        initiateGrid();
    }

    public void onHomeClick(View view){
        startActivity(new Intent(LevelsActivity.this, HomeActivity.class));
    }

    public void onLevelClick(View view){
        Button temp = (Button) view;
        String[] data = levelsData.get(Integer.parseInt((String) temp.getText())).split(",");
        Game.getInstance().setTime(Integer.parseInt(data[1]));
        Game.getInstance().setIterations(Integer.parseInt(data[2]));
        startActivity(new Intent(LevelsActivity.this, GameActivity.class));
    }

    public void initiateGrid(){
        TableRow row = new TableRow(this);
        int numOfViews = 0;
        for(int i = 1; i <= levelsData.size(); i++){
            if(numOfViews == 2){
                numOfViews = 0;
                levels.addView(row);
                row = new TableRow(this);
            }
            row.addView(createDeviceButton(i + ""));
            numOfViews++;
        }
        levels.addView(row);
    }

    public Button createDeviceButton(String name){
        Button levelButton = new Button(LevelsActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            levelButton.setWidth(this.getWindow().getWindowManager().getCurrentWindowMetrics().getBounds().width()/2);
        }
        levelButton.setAllCaps(false);
        levelButton.setText(name);
        levelButton.setTextSize(35);
        levelButton.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        levelButton.setBackgroundColor(0xE05b5f60);
        levelButton.setTextColor(0xE0FFFFFF);
        levelButton.setOnClickListener(this::onLevelClick);

        if(levelsData.get(Integer.parseInt(name)).split(",")[0].equals("done")){
            levelButton.setBackgroundColor(0xE000bc65);
        }
        return levelButton;
    }
}