package com.example.plateapp;

public class Game {

    private int m_time, m_iterations;
    private double[] m_probabilities;

    private static final Game m_instance = new Game();

    private Game(){
        this.m_time = 0;
        this.m_iterations = 0;
        m_probabilities = new double[] {0.0, 0.0, 0.0, 0.0};
    }

    public static Game getInstance(){
        return m_instance;
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
