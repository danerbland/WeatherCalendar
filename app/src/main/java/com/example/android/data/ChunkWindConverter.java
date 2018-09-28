package com.example.android.data;

import android.arch.persistence.room.TypeConverter;
import android.util.Log;

import com.example.android.model.ChunkMain;
import com.example.android.model.ChunkWeather;
import com.example.android.model.ChunkWind;
import com.example.android.utils.OpenWeatherJsonUtils;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ChunkWindConverter {
    private static final String TAG = ChunkWindConverter.class.getSimpleName();

    @TypeConverter
    public static ChunkWind toChunkWind(String chunkwindjsonstring){
        try{
            JSONObject object = new JSONObject(chunkwindjsonstring);
            return OpenWeatherJsonUtils.getChunkWindFromJsonObject(object);
        } catch(JSONException e){
            Log.e(TAG, e.toString());
            return null;
        }
    }

    @TypeConverter
    public static String toString(ChunkWind chunkwind){
        Gson gson = new Gson();
        String jsonString = gson.toJson(chunkwind);
        Log.e(TAG, "toString: " + jsonString);
        return jsonString;
    }


}
