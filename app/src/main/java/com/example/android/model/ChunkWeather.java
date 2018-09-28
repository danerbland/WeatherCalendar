package com.example.android.model;

public class ChunkWeather {
    private int mId;
    private String mMain;
    private String mDescription;
    private String mIcon;

    public ChunkWeather (int id, String main, String description, String icon){
        mId = id;
        mMain = main;
        mDescription = description;
        mIcon = icon;
    }

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public String getmMain() {
        return mMain;
    }

    public void setmMain(String mMain) {
        this.mMain = mMain;
    }

    public String getmDescription() {
        return mDescription;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public String getmIcon() {
        return mIcon;
    }

    public void setmIcon(String mIcon) {
        this.mIcon = mIcon;
    }
}
