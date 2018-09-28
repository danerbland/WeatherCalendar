package com.example.android.utils;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.util.Log;

import com.example.android.weathercalendar.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();


    private static final String BASE_OPENWEATHERMAPS_URL =
            "https://api.openweathermap.org/data/2.5/forecast";

    private static final String EXAMPLE_ZIP_CODE = "10023";

    private static final String DATA_TYPE_PARAM="mode";
    private static final String CITY_NAME_PARAM="q";
    private static final String CITY_ID_PARAM = "id";
    private static final String LATITUDE_PARAM = "lat";
    private static final String LONGITUDE_PARAM = "lon";
    private static final String ZIP_PARAM = "zip";
    private static final String API_KEY_PARAM="APPID";

    private static final String DATA_TYPE = "json";

    public static URL buildWeatherQueryURL (int ZipCode, String CountryCode, Context context){

        String ZipParam = Integer.toString(ZipCode) + ","+ CountryCode;

        Uri weatherQueryUri = Uri.parse(BASE_OPENWEATHERMAPS_URL).buildUpon()
                .appendQueryParameter(ZIP_PARAM, ZipParam)
                .appendQueryParameter(DATA_TYPE_PARAM, DATA_TYPE)
                .appendQueryParameter(API_KEY_PARAM, context.getString(R.string.OpenWeatherMaps_API_Key_Secret)).build();

        Log.e(TAG, weatherQueryUri.toString());

        try {
            String s = weatherQueryUri.toString();
            URL weatherQueryURL = new URL(s);
            return weatherQueryURL;
        } catch (MalformedURLException e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
            return null;
        }
    }

    public static URL buildWeatherQueryURL (double latitude, double longitude, Context context){

        Uri weatherQueryUri = Uri.parse(BASE_OPENWEATHERMAPS_URL).buildUpon()
                .appendQueryParameter(LATITUDE_PARAM, Double.toString(latitude))
                .appendQueryParameter(LONGITUDE_PARAM, Double.toString(longitude))
                .appendQueryParameter(API_KEY_PARAM, context.getString(R.string.OpenWeatherMaps_API_Key_Secret)).build();

        Log.e(TAG, weatherQueryUri.toString());

        try {
            String s = weatherQueryUri.toString();
            URL weatherQueryURL = new URL(s);
            return weatherQueryURL;
        } catch (MalformedURLException e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
            return null;
        }
    }



    public static String getResponseFromHttpUrl(URL url) throws IOException {

        //Create a HttpURLConnection object to handle the streams.
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream inStream = urlConnection.getInputStream();

            Scanner scanner = new Scanner(inStream);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            String response = null;
            if (hasInput) {
                response = scanner.next();
            }
            scanner.close();
            return response;
        } finally {
            urlConnection.disconnect();
        }
    }


}
