package com.example.android.data;

import com.example.android.model.ChunkMain;
import com.example.android.model.ChunkWeather;
import com.example.android.model.ChunkWind;

public class ExampleWeatherEntry {

    WeatherEntry mWeatherEntry = new WeatherEntry(001100, new ChunkMain(70, 60, 80, 333, 45, 43, 56, 567), new ChunkWeather(4, "main stuff", "weather description", "4"), 44, new ChunkWind(45, 34), "44", "today");

}
