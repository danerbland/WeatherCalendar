package com.example.android.model;

public class ChunkWind {
    private double mSpeed;
    private double mDirection;


    public ChunkWind (double speed, double direction){
        mSpeed = speed;
        mDirection = direction;
    }

    public double getmSpeed() {
        return mSpeed;
    }

    public void setmSpeed(double mSpeed) {
        this.mSpeed = mSpeed;
    }

    public double getmDirection() {
        return mDirection;
    }

    public void setmDirection(double mDirection) {
        this.mDirection = mDirection;
    }
}
