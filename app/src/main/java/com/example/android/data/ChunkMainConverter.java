package com.example.android.data;

import android.arch.persistence.room.TypeConverter;
import android.util.Log;

import com.example.android.model.ChunkMain;
import com.example.android.utils.OpenWeatherJsonUtils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class ChunkMainConverter {
    private static final String TAG = ChunkMainConverter.class.getSimpleName();

    @TypeConverter
    public static ChunkMain toChunkMain(String chunkmainjsonstring){
        try{
            JSONObject object = new JSONObject(chunkmainjsonstring);
            return OpenWeatherJsonUtils.getChunkMainFromJsonObject(object);
        } catch(JSONException e){
            Log.e(TAG, e.toString());
            return null;
        }
    }

    @TypeConverter
    public static String toString(ChunkMain chunkMain){
        Gson gson = new Gson();
        String jsonString = gson.toJson(chunkMain);
        return jsonString;
    }
}
