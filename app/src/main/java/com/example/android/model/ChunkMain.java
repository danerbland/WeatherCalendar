package com.example.android.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ChunkMain implements Parcelable {
    private double mMainTemp;
    private double mMinTemp;
    private double mMaxTemp;
    private double mPressure;
    private double mSeaLevel;
    private double mGroundLevel;
    private double mHumidity;
    private double mTempKF;

    public ChunkMain (double mainTemp, double minTemp, double maxTemp, double pressure, double seaLevel, double groundLevel, double humidity, double tempKF){
        mMainTemp = mainTemp;
        mMinTemp = minTemp;
        mMaxTemp = maxTemp;
        mPressure = pressure;
        mSeaLevel = seaLevel;
        mGroundLevel = groundLevel;
        mHumidity = humidity;
        mTempKF = tempKF;
    }

    protected ChunkMain(Parcel in) {
        mMainTemp = in.readDouble();
        mMinTemp = in.readDouble();
        mMaxTemp = in.readDouble();
        mPressure = in.readDouble();
        mSeaLevel = in.readDouble();
        mGroundLevel = in.readDouble();
        mHumidity = in.readDouble();
        mTempKF = in.readDouble();
    }

    public static final Creator<ChunkMain> CREATOR = new Creator<ChunkMain>() {
        @Override
        public ChunkMain createFromParcel(Parcel in) {
            return new ChunkMain(in);
        }

        @Override
        public ChunkMain[] newArray(int size) {
            return new ChunkMain[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(mMainTemp);
        dest.writeDouble(mMinTemp);
        dest.writeDouble(mMaxTemp);
        dest.writeDouble(mPressure);
        dest.writeDouble(mSeaLevel);
        dest.writeDouble(mGroundLevel);
        dest.writeDouble(mHumidity);
        dest.writeDouble(mTempKF);
    }


    public double getmMainTemp() {
        return mMainTemp;
    }

    public void setmMainTemp(double mMainTemp) {
        this.mMainTemp = mMainTemp;
    }

    public double getmMinTemp() {
        return mMinTemp;
    }

    public void setmMinTemp(double mMinTemp) {
        this.mMinTemp = mMinTemp;
    }

    public double getmMaxTemp() {
        return mMaxTemp;
    }

    public void setmMaxTemp(double mMaxTemp) {
        this.mMaxTemp = mMaxTemp;
    }

    public double getmPressure() {
        return mPressure;
    }

    public void setmPressure(double mPressure) {
        this.mPressure = mPressure;
    }

    public double getmSeaLevel() {
        return mSeaLevel;
    }

    public void setmSeaLevel(double mSeaLevel) {
        this.mSeaLevel = mSeaLevel;
    }

    public double getmGroundLevel() {
        return mGroundLevel;
    }

    public void setmGroundLevel(double mGroundLevel) {
        this.mGroundLevel = mGroundLevel;
    }

    public double getmHumidity() {
        return mHumidity;
    }

    public void setmHumidity(double mHumidity) {
        this.mHumidity = mHumidity;
    }

    public double getmTempKF() {
        return mTempKF;
    }

    public void setmTempKF(double mTempKF) {
        this.mTempKF = mTempKF;
    }

    @Override
    public int describeContents() {
        return 0;
    }


}

