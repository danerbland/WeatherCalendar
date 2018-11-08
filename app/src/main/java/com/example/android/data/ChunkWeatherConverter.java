package com.example.android.data;

import android.arch.persistence.room.TypeConverter;
import android.util.Log;


import com.example.android.model.ChunkWeather;
import com.example.android.utils.OpenWeatherJsonUtils;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

public class ChunkWeatherConverter {
    private static final String TAG = ChunkWeatherConverter.class.getSimpleName();

    @TypeConverter
    public static ChunkWeather toChunkWeather(String chunkweatherjsonString){
        try{
            JSONArray array = new JSONArray(chunkweatherjsonString);
            return OpenWeatherJsonUtils.getChunkWeatherFromWeatherEntry(array);
        } catch(JSONException e){
            Log.e(TAG, e.toString());
            return null;
        }
    }

    @TypeConverter
    public static String toString(ChunkWeather chunkweather){
        Gson gson = new Gson();
        String jsonString = "[" + gson.toJson(chunkweather) + "]";
        return jsonString;
    }

}
