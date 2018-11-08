package com.example.android.weathercalendar.sync;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.android.model.ForecastChunk;
import com.example.android.weathercalendar.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LocationSyncTask {

    private static final String TAG = LocationSyncTask.class.getSimpleName();

    synchronized public static void syncLocationAndWeather(final Context context) {
        Log.e(TAG, "syncWeather Triggered");

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        if(sp.getBoolean(context.getString(R.string.pref_use_current_location_key), true)) {
            try {
                updateWithLastKnownLocation(context, sp);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private static void updateSharedPreferences(Context context, Location location, SharedPreferences sharedPreferences){

        //Update the Shared Preferences with the location information.
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(context.getString(R.string.pref_location_latitude_key), Float.valueOf(Double.toString(location.getLatitude())));
        editor.putFloat(context.getString(R.string.pref_location_longitude_key), Float.valueOf(Double.toString(location.getLongitude())));


        //Because a location doesn't have an explicit Locale, we will retrieve the locality and Admin area from a geocoder and
        // store it in our SharedPreferences for use in the UX.
        try {
            List<Address> address = new Geocoder(context).getFromLocation(location.getLatitude(),location.getLongitude(), 1);
            editor.putString(context.getString(R.string.pref_location_edittext_key), address.get(0).getLocality() + ", " + address.get(0).getAdminArea());
        } catch (IOException e) {
            e.printStackTrace();
        }

        editor.commit();

        return;
    }

    private static void updateWithLastKnownLocation (final Context context, final SharedPreferences sp) throws IOException{
        Log.e(TAG, "started updateWithLastKnownLocation");

        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        try{
            final Task locationTask = mFusedLocationClient.getLastLocation();
            locationTask.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    Log.e(TAG, "getLastLocation complete");
                    if(locationTask.isSuccessful()){
                        Log.e(TAG, "getLastLocation successful");
                        Location location = (Location) task.getResult();

                        //If we have a location, we update the shared preferences and then update the database.
                        if(location!=null) {
                            updateSharedPreferences(context, location, sp);
                            Intent intentToSyncWeather = new Intent(context, WeatherSyncIntentService.class);
                            context.startService(intentToSyncWeather);
                            ArrayList<ForecastChunk> mForecastChunks;
                        }
                    }
                }
            });
        } catch(SecurityException e){
            Log.e(TAG, "getLastKnownLocation: Security Exception : " + e.getMessage() );
        }
        Log.e(TAG, "ended updateWithLastKnownLocation");
    }

}
