package com.example.android.weathercalendar;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.android.data.AppDatabase;
import com.example.android.data.WeatherEntry;
import com.example.android.model.ForecastChunk;
import com.example.android.utils.CalendarUtils;
import com.example.android.utils.OpenWeatherJsonUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Implementation of App Widget functionality.
 */
public class WeatherCalendarWidget extends AppWidgetProvider {

    private static final String TAG = WeatherCalendarWidget.class.getSimpleName();

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, int imageResourceId, String eventDescription, long eventTimeInMillis,
                                ArrayList<ForecastChunk> forecastChunks, String weatherDescription, String weatherTemp) {

        String eventDateAndTime;
        Calendar calendar = Calendar.getInstance();

        //If we have an event
        if(eventTimeInMillis != -1) {
            calendar.setTimeInMillis(eventTimeInMillis);
            eventDateAndTime = CalendarUtils.getDateString(calendar) + "\n" + CalendarUtils.getTimeString(calendar);
        } else {
            eventDateAndTime = "No Event Time";
        }
        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather_calendar_widget);
        views.setTextViewText(R.id.textview_widget_event_description, eventDescription);
        views.setTextViewText(R.id.textview_widget_event_time, eventDateAndTime);
        views.setTextViewText(R.id.textview_widget_weather_description, weatherDescription);
        views.setTextViewText(R.id.textview_widget_weather_temp, weatherTemp);
        if(imageResourceId != -1) {
            views.setImageViewResource(R.id.imageview_widget_weather_forecast, imageResourceId);
        }

        if(eventTimeInMillis != -1) {
            Intent intent = new Intent(context, DayDetailActivity.class);
            intent.putExtra(context.getString(R.string.year_bundle_key), calendar.get(Calendar.YEAR));
            intent.putExtra(context.getString(R.string.month_bundle_key), calendar.get(Calendar.MONTH));
            intent.putExtra(context.getString(R.string.day_bundle_key), calendar.get(Calendar.DAY_OF_MONTH));
            intent.putParcelableArrayListExtra(context.getString(R.string.forecast_bundle_key), forecastChunks);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.linearlayout_widget_container, pendingIntent);
        } else {
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.linearlayout_widget_container, pendingIntent);
        }


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {

        Log.d(TAG, "onUpdate Fired");

        final AppDatabase mDb = AppDatabase.getInstance(context);


        Calendar today = Calendar.getInstance();
        final long monthInMillis = 2592000000L;
        final long dayInMillis = 1000 * 60 * 60 * 24;
        final long threeHoursInMillis = 10800000L;
        final long oneAndAHalfHoursInMillis = threeHoursInMillis/2;
        Calendar monthFromToday = Calendar.getInstance();
        monthFromToday.setTimeInMillis((today.getTimeInMillis() + monthInMillis));
        final Cursor cursor = CalendarUtils.queryEventsFromEventTable(context.getApplicationContext(), today.getTimeInMillis(), monthFromToday.getTimeInMillis());


        Log.d(TAG, "looking for events between " + today.getTimeInMillis() + " and " + monthFromToday.getTimeInMillis());
        if(cursor != null && cursor.getCount() > 0){
            Log.e(TAG, "Event FOUND!");

            cursor.moveToFirst();

            final String eventTitle = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.TITLE));
            Calendar eventCalendar = Calendar.getInstance();
            eventCalendar.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(CalendarContract.Events.DTSTART)));
            final String eventTime = CalendarUtils.getTimeString(eventCalendar);
            final String eventDate = CalendarUtils.getDateString(eventCalendar);



            //Now we must look for relevant weather information in our database.
            AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {

                    List<WeatherEntry> weatherEntries = mDb.weatherDao().loadWeatherEntryList();
                    ArrayList<ForecastChunk> mForecastChunks = new ArrayList<>();

                    //If we have weather data
                    if(weatherEntries.size()>0){
                        //Look through the weather data to find the forecast chunk closest to the event.
                        // It must be within 3 hours.
                        for(WeatherEntry entry: weatherEntries){
                            ForecastChunk chunk;
                            //If there is any weather data within 3 hours of our event's start time.
                            chunk = new ForecastChunk(
                                    entry.getDatelong(),
                                    entry.getChunkmain(),
                                    entry.getChunkweather(),
                                    entry.getClouds(),
                                    entry.getChunkwind(),
                                    entry.getSyspod(),
                                    entry.getDatetext()
                            );
                            mForecastChunks.add(chunk);
                        }
                        for(ForecastChunk chunk:mForecastChunks){

                            if(Math.abs((chunk.getmDate() * 1000L) - cursor.getLong(cursor.getColumnIndex(CalendarContract.Events.DTSTART))) <= oneAndAHalfHoursInMillis){

                                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
                                String tempString;
                                if(sp.getString(context.getString(R.string.pref_units_key), context.getString(R.string.pref_units_imperial)).matches(context.getString(R.string.pref_units_imperial))){
                                    tempString = OpenWeatherJsonUtils.convertToFahrenheit(chunk.getmChunkMain().getmMainTemp()) + "°";
                                } else{
                                    tempString = OpenWeatherJsonUtils.convertToCelcius(chunk.getmChunkMain().getmMainTemp()) + "°";
                                }

                                updateWidgets(context, appWidgetManager,
                                        OpenWeatherJsonUtils.getDrawableIdFromWeatherCode(chunk.getmChunkWeather().getmId()),
                                        eventTitle,
                                        cursor.getLong(cursor.getColumnIndex(CalendarContract.Events.DTSTART)),
                                        mForecastChunks,
                                        chunk.getmChunkWeather().getmDescription(),
                                        tempString,
                                        appWidgetIds
                                );
                            }
                        }
                    }//end If we have weather data
                    else {
                        updateWidgets(context, appWidgetManager,
                                -1,
                                eventTitle,
                                cursor.getLong(cursor.getColumnIndex(CalendarContract.Events.DTSTART)),
                                mForecastChunks,
                                "",
                                "",
                                appWidgetIds
                        );
                    }
                    return null;
                }
            }.execute();

        }//End If we have Cursor

        // There may be multiple widgets active, so update all of them
        //If there is no cursor or no event information, load an empty widget
        else for (int appWidgetId : appWidgetIds) {
            Log.d(TAG, "NO EVENT FOUND");
            updateAppWidget(context, appWidgetManager, appWidgetId, 0, "No Event", -1, null, "", "");
        }
    }

    public static void updateWidgets(Context context, AppWidgetManager appWidgetManager,
                                          int imgRes, String eventDescription, long eventTimeInMillis, ArrayList<ForecastChunk> forecastChunks, String weatherDescription, String weatherTemp, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, imgRes, eventDescription, eventTimeInMillis, forecastChunks, weatherDescription, weatherTemp);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

