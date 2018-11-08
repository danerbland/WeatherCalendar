package com.example.android.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ForecastChunk implements Parcelable{
    private long mDate;
    private ChunkMain mChunkMain;
    private ChunkWeather mChunkWeather;
    private int mClouds;
    private ChunkWind mChunkWind;
    private String mSysPod;
    private String mDateText;


    public ForecastChunk(long mDate, ChunkMain mChunkMain, ChunkWeather mChunkWeather, int mClouds, ChunkWind mChunkWind, String mSysPod, String mDateText) {
        this.mDate = mDate;
        this.mChunkMain = mChunkMain;
        this.mChunkWeather = mChunkWeather;
        this.mClouds = mClouds;
        this.mChunkWind = mChunkWind;
        this.mSysPod = mSysPod;
        this.mDateText = mDateText;
    }


    protected ForecastChunk(Parcel in) {
        mDate = in.readLong();
        mChunkMain = in.readParcelable(ChunkMain.class.getClassLoader());
        mChunkWeather = in.readParcelable(ChunkWeather.class.getClassLoader());
        mClouds = in.readInt();
        mChunkWind = in.readParcelable(ChunkWind.class.getClassLoader());
        mSysPod = in.readString();
        mDateText = in.readString();
    }

    public static final Creator<ForecastChunk> CREATOR = new Creator<ForecastChunk>() {
        @Override
        public ForecastChunk createFromParcel(Parcel in) {
            return new ForecastChunk(in);
        }

        @Override
        public ForecastChunk[] newArray(int size) {
            return new ForecastChunk[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mDate);
        dest.writeParcelable(mChunkMain, flags);
        dest.writeParcelable(mChunkWeather, flags);
        dest.writeInt(mClouds);
        dest.writeParcelable(mChunkWind, flags);
        dest.writeString(mSysPod);
        dest.writeString(mDateText);

    }


    public ChunkWeather getmChunkWeather() {
        return mChunkWeather;
    }

    public void setmChunkWeather(ChunkWeather mChunkWeather) {
        this.mChunkWeather = mChunkWeather;
    }

    public int getmClouds() {
        return mClouds;
    }

    public void setmClouds(int mClouds) {
        this.mClouds = mClouds;
    }

    public ChunkWind getmChunkWind() {
        return mChunkWind;
    }

    public void setmChunkWind(ChunkWind mChunkWind) {
        this.mChunkWind = mChunkWind;
    }

    public String getmSysPod() {
        return mSysPod;
    }

    public void setmSysPod(String mSysPod) {
        this.mSysPod = mSysPod;
    }

    public String getmDateText() {
        return mDateText;
    }

    public void setmDateText(String mDateText) {
        this.mDateText = mDateText;
    }

    public long getmDate() {
        return mDate;
    }

    public void setmDate(long mDate) {
        this.mDate = mDate;
    }

    public ChunkMain getmChunkMain() {
        return mChunkMain;
    }

    public void setmChunkMain(ChunkMain mChunkMain) {
        this.mChunkMain = mChunkMain;
    }

    @Override
    public int describeContents() {
        return 0;
    }


}
