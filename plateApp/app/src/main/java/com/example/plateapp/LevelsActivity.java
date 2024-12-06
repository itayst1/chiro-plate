package com.example.plateapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

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

        levelsData = GameInstance.getInstance().getLevelsData();

        if(levelsData.isEmpty()) {//level,time,iterations,probabilities,stars
            levelsData.put(1, "1,20,10,0.25,0.25,0.25,0.25,0");
            levelsData.put(2, "2,50,10,0.5,0,0.5,0,0");
            levelsData.put(3, "3,50,20,0,0.5,0,0.5,0");
            levelsData.put(4, "4,50,15,0,0.33,0.33,0.33,0");
        }

        initiateGrid();
    }

    public void onHomeClick(View view){
        startActivity(new Intent(LevelsActivity.this, HomeActivity.class));
    }

    public void onLevelClick(View view){
        Button temp = (Button) view;
        String[] data = levelsData.get(Integer.parseInt((String) temp.getText())).split(",");
        GameInstance.getInstance().setMode(false);
        GameInstance.getInstance().setLevel(Integer.parseInt(data[0]));
        GameInstance.getInstance().setTime(Integer.parseInt(data[1]));
        GameInstance.getInstance().setIterations(Integer.parseInt(data[2]));
        double[] probabilities = new double[4];
        for(int i = 3; i < 7; i++){
            probabilities[i-3] = Double.parseDouble(data[i]);
        }
        GameInstance.getInstance().setProbabilities(probabilities);
        GameInstance.getInstance().setStars(Integer.parseInt(data[7]));
        startActivity(new Intent(LevelsActivity.this, GameActivity.class));
    }

    public void initiateGrid(){
        TableRow row = new TableRow(this);
        for(int i = 1; i <= levelsData.size(); i++){
            row.addView(createDeviceButton(i + ""));
            row.addView(createStars(levelsData.get(i).split(",")[7]));
            levels.addView(row);
            row = new TableRow(this);
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
        return levelButton;
    }

    public Button createStars(String stars){
        Button levelStars = new Button(LevelsActivity.this);
        levelStars.setActivated(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            levelStars.setWidth(this.getWindow().getWindowManager().getCurrentWindowMetrics().getBounds().width()/2);
        }
        levelStars.setAllCaps(false);
        levelStars.setText("stars: " + stars);
        levelStars.setTextSize(35);
        levelStars.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        levelStars.setBackgroundColor(0xE05b5f60);
        levelStars.setTextColor(0xE0FFFFFF);
        return levelStars;
    }
}