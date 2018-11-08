package com.example.android.weathercalendar;

import android.Manifest;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.data.AppDatabase;
import com.example.android.data.WeatherEntry;
import com.example.android.model.ForecastChunk;
import com.example.android.utils.CalendarUtils;
import com.example.android.weathercalendar.sync.LocationSyncTask;
import com.example.android.weathercalendar.sync.WeatherSyncIntentService;

import com.example.android.weathercalendar.sync.WeatherSyncUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<ArrayList<ForecastChunk>>{

    //Permissions
    int PERMISSION_ALL = 111;
    String[] PERMISSIONS = {
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };


    private static final String TAG = MainActivity.class.getSimpleName();
    private int WEATHER_LOADER_ID = 222;

    private Location mLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private ArrayList<ForecastChunk> mData = null;
    private AppDatabase mDb;

    private SharedPreferences mSharedPreferences;

    private LocationCallback mLocationCallBack;

    private CalendarFragment mCalendarFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Ask permissions
        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
            return;
        }

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);


        //If our savedInstanceState is null, we need to initialize the fragment and loader.
        if(savedInstanceState == null){
            //If our forecast is null, load it from the database.
            if (mData == null) {
                LoaderManager.LoaderCallbacks<ArrayList<ForecastChunk>> callback = MainActivity.this;
                getSupportLoaderManager().initLoader(WEATHER_LOADER_ID, null, callback);
                //While the forecast is being loaded from the database, Initialize the fragment.
                FragmentManager fragmentManager = getSupportFragmentManager();
                mCalendarFragment = new CalendarFragment();
                fragmentManager.beginTransaction()
                        .add(R.id.calendar_fragment, mCalendarFragment)
                        .commit();
            }

        } else {
            mData = savedInstanceState.getParcelableArrayList(getString(R.string.forecast_bundle_key));

        }





        Long weatherCalendarId = CalendarUtils.getWeatherCalendarId(this);
        if(weatherCalendarId == -1){
            CalendarUtils.addCalendar(this, this);
        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentToStartAddEventPopup = new Intent(MainActivity.this, AddEventPopup.class);
                ImageView mainActivityScrim = findViewById(R.id.imageview_main_activity_scrim);
                mainActivityScrim.setVisibility(View.VISIBLE);
                startActivity(intentToStartAddEventPopup);
            }
        });


        WeatherSyncUtils.initialize(this);


    }



    @Override
    protected void onResume() {
        ImageView mainActivityScrim = findViewById(R.id.imageview_main_activity_scrim);
        mainActivityScrim.setVisibility(View.INVISIBLE);
        super.onResume();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if(mData!=null) {
            Log.e(TAG, "Writing to Out State");
            outState.putParcelableArrayList(getString(R.string.forecast_bundle_key), mData);
        }
        super.onSaveInstanceState(outState);
    }


    //This is only needed if we are opening the app for the first time. OR if the user turned off permissions manually.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(!hasPermissions(this, PERMISSIONS)){
            Toast.makeText(this, "Calendar and Location Permissions are essential!", Toast.LENGTH_LONG).show();
            return;
        }

        Intent intentToSyncImmediately = new Intent(this, WeatherSyncIntentService.class);
        startService(intentToSyncImmediately);

        //If we have reached this point, we were granted permissions and need to continue with our loading.
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        //TODO after the weather data is loaded, initialize the loader.

        //If our forecast is null, load it from the database.
        if(mData == null) {
            LoaderManager.LoaderCallbacks<ArrayList<ForecastChunk>> callback = MainActivity.this;
            getSupportLoaderManager().initLoader(WEATHER_LOADER_ID, null, callback);
        }

        //While the forecast is being loaded from the database, Initialize the fragment.
        FragmentManager fragmentManager = getSupportFragmentManager();
        mCalendarFragment = new CalendarFragment();
        fragmentManager.beginTransaction()
                .add(R.id.calendar_fragment, mCalendarFragment)
                .commit();


        Long weatherCalendarId = CalendarUtils.getWeatherCalendarId(this);
        if(weatherCalendarId == -1){
            CalendarUtils.addCalendar(this, this);
        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentToStartAddEventPopup = new Intent(MainActivity.this, AddEventPopup.class);
                ImageView mainActivityScrim = findViewById(R.id.imageview_main_activity_scrim);
                mainActivityScrim.setVisibility(View.VISIBLE);
                startActivity(intentToStartAddEventPopup);
            }
        });


        WeatherSyncUtils.initialize(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_refresh){
            Intent intentToSyncImmediately = new Intent(this, WeatherSyncIntentService.class);
            startService(intentToSyncImmediately);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void getForecastChunksFromDb(){
        mDb = AppDatabase.getInstance(this);
        LiveData<List<WeatherEntry>> entries= mDb.weatherDao().loadForecast();
        entries.observe(this, new Observer<List<WeatherEntry>>() {
            @Override
            public void onChanged(@Nullable List<WeatherEntry> weatherEntries) {
                Log.e(TAG, "LiveData onChanged Fired");
                if(weatherEntries.size()>0) {
                    ArrayList<ForecastChunk> chunks = new ArrayList<>();
                    for (WeatherEntry entry : weatherEntries) {
                        chunks.add(new ForecastChunk(
                                entry.getDatelong(),
                                entry.getChunkmain(),
                                entry.getChunkweather(),
                                entry.getClouds(),
                                entry.getChunkwind(),
                                entry.getSyspod(),
                                entry.getDatetext()
                        ));
                    }
                    mData = chunks;
                    mCalendarFragment.setmForecastChunks(mData);
                }
            }
        });

    }

    @NonNull
    @Override
    public Loader<ArrayList<ForecastChunk>> onCreateLoader(int i, @Nullable Bundle bundle) {
        return new AsyncTaskLoader<ArrayList<ForecastChunk>>(this) {

            @Override
            protected void onStartLoading(){
                forceLoad();
            }

            @Nullable
            @Override
            public ArrayList<ForecastChunk> loadInBackground() {
                getForecastChunksFromDb();

                    //If The Database is Empty
                    if(mData == null || mData.size() == 0){
                        Log.e(TAG, "Database is currently empty");
                        LocationSyncTask.syncLocationAndWeather(getContext());
                }
                return  mData;
            }


        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<ForecastChunk>> loader, ArrayList<ForecastChunk> forecastChunks) {
        //Initialize the calendar Fragment
        Log.e(TAG, "onLoadFinished fired");

    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<ForecastChunk>> loader) {

    }


    //Help from https://www.sitepoint.com/requesting-runtime-permissions-in-android-m-and-n/
    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }



}

