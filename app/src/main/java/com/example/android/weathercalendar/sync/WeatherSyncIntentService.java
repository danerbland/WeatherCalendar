package com.example.android.weathercalendar.sync;

import android.app.IntentService;
import android.content.Intent;

public class WeatherSyncIntentService extends IntentService{

    private static final String TAG = WeatherSyncIntentService.class.getSimpleName();

    public WeatherSyncIntentService(){
        super("WeatherSyncIntentService");
        }

    @Override
    protected void onHandleIntent(Intent intent) {
        WeatherSyncTask.syncWeather(this);
    }
}
