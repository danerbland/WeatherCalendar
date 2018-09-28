package com.example.android.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface WeatherDao {

    @Query("SELECT * FROM weather ORDER BY id")
    List<WeatherEntry> loadForecast();

    @Insert
    void insertWeatherEntry(WeatherEntry weatherEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateWeatherEntry(WeatherEntry weatherEntry);

    @Delete
    void deleteWeatherEntry(WeatherEntry weatherEntry);

    @Query("DELETE FROM weather")
    void deleteAllWeather();

}
