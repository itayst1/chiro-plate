package com.example.plateapp;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private TextView[] holes;
    private ImageView colors;
    private int[] colorsArr;
    private TextView timer;
    private TextView iterations;

    private BluetoothController bluetoothController;
    private GameInstance gameInstance;

    private boolean play;
    private int remainingIterations;

    private Random random = new Random();

    private MediaPlayer scoreSound;
    private MediaPlayer winSound;
    private MediaPlayer loseSound;


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

        scoreSound = MediaPlayer.create(this, R.raw.score_sound);
        winSound = MediaPlayer.create(this, R.raw.win_sound);
        loseSound = MediaPlayer.create(this, R.raw.lose_sound);


        colorsArr = new int[] {
            R.drawable.red_active,
            R.drawable.blue_active,
            R.drawable.green_active,
            R.drawable.yellow_active
        };

        bluetoothController = BluetoothController.getInstance();
        gameInstance = GameInstance.getInstance();

        play = true;
        remainingIterations = GameInstance.getInstance().getIterations();

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
        if(GameInstance.getInstance().getTime() > 0) {
            timer.setText("⧖" + GameInstance.getInstance().getTime());
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    new CountDownTimer((long) ((gameInstance.getTime() + 0.4) * 1000), 1000) {

                        public void onTick(long millisUntilFinished) {
                            if (play)
                                timer.setText("⧖" + millisUntilFinished / 1000);
                        }

                        public void onFinish() {
                            if (remainingIterations > 0) {
                                play = false;
                                colors.setImageResource(R.drawable.inactive);
                                timer.setText("משחק נגמר");
                                loseSound.start();
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
        if(gameInstance.isTutorial()){
            getTurorialThread().start();
        }
        else{
            getLevelsThread().start();
        }
    }

    private Thread getTurorialThread(){
        return new Thread(()->{
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
                        updateScore(data, -1);
                    }
                    Thread.sleep(50);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private Thread getLevelsThread(){
        return new Thread(()->{
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
            boolean scored = true;
            int goal = 0, prevGoal = goal;
            while(play){
                if(remainingIterations == 0) {
                    play = false;
                    iterations.setText("ניצחת!!");
                    winSound.start();
                    colors.setImageResource(R.drawable.inactive);
                    gameInstance.getLevelsData().put(gameInstance.getLevel(), gameInstance.getLevelsData().get(GameInstance.getInstance().getLevel()).replace("undone", "done"));
                    break;
                }
                try {
                    int data = Integer.parseInt(bluetoothController.readData());
                    if (data == 0) {
                        if(scored) {
                            while(goal == prevGoal)
                                goal = getNext(gameInstance.getProbabilities());
                            prevGoal = goal;
                            colors.setImageResource(colorsArr[goal]);
                        }
                        scored = false;
                    } else if (!scored) {
                        scored = updateScore(data, goal);
                    }
                    Thread.sleep(50);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private boolean updateScore(int data, int goal){
        for(int i = 0; i < 4; i++){
            int isActive = data % 10;
            if(data > 0)
                data /= 10;
            if(isActive == 1){
                if(i == goal || goal == -1) {
                    String cur = (Integer.parseInt(holes[i].getText().toString()) + 1) + "";
                    holes[i].setText(cur);
                    colors.setImageResource(colorsArr[i]);
                    if(remainingIterations > 0) {
                        iterations.setText(--remainingIterations + "");
                    }
                    scoreSound.seekTo(0);
                    scoreSound.start();
                    return true;
                }
            }
        }
        return false;
    }

    private int getNext(double[] probabilities){
        double result = random.nextDouble();
        double total = 0;
        for(int i = 0; i < probabilities.length; i++){
            if(probabilities[i] == 0){
                continue;
            }
            total += probabilities[i];
            if(result <= total){
                return i;
            }
        }
        return random.nextInt(4);
    }

    public void onExitClick(View view){
        play = false;
        startActivity(new Intent(GameActivity.this, HomeActivity.class));
    }

}