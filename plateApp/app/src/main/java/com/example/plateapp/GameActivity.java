package com.example.plateapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {

    private TextView[] holes;
    private BluetoothController bluetoothController;
    private boolean play;

    private ImageView colors;
    private int[] colorsArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        holes = new TextView[4];
        holes[0] = (TextView) findViewById(R.id.textView1);
        holes[1] = (TextView) findViewById(R.id.textView3);
        holes[2] = (TextView) findViewById(R.id.textView4);
        holes[3] = (TextView) findViewById(R.id.textView2);

        colors = (ImageView) findViewById(R.id.colors);

        colorsArr = new int[] {
            R.drawable.red_active,
            R.drawable.blue_active,
            R.drawable.green_active,
            R.drawable.yellow_active
        };

        bluetoothController = BluetoothController.getInstance();

        play = true;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startGame();
            }
        }, 500);
    }

    public void startGame(){
        new Thread(()->{
            try {
                Thread.sleep(200);
                colors.setImageResource(R.drawable.inactive);
                Thread.sleep(250);
                for (int i = 0; i < 4; i++) {
                    colors.setImageResource(colorsArr[i]);
                    Thread.sleep(200);
                }
                Thread.sleep(100);
                colors.setImageResource(R.drawable.all_active);
                Thread.sleep(250);
                colors.setImageResource(R.drawable.inactive);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            colors.setImageResource(R.drawable.inactive);
            boolean scored = false;
            while(play){
                try {
                    int data = Integer.parseInt(bluetoothController.readData());
                    if (data == 0) {
                        if(scored)
                            colors.setImageResource(R.drawable.inactive);
                        scored = false;
                    } else if (!scored) {
                        scored = true;
                        updateScore(data);
                    }
                    Thread.sleep(100);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void updateScore(int idx){
        String cur = (Integer.parseInt(holes[idx-1].getText().toString()) + 1) + "";
        holes[idx - 1].setText(cur);
        colors.setImageResource(colorsArr[idx-1]);
    }

    public void onExitClick(View view){
        play = false;
        startActivity(new Intent(GameActivity.this, HomeActivity.class));
    }

}