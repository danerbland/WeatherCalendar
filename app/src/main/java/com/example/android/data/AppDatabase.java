package com.example.android.data;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.util.Log;

@Database(entities = {WeatherEntry.class}, version = 1, exportSchema = false)
@TypeConverters({ChunkMainConverter.class, ChunkWeatherConverter.class, ChunkWindConverter.class})
public abstract class AppDatabase extends RoomDatabase{

    private static final String TAG = AppDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "weather";
    private static AppDatabase mInstance;

    public static AppDatabase getInstance(Context context) {
        if(mInstance == null){
            synchronized (LOCK) {
                Log.d(TAG, "Creating new database instance");
                mInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, AppDatabase.DATABASE_NAME)
                        .build();
            }
        }
        return mInstance;
    }

    public abstract WeatherDao weatherDao();

}
