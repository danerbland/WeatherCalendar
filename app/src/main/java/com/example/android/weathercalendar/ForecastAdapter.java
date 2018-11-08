package com.example.android.weathercalendar;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.model.ForecastChunk;
import com.example.android.utils.CalendarUtils;
import com.example.android.utils.OpenWeatherJsonUtils;

import java.util.ArrayList;
import java.util.Calendar;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder>{


    private static final String TAG = ForecastAdapter.class.getSimpleName();
    public final ArrayList<ForecastChunk> mForecastChunks;
    public final Context mContext;


    public ForecastAdapter (Context context, ArrayList<ForecastChunk> arraylist){
        mContext = context;
        mForecastChunks = arraylist;
    }

    @NonNull
    @Override
    public ForecastAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        int layoutId = R.layout.detail_forecast_item;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(layoutId, viewGroup, false);

        return new ForecastAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastAdapterViewHolder holder, int position) {
        if(mForecastChunks != null){
            ForecastChunk chunk = mForecastChunks.get(position);
            //TODO update this with correct time?
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(chunk.getmDate() * 1000L);

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
            if(sp.getString(mContext.getString(R.string.pref_units_key), mContext.getString(R.string.pref_units_imperial)).matches(mContext.getString(R.string.pref_units_imperial))){
                holder.mTempTextView.setText(OpenWeatherJsonUtils.convertToFahrenheit(chunk.getmChunkMain().getmMainTemp()) + "°");
            } else{
                holder.mTempTextView.setText(OpenWeatherJsonUtils.convertToCelcius(chunk.getmChunkMain().getmMainTemp()) + "°");

            }

            holder.mTimeTextView.setText(Integer.toString(calendar.get(Calendar.HOUR)) + ":00" + CalendarUtils.getAmOrPm(calendar));
            holder.mIconImageView.setImageResource(OpenWeatherJsonUtils.getDrawableIdFromWeatherCode(chunk.getmChunkWeather().getmId()));
            holder.mDescriptionTextView.setText(chunk.getmChunkWeather().getmDescription());
        }
    }

    @Override
    public int getItemCount() {
        if(mForecastChunks!=null){
            return mForecastChunks.size();
        } else {
            return 0;
        }
    }

    class ForecastAdapterViewHolder extends RecyclerView.ViewHolder {

        public final TextView mTimeTextView;
        public final ImageView mIconImageView;
        public final TextView mDescriptionTextView;
        public final TextView mTempTextView;

        public ForecastAdapterViewHolder(View itemView) {
            super(itemView);
            mTimeTextView = (TextView) itemView.findViewById(R.id.textview_detail_forecast_time);
            mIconImageView = (ImageView) itemView.findViewById(R.id.imageView_detail_forecast_icon);
            mDescriptionTextView = (TextView) itemView.findViewById(R.id.textview_detail_forecast_description);
            mTempTextView = (TextView) itemView.findViewById(R.id.textview_detail_forecast_temp);
        }

    }



}
