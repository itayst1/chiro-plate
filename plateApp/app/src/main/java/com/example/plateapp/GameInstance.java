package com.example.plateapp;

import java.util.HashMap;

public class GameInstance {

    private int m_level;
    private boolean m_tutorial;
    private int m_time, m_iterations;
    private double[] m_probabilities;

    private HashMap<Integer, String> levelsData;

    private static final GameInstance m_instance = new GameInstance();

    private GameInstance(){
        this.m_level = 0;
        this.m_tutorial = false;
        this.m_time = 0;
        this.m_iterations = 0;
        this.m_probabilities = new double[] {0.0, 0.0, 0.0, 0.0};
        levelsData = new HashMap<>();
    }

    public static GameInstance getInstance(){
        return m_instance;
    }

    public HashMap<Integer, String> getLevelsData(){
        return this.levelsData;
    }

    public int getLevel(){
        return this.m_level;
    }

    public boolean isTutorial(){
        return this.m_tutorial;
    }

    public int getTime(){
        return this.m_time;
    }

    public int getIterations(){
        return this.m_iterations;
    }

    public double[] getProbabilities(){
        return this.m_probabilities;
    }

    public void setLevel(int level){
        this.m_level = level;
    }

    public void setMode(boolean isTutorial){
        this.m_tutorial = isTutorial;
    }

    public void setTime(int time){
        this.m_time = time;
    }

    public void setIterations(int iterations){
        this.m_iterations = iterations;
    }

    public void setProbabilities(double[] probabilities){
        this.m_probabilities = probabilities;
    }
}
