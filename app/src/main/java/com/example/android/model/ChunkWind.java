package com.example.android.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ChunkWind implements Parcelable {
    private double mSpeed;
    private double mDirection;


    public ChunkWind (double speed, double direction){
        mSpeed = speed;
        mDirection = direction;
    }

    protected ChunkWind(Parcel in) {
        mSpeed = in.readDouble();
        mDirection = in.readDouble();
    }

    public static final Creator<ChunkWind> CREATOR = new Creator<ChunkWind>() {
        @Override
        public ChunkWind createFromParcel(Parcel in) {
            return new ChunkWind(in);
        }

        @Override
        public ChunkWind[] newArray(int size) {
            return new ChunkWind[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(mSpeed);
        dest.writeDouble(mDirection);
    }
}
