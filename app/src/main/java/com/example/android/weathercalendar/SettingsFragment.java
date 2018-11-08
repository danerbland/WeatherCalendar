package com.example.android.weathercalendar;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.util.Log;

import com.example.android.weathercalendar.sync.LocationSyncTask;
import com.example.android.weathercalendar.sync.WeatherSyncIntentService;

import java.io.IOException;
import java.util.List;


public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = SettingsFragment.class.getSimpleName();
    private PreferenceScreen mPreferenceScreen;

    private void setPreferenceSummary(Preference preference, Object value) {

        String stringValue = value.toString();

        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list (since they have separate labels/values).
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else {
            // For other preferences, set the summary to the value's simple string representation.
            preference.setSummary(stringValue);
        }
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        // Add 'general' preferences, defined in the XML file
        addPreferencesFromResource(R.xml.pref_general);

        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        mPreferenceScreen = getPreferenceScreen();
        int count = mPreferenceScreen.getPreferenceCount();
        for (int i = 0; i < count; i++) {
            Preference p = mPreferenceScreen.getPreference(i);
            if (!(p instanceof CheckBoxPreference)) {
                String value = sharedPreferences.getString(p.getKey(), "");
                setPreferenceSummary(p, value);
            }
            if(p.getKey().matches(getString(R.string.pref_location_edittext_key)) && sharedPreferences.getBoolean(getString(R.string.pref_use_current_location_key), true)){
                p.setVisible(false);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        // unregister the preference change listener
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        // register the preference change listener
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Activity activity = getActivity();

        //If the user wants to use the current location, hide the "Select Location" EditText.
        if(key.equals(getString(R.string.pref_use_current_location_key))){
            int count = mPreferenceScreen.getPreferenceCount();
            for (int i = 0; i < count; i++) {
                Preference p = mPreferenceScreen.getPreference(i);
                if(p.getKey().matches(getString(R.string.pref_location_edittext_key)) && sharedPreferences.getBoolean(getString(R.string.pref_use_current_location_key), true)){
                    p.setVisible(false);
                    LocationSyncTask.syncLocationAndWeather(getContext());
                }
                if(p.getKey().matches(getString(R.string.pref_location_edittext_key)) && !sharedPreferences.getBoolean(getString(R.string.pref_use_current_location_key), true)){
                    p.setVisible(true);
                }
            }
        }

        //If the user puts in a new location manually.
        else if (key.equals(getString(R.string.pref_location_edittext_key))) {
            Log.d(TAG, "Location Key Changed");
            Geocoder geocoder = new Geocoder(getContext());
            try {
                List<Address> locations = geocoder.getFromLocationName(sharedPreferences.getString(getString(R.string.pref_location_edittext_key), "New York, NY"), 1);
                if(locations.size()!=0) {

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(getString(R.string.pref_location_edittext_key), locations.get(0).getLocality() + ", " + locations.get(0).getAdminArea());
                    editor.putFloat(getString(R.string.pref_location_latitude_key), Float.valueOf(Double.toString(locations.get(0).getLatitude())));
                    editor.putFloat(getString(R.string.pref_location_longitude_key), Float.valueOf(Double.toString(locations.get(0).getLongitude())));

                    Log.d(TAG, "Location Latitude: " + Double.toString(locations.get(0).getLatitude()));
                    Log.d(TAG, "Location Longitude: " + Double.toString(locations.get(0).getLongitude()));

                    editor.commit();
                    Intent intentToSyncImmediately = new Intent(getContext(), WeatherSyncIntentService.class);
                    getContext().startService(intentToSyncImmediately);
                    Log.e(TAG, locations.get(0).getLocality() + ", " + locations.get(0).getAdminArea());
                } else {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(getString(R.string.pref_location_edittext_key), "No Location Found");
                    editor.commit();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            Intent intentToSyncImmediately = new Intent(activity.getApplicationContext(), WeatherSyncIntentService.class);
            activity.startService(intentToSyncImmediately);

        } else if (key.equals(getString(R.string.pref_units_key))) {
            if(activity.getClass().getSimpleName().matches(DayDetailActivity.class.getSimpleName())){
                Log.d(TAG, "Units changed from Day Detail Screen");
                startActivity(activity.getIntent());
            }
        }
        Preference preference = findPreference(key);
        if (null != preference) {
            if (!(preference instanceof CheckBoxPreference)) {
                setPreferenceSummary(preference, sharedPreferences.getString(key, ""));
            }
        }
    }
}
