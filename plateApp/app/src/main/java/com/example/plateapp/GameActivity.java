package com.example.plateapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {

    private TextView[] holes;
    private BluetoothController bluetoothController;
    private boolean play;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        holes = new TextView[4];
        holes[0] = (TextView) findViewById(R.id.textView1);
        holes[1] = findViewById(R.id.textView2);
        holes[2] = findViewById(R.id.textView3);
        holes[3] = (TextView) findViewById(R.id.textView4);

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
            boolean scored = false;
            while(play){
                try {
                    int data = Integer.parseInt(bluetoothController.readData());
                    if (data == 0) {
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
        Log.d("data", idx + "");
        Log.d("cur", cur);
    }

    public void onExitClick(View view){
        play = false;
        bluetoothController.disconnect();
        startActivity(new Intent(GameActivity.this, MainActivity.class));
    }

}