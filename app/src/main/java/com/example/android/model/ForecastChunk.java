package com.example.android.model;

public class ForecastChunk {
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
}
