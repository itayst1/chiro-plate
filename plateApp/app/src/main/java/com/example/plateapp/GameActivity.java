package com.example.plateapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {

    private TextView[] holes;
    private ImageView colors;
    private int[] colorsArr;
    private TextView timer;
    private TextView iterations;

    private BluetoothController bluetoothController;
    private Game gameInstance;

    private boolean play;
    private int remainingIterations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        holes = new TextView[4];
        holes[0] = (TextView) findViewById(R.id.redCounter);
        holes[1] = (TextView) findViewById(R.id.blueCounter);
        holes[2] = (TextView) findViewById(R.id.greenCounter);
        holes[3] = (TextView) findViewById(R.id.yellowCounter);

        colors = (ImageView) findViewById(R.id.colors);

        timer = (TextView) findViewById(R.id.timer);
        iterations = (TextView) findViewById(R.id.iterations);

        colorsArr = new int[] {
            R.drawable.red_active,
            R.drawable.blue_active,
            R.drawable.green_active,
            R.drawable.yellow_active
        };

        bluetoothController = BluetoothController.getInstance();
        gameInstance = Game.getInstance();

        play = true;
        remainingIterations = Game.getInstance().getIterations();

        if(remainingIterations > 0)
            iterations.setText(remainingIterations + "");
        else
            iterations.setText("∞");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startGame();
            }
        }, 500);
        if(Game.getInstance().getTime() > 0) {
            timer.setText("⧖" + Game.getInstance().getTime());
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    new CountDownTimer((long) ((gameInstance.getTime() + 0.5) * 1000), 1000) {

                        public void onTick(long millisUntilFinished) {
                            if (play)
                                timer.setText("⧖" + millisUntilFinished / 1000);
                        }

                        public void onFinish() {
                            if (remainingIterations > 0) {
                                play = false;
                                colors.setImageResource(R.drawable.inactive);
                                timer.setText("משחק נגמר");
                            }
                        }
                    }.start();
                }
            }, 2000);
        }
        else
            timer.setText("⧖∞");
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
                if(remainingIterations == 0){
                    play = false;
                    iterations.setText("ניצחת!!");
                    colors.setImageResource(R.drawable.inactive);
                    break;
                }
                try {
                    int data = Integer.parseInt(bluetoothController.readData());
                    if (data == 0) {
                        if(scored)
                            colors.setImageResource(R.drawable.inactive);
                        scored = false;
                    } else if (!scored) {
                        scored = true;
                        updateScore(data);
                        iterations.setText(--remainingIterations + "");
                    }
                    Thread.sleep(50);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void updateScore(int activated){
        for(int i = 0; i < 4; i++){
            int isActive = activated % 10;
            if(activated > 0)
                activated /= 10;
            if(isActive == 1){
                String cur = (Integer.parseInt(holes[i].getText().toString()) + 1) + "";
                holes[i].setText(cur);
                colors.setImageResource(colorsArr[i]);
            }
        }
    }

    public void onExitClick(View view){
        play = false;
        startActivity(new Intent(GameActivity.this, HomeActivity.class));
    }

}