package com.example.android.weathercalendar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventAdapterViewHolder>{

    private static final String TAG = EventAdapter.class.getSimpleName() ;
    private static final long DAY_IN_MILLISECONDS = 86400000;

    private Context mContext;
    private ArrayList<ForecastChunk> mForecastChunks;


    private Cursor mCursor;
    private EventOnClickHandler mEventOnClickHandler;

    public interface EventOnClickHandler {
        void onClick();
    }

    //Constructor
    public EventAdapter(Context context, ArrayList<ForecastChunk> arrayList, Cursor cursor, EventOnClickHandler handler){
        mContext = context;
        mForecastChunks = arrayList;
        mCursor = cursor;
        mEventOnClickHandler = handler;
    }


    @NonNull
    @Override
    public EventAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        int layoutId = R.layout.event_list_item;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(layoutId, viewGroup, false);

        return new EventAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventAdapterViewHolder holder, int position) {
        if(mCursor != null){

            Calendar calendar = Calendar.getInstance();
            mCursor.moveToPosition(position);
            calendar.setTimeInMillis(mCursor.getLong(mCursor.getColumnIndex(CalendarContract.Events.DTSTART)));
            holder.mEventTime.setText(CalendarUtils.getTimeString(calendar));
            holder.mDescriptionTextView.setText(mCursor.getString(mCursor.getColumnIndex(CalendarContract.Events.TITLE)));

            //If we have forecast information, find the chunk with the closest time to the event's start.
            if(mForecastChunks != null && mForecastChunks.size()!=0){

                long shortestTime = DAY_IN_MILLISECONDS;
                int closestIndex = -1;

                //Find the index of the closes forecast chunk to our event
                for(int j = 0; j < mForecastChunks.size(); j ++){
                    if(Math.abs(calendar.getTimeInMillis() - (mForecastChunks.get(j).getmDate()*1000L)) <=  shortestTime){
                        shortestTime = calendar.getTimeInMillis() - (mForecastChunks.get(j).getmDate()*1000L);
                        closestIndex = j;
                    }
                }

                if(closestIndex!=-1){
                    ForecastChunk chunk = mForecastChunks.get(closestIndex);
                    holder.mIconImageView.setImageResource(OpenWeatherJsonUtils.getDrawableIdFromWeatherCode(chunk.getmChunkWeather().getmId()));
                    holder.mWeatherDescriptionTextView.setText(chunk.getmChunkWeather().getmDescription());

                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
                    if(sp.getString(mContext.getString(R.string.pref_units_key), mContext.getString(R.string.pref_units_imperial)).matches(mContext.getString(R.string.pref_units_imperial))){
                        holder.mTempTextView.setText(OpenWeatherJsonUtils.convertToFahrenheit(chunk.getmChunkMain().getmMainTemp()) + "°");
                    } else{
                        holder.mTempTextView.setText(OpenWeatherJsonUtils.convertToCelcius(chunk.getmChunkMain().getmMainTemp()) + "°");

                    }
                }

            }  else {
                holder.mIconImageView.setVisibility(View.GONE);
                holder.mWeatherDescriptionTextView.setVisibility(View.GONE);
                holder.mTempTextView.setVisibility(View.GONE);
            }

        }
    }

    @Override
    public int getItemCount() {
        if(mCursor != null){
            return mCursor.getCount();
        }else {
            Log.d(TAG, "mCursor = null");
            return 0;
        }
    }


    class EventAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public final TextView mDescriptionTextView;
        public final TextView mEventTime;
        public final ImageView mIconImageView;
        public final TextView mWeatherDescriptionTextView;
        public final TextView mTempTextView;

        public EventAdapterViewHolder(View itemView) {
            super(itemView);
            mDescriptionTextView = (TextView) itemView.findViewById(R.id.textview_event_description);
            mEventTime = (TextView) itemView.findViewById(R.id.textView_event_time);
            mIconImageView = (ImageView) itemView.findViewById(R.id.imageview_event_weather_icon);
            mWeatherDescriptionTextView = (TextView) itemView.findViewById(R.id.textview_event_weather_description);
            mTempTextView = (TextView) itemView.findViewById(R.id.textview_event_temperature);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {


            int position = getAdapterPosition();
            mCursor.moveToPosition(position);

            Intent intentToStartAddEventScreen = new Intent(mContext,AddEventPopup.class);
            intentToStartAddEventScreen.putExtra(mContext.getString(R.string.event_extras_id_key), mCursor.getString(mCursor.getColumnIndex(CalendarContract.Events._ID)));
            intentToStartAddEventScreen.putExtra(mContext.getString(R.string.event_extras_Start_Date_key), mCursor.getLong(mCursor.getColumnIndex(CalendarContract.Events.DTSTART)));
            intentToStartAddEventScreen.putExtra(mContext.getString(R.string.event_extras_End_Date_key), mCursor.getLong(mCursor.getColumnIndex(CalendarContract.Events.DTEND)));
            intentToStartAddEventScreen.putExtra(mContext.getString(R.string.event_extras_Title_key), mCursor.getString(mCursor.getColumnIndex(CalendarContract.Events.TITLE)));

            mEventOnClickHandler.onClick();

            mContext.startActivity(intentToStartAddEventScreen);

        }
    }

    public void setmCursor(Cursor mCursor) {
        this.mCursor = mCursor;
    }
}
