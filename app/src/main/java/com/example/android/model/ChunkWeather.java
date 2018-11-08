package com.example.android.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ChunkWeather implements Parcelable {
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

    protected ChunkWeather(Parcel in) {
        mId = in.readInt();
        mMain = in.readString();
        mDescription = in.readString();
        mIcon = in.readString();
    }

    public static final Creator<ChunkWeather> CREATOR = new Creator<ChunkWeather>() {
        @Override
        public ChunkWeather createFromParcel(Parcel in) {
            return new ChunkWeather(in);
        }

        @Override
        public ChunkWeather[] newArray(int size) {
            return new ChunkWeather[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mMain);
        dest.writeString(mDescription);
        dest.writeString(mIcon);
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

    @Override
    public int describeContents() {
        return 0;
    }

}
