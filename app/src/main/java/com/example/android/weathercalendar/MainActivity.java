package com.example.android.weathercalendar;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;

import com.example.android.model.ForecastChunk;
import com.example.android.utils.GooglePlayServicesUtils;
import com.example.android.utils.NetworkUtils;
import com.example.android.utils.OpenWeatherJsonUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int MY_PERMISSIONS_REQUEST_COARSE_LOCATION = 333 ;
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 444;
    private static double mLatitude;
    private static double mLongitude;
    private static GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if( ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_COARSE_LOCATION);
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_FINE_LOCATION);
        } else {
            Log.e(TAG, "coarse Location Permission Granted");
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastKnownLocation();

        mGoogleApiClient = GooglePlayServicesUtils.buildGoogleApiClient(this, this, this);
        mGoogleApiClient.connect();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }





    //GOOGLE API CONNECTION CALLBACKS AND LISTERNER
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLocation != null) {
            mLatitude = mLocation.getLatitude();
            mLongitude = mLocation.getLongitude();
        } else {
            Log.e(TAG, "API Callback Location = null");
        }
        Log.e(TAG, Double.toString(mLatitude));
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void getLastKnownLocation() {
        Log.e(TAG, "started getlastknownlocation");
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        mLocation = location;
                        if (location != null) {
                            mLatitude = mLocation.getLatitude();
                            mLongitude = mLocation.getLongitude();
                            Log.e(TAG, Double.toString(mLatitude));
                        } else {
                            Log.e(TAG, "FusedLocationClient Location = null");
                        }
                    }
                }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                Log.e(TAG, "FusedLocationClient getLastLocation CANCELED");
            }
        });
        Log.e(TAG, "ended getlastknownlocation");
    }
}
