package com.example.android.utils;


import android.util.Log;

import com.example.android.model.ChunkMain;
import com.example.android.model.ChunkWeather;
import com.example.android.model.ChunkWind;
import com.example.android.model.ForecastChunk;
import com.example.android.weathercalendar.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

//A UTILITY FOR EXTRACTING JAVA OBJECTS FROM JSON.
public class OpenWeatherJsonUtils {

    private static final String TAG = OpenWeatherJsonUtils.class.getSimpleName();

    //JSON KEYS ONLY TO BE USED IN THIS UTILITY.
    private static final String JSON_CITY_KEY = "city";
    //WITHIN CITY
    private static final String JSON_ID_KEY = "id";
    private static final String JSON_NAME_KEY = "name";
    //END CITY
    private static final String JSON_COORD_KEY = "coord";
    //WITHIN COORD
    private static final String JSON_LON_KEY = "lon";
    private static final String JSON_LAT_KEY = "lat";
    //END COORD
    private static final String JSON_COUNTRY_KEY = "country";
    private static final String JSON_CODE_KEY = "cod";
    private static final String JSON_MESSAGE_KEY = "message";
    private static final String JSON_COUNT_KEY = "cnt";
    private static final String JSON_LIST_KEY = "list";
            //WITHIN LIST
        private static final String JSON_DATE_KEY = "dt";
        private static final String JSON_MAIN_KEY = "main";
         //WITHIN MAIN
            private static final String JSON_TEMP_KEY = "temp";
            private static final String JSON_MAX_TEMP_KEY = "temp_min";
            private static final String JSON_MIN_TEMP_KEY = "temp_max";
            private static final String JSON_PRESSURE_KEY = "pressure";
            private static final String JSON_SEA_LEVEL_KEY = "sea_level";
            private static final String JSON_GROUND_LEVEL_KEY = "grnd_level";
            private static final String JSON_HUMIDITY_KEY = "humidity";
            private static final String JSON_TEMP_KF_KEY = "temp_kf";
            //END MAIN
        private static final String JSON_WEATHER_KEY = "weather";
            //WITHIN WEATHER
            private static final String JSON_WEATHER_ID_KEY = "id";
            private static final String JSON_WEATHER_MAIN_KEY = "main";
            private static final String JSON_WEATHER_DESCRIPTION_KEY = "description";
            private static final String JSON_WEATHER_ICON_KEY = "icon";
            //END WEATHER
        private static final String JSON_CLOUDS_KEY = "clouds";
            //WITHIN CLOUDS
            private static final String JSON_CLOUDS_ALL_KEY = "all";
            //END CLOUDS
        private static final String JSON_WIND_KEY = "wind";
            //WITHIN WIND
           private static final String JSON_WIND_SPEED_KEY = "speed";
           private static final String JSON_WIND_DIRECTION_KEY = "deg";
            //END WIND
        private static final String JSON_SYS_KEY = "sys";
            //WITHIN SYS
            private static final String JSON_SYS_POD_KEY = "pod";
            //END SYS
        private static final String JSON_DATE_TEXT_KEY = "dt_txt";
    //END LIST



    //Return an array list of ForecastChunks to be stored in the database.
    public static ArrayList<ForecastChunk> getForecastChunksFromJSON (String json){

        try {
            //convert JSON string to JSON object and identify the "list" portion, which contains the forecast broken into 3-hour chunks.
            JSONObject ForecastObject = new JSONObject(json);
            JSONArray JSONChunkArray = ForecastObject.getJSONArray(JSON_LIST_KEY);

            if(JSONChunkArray == null || JSONChunkArray.length()==0){
                return null;
            }

            ArrayList<ForecastChunk> forecastChunks = new ArrayList<>();

            for(int i = 0; i<JSONChunkArray.length(); i++){
                JSONObject JSONChunkObject = JSONChunkArray.getJSONObject(i);
                ForecastChunk forecastChunk = new ForecastChunk(
                        JSONChunkObject.getLong(JSON_DATE_KEY),
                        getChunkMainFromJsonObject(JSONChunkObject.getJSONObject(JSON_MAIN_KEY)),
                        getChunkWeatherFromJsonArray(JSONChunkObject.getJSONArray(JSON_WEATHER_KEY)),
                        JSONChunkObject.getJSONObject(JSON_CLOUDS_KEY).getInt(JSON_CLOUDS_ALL_KEY),
                        getChunkWindFromJsonObject(JSONChunkObject.getJSONObject(JSON_WIND_KEY)),
                        JSONChunkObject.getJSONObject(JSON_SYS_KEY).getString(JSON_SYS_POD_KEY),
                        JSONChunkObject.getString(JSON_DATE_TEXT_KEY)
                );
                forecastChunks.add(forecastChunk);
            }

            return forecastChunks;
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
            return null;
        }

    }

