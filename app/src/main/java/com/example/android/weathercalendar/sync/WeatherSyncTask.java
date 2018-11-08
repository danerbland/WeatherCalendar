package com.example.android.weathercalendar.sync;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.android.data.AppDatabase;
import com.example.android.data.WeatherEntry;
import com.example.android.model.ForecastChunk;
import com.example.android.utils.NetworkUtils;
import com.example.android.utils.OpenWeatherJsonUtils;
import com.example.android.weathercalendar.R;
import com.example.android.weathercalendar.WeatherCalendarWidget;


import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class WeatherSyncTask {

    private static final String TAG = WeatherSyncTask.class.getSimpleName();

    synchronized public static void syncWeather(final Context context) {

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        try {
            updateDatabase(sp, context);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Help from StackOverflow question 3455123
        Intent intent = new Intent(context, WeatherCalendarWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[]ids = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, WeatherCalendarWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        context.sendBroadcast(intent);
    }


    public static final void updateDatabase(SharedPreferences sp, Context context) throws IOException {

        ArrayList<ForecastChunk> mForecastChunks;

        Float latitude = sp.getFloat(context.getString(R.string.pref_location_latitude_key), Float.valueOf(String.valueOf(181)));
        Float longitude = sp.getFloat(context.getString(R.string.pref_location_longitude_key), Float.valueOf(String.valueOf(181)));



        URL forecastRequestURL = NetworkUtils.buildWeatherQueryURL(latitude, longitude, context);
        String jsonResponse = NetworkUtils.getResponseFromHttpUrl(forecastRequestURL);
        mForecastChunks = OpenWeatherJsonUtils.getForecastChunksFromJSON(jsonResponse);

        //If no data comes back, return. Otherwise, continue.
        if (mForecastChunks == null || mForecastChunks.size() == 0) {
            Log.e(TAG, "chunkArrayList returned null or size = 0");
            return;
        }

        //Delete old weather data
        AppDatabase mDb = AppDatabase.getInstance(context.getApplicationContext());
        mDb.weatherDao().deleteAllWeather();

        //Insert new weather data
        for (ForecastChunk chunk : mForecastChunks) {
            WeatherEntry entry = new WeatherEntry(chunk.getmDate(),
                    chunk.getmChunkMain(), chunk.getmChunkWeather(), chunk.getmClouds(), chunk.getmChunkWind(), chunk.getmSysPod(), chunk.getmDateText());
            mDb.weatherDao().insertWeatherEntry(entry);
        }

    }

}
