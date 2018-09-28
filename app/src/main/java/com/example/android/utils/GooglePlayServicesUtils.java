package com.example.android.utils;

import android.content.Context;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class GooglePlayServicesUtils {

    public static GoogleApiClient buildGoogleApiClient(Context context, GoogleApiClient.ConnectionCallbacks callbacks, GoogleApiClient.OnConnectionFailedListener listener){
        return new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(callbacks)
                .addOnConnectionFailedListener(listener)
                .addApi(LocationServices.API)
                .build();
    }

}