    public static ChunkMain getChunkMainFromJsonObject(JSONObject object) throws JSONException{
        ChunkMain chunkMain = new ChunkMain(
                object.getDouble(JSON_TEMP_KEY),
                object.getDouble(JSON_MIN_TEMP_KEY),
                object.getDouble(JSON_MAX_TEMP_KEY),
                object.getDouble(JSON_PRESSURE_KEY),
                object.getDouble(JSON_SEA_LEVEL_KEY),
                object.getDouble(JSON_GROUND_LEVEL_KEY),
                object.getDouble(JSON_HUMIDITY_KEY),
                object.getDouble(JSON_TEMP_KF_KEY)
        );
        return chunkMain;
    }

    public static ChunkWeather getChunkWeatherFromJsonArray (JSONArray array) throws JSONException {
        JSONObject chunkWeatherObject = array.getJSONObject(0);
        ChunkWeather chunkWeather = new ChunkWeather(
                chunkWeatherObject.getInt(JSON_WEATHER_ID_KEY),
                chunkWeatherObject.getString(JSON_WEATHER_MAIN_KEY),
                chunkWeatherObject.getString(JSON_WEATHER_DESCRIPTION_KEY),
                chunkWeatherObject.getString(JSON_WEATHER_ICON_KEY)
        );
        return chunkWeather;
    }

    public static ChunkWind getChunkWindFromJsonObject (JSONObject object) throws JSONException {
        ChunkWind chunkWind = new ChunkWind(
                object.getDouble(JSON_WIND_SPEED_KEY),
                object.getDouble(JSON_WIND_DIRECTION_KEY)
        );
        return chunkWind;
    }

    public static ChunkWind getChunkWindFromWeatherEntry (JSONObject object) throws JSONException {
        ChunkWind chunkWind = new ChunkWind(
                object.getDouble("mSpeed"),
                object.getDouble("mDirection"));
        return chunkWind;
    }

    public static ChunkWeather getChunkWeatherFromWeatherEntry (JSONArray array) throws JSONException {
        JSONObject chunkWeatherObject = array.getJSONObject(0);
        ChunkWeather chunkWeather = new ChunkWeather(
                chunkWeatherObject.getInt("mId"),
                chunkWeatherObject.getString("mMain"),
                chunkWeatherObject.getString("mDescription"),
                chunkWeatherObject.getString("mIcon")
        );
        return chunkWeather;
    }

    public static ChunkMain getChunkMainFromWeatherEntry(JSONObject object) throws JSONException{
        ChunkMain chunkMain = new ChunkMain(
                object.getDouble("mMainTemp"),
                object.getDouble("mMinTemp"),
                object.getDouble("mMaxTemp"),
                object.getDouble("mPressure"),
                object.getDouble("mSeaLevel"),
                object.getDouble("mGroundLevel"),
                object.getDouble("mHumidity"),
                object.getDouble("mTempKF")
        );
        return chunkMain;
    }

    public static int getDrawableIdFromWeatherCode(int weatherCode){
        if(weatherCode >= 800) {
            switch (weatherCode) {
                case 800:
                    return R.drawable.art_clear;
                case 801:
                    return R.drawable.art_light_clouds;
                case 802:
                    return R.drawable.art_light_clouds;
                case 803:
                    return R.drawable.art_clouds;
                case 804:
                    return R.drawable.art_clouds;
                default:
                    return -1;
            }
        }

        switch (weatherCode/100){
            case 2:
                return R.drawable.art_storm;
            case 3:
                return R.drawable.art_light_rain;
            case 5:
                return R.drawable.art_rain;
            case 6:
                return R.drawable.art_snow;
            default:
                return -1;
        }
    }

    public static int convertToFahrenheit(Double temp){
        return (int) Math.round(((temp - Double.valueOf(273.15)) * Double.valueOf(1.8)) + Double.valueOf(32));
    }

    public static int convertToCelcius(Double temp){
        return (int) Math.round(temp - 273.15);
    }


}
