package com.example.android.weathercalendar;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.data.AppDatabase;
import com.example.android.data.WeatherEntry;
import com.example.android.model.ForecastChunk;
import com.example.android.weathercalendar.sync.WeatherSyncIntentService;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DayDetailActivity extends AppCompatActivity
        implements DayDetailFragment.OnFragmentInteractionListener{

    private String TAG = DayDetailActivity.class.getSimpleName();

    private DayDetailFragment mDayDetailFragment;
    private SharedPreferences mSharedPreferences;

    private TextView mDateTextView;
    private TextView mLocationTextView;

    private int mYear;
    private int mMonth;
    private int mDay;
    private ArrayList<ForecastChunk> mForecastChunks;

    private Calendar mCalendar;
    private FragmentManager mFragmentManager;
    private AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_detail);

        if(getIntent().getExtras() != null){
            Bundle b = getIntent().getExtras();
            mYear = b.getInt(getString(R.string.year_bundle_key));
            mMonth = b.getInt(getString(R.string.month_bundle_key));
            mDay = b.getInt(getString(R.string.day_bundle_key));
            mForecastChunks = getIntent().getParcelableArrayListExtra(getString(R.string.forecast_bundle_key));
        }

        //TODO set title to city
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Initialize FAB
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.day_detail_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentToStartAddEventPopup = new Intent(DayDetailActivity.this, AddEventPopup.class);

                //Add the day to the Event popup
                Calendar calendar = Calendar.getInstance();
                calendar.set(mYear, mMonth, mDay);

                Log.e(TAG, Long.toString(calendar.getTimeInMillis()));
                intentToStartAddEventPopup.putExtra(getString(R.string.event_extras_Start_Date_key), calendar.getTimeInMillis());
                intentToStartAddEventPopup.putExtra(getString(R.string.event_extras_End_Date_key), calendar.getTimeInMillis());
                intentToStartAddEventPopup.putExtra(getString(R.string.event_extras_id_key), -1);

                ImageView dayDetailActivityScrim = findViewById(R.id.imageview_day_detail_activity_scrim);
                dayDetailActivityScrim.setVisibility(View.VISIBLE);
                startActivity(intentToStartAddEventPopup);
            }
        });




        mCalendar = Calendar.getInstance();
        mCalendar.set(mYear, mMonth, mDay, 0, 0);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String LocationName = "";
        if(mSharedPreferences!=null && mSharedPreferences.getString(getString(R.string.pref_location_edittext_key), null) != null){
            LocationName = mSharedPreferences.getString(getString(R.string.pref_location_edittext_key), null);
        }
        String dayLongName = mCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
        mDateTextView = (TextView) findViewById(R.id.textview_day_detail_date);
        mDateTextView.setText(dayLongName + ", " + (mMonth + 1) + "/" + mDay + "/" + mYear);
        mLocationTextView = (TextView) findViewById(R.id.textview_day_detail_location);
        mLocationTextView.setText(LocationName);

        //If the fragment does not exist, initialize it.
        mFragmentManager = getSupportFragmentManager();
        if(savedInstanceState == null) {
            //initialize forecast fragment
            mFragmentManager = getSupportFragmentManager();
            mDayDetailFragment = DayDetailFragment.newInstance(mCalendar, mForecastChunks);

            mFragmentManager.beginTransaction()
                    .add(R.id.day_detail_fragment, mDayDetailFragment)
                    .commit();

        } else {
            mDayDetailFragment = (DayDetailFragment) mFragmentManager.getFragment(savedInstanceState, getString(R.string.fragment_bundle_key));
        }


        mDb = AppDatabase.getInstance(this);
        LiveData<List<WeatherEntry>> entries= mDb.weatherDao().loadForecast();
        entries.observe(this, new Observer<List<WeatherEntry>>() {
            @Override
            public void onChanged(@Nullable List<WeatherEntry> weatherEntries) {
                Log.e(TAG, "Observer onChanged Fired");
                ArrayList<ForecastChunk> chunks = new ArrayList<>();
                for(WeatherEntry entry: weatherEntries){
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
                mForecastChunks = chunks;
                mDayDetailFragment.setmForecastChunks(mForecastChunks);
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mFragmentManager.putFragment(outState, getString(R.string.fragment_bundle_key), mDayDetailFragment);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onResume() {

        //Hide the scrim
        ImageView dayDetailActivityScrim = findViewById(R.id.imageview_day_detail_activity_scrim);
        dayDetailActivityScrim.setVisibility(View.INVISIBLE);

        //Update the header with possible new location information
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String LocationName = "";
        if(mSharedPreferences!=null && mSharedPreferences.getString(getString(R.string.pref_location_edittext_key), null) != null){
            LocationName = mSharedPreferences.getString(getString(R.string.pref_location_edittext_key), null);
        }
        String dayLongName = mCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
        mDateTextView = (TextView) findViewById(R.id.textview_day_detail_date);
        mDateTextView.setText(dayLongName + ", " + (mMonth + 1) + "/" + mDay + "/" + mYear);
        mLocationTextView = (TextView) findViewById(R.id.textview_day_detail_location);
        mLocationTextView.setText(LocationName);


        super.onResume();
    }

    @Override
    public void onEventClicked() {
        ImageView dayDetailActivityScrim = findViewById(R.id.imageview_day_detail_activity_scrim);
        dayDetailActivityScrim.setVisibility(View.VISIBLE);

        return;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_refresh){
            Intent intentToSyncImmediately = new Intent(this, WeatherSyncIntentService.class);
            this.startService(intentToSyncImmediately);
            return true;
        }
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
