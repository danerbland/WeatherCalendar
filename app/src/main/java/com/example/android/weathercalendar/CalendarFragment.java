package com.example.android.weathercalendar;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.util.Log;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.example.android.model.ForecastChunk;
import com.example.android.utils.OpenWeatherJsonUtils;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;



public class CalendarFragment extends Fragment {

    private static final String TAG = CalendarFragment.class.getSimpleName();

    private int mYear = -1;
    private int mMonth = -1;

    private static final Calendar mCalendar = Calendar.getInstance();
    private CalendarView mCalendarView;
    private ArrayList<ForecastChunk> mForecastChunks;

    public CalendarFragment() {
        // Required empty public constructor
    }

    public void setmForecastChunks(ArrayList<ForecastChunk> chunks){
        mForecastChunks = chunks;
        removeWeatherIconsFromCalendar(mCalendarView);
        addWeatherIconsToCalendar(mCalendarView);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static CalendarFragment newInstance(int year, int month) {
        CalendarFragment fragment = new CalendarFragment();
        fragment.setmMonth(month);
        fragment.setmYear(year);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Recover our state from rotation
        if (savedInstanceState != null) {
            mYear = savedInstanceState.getInt(getActivity().getString(R.string.year_bundle_key));
            mMonth = savedInstanceState.getInt(getActivity().getString(R.string.month_bundle_key));
            mForecastChunks = savedInstanceState.getParcelableArrayList(getString(R.string.forecast_bundle_key));
        }

        //if mYear && mMonth are set, then we should recover our calendarview to the Year and Month
        if(mYear!=-1 && mMonth!=-1){
            mCalendar.set(mYear, mMonth, 1);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootview = inflater.inflate(R.layout.fragment_calendar, container, false);
        mCalendarView = rootview.findViewById(R.id.month_calendarview);
        initializemCalendar(mCalendarView);

        //If the saved instance state is not null, add the weather icons back to the calendar.  The rest of the data is safe.
        if(savedInstanceState != null){
            addWeatherIconsToCalendar(mCalendarView);
        }
        return rootview;
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Calendar c = mCalendarView.getCurrentPageDate();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        outState.putInt(getActivity().getString(R.string.year_bundle_key), year);
        outState.putInt(getActivity().getString(R.string.month_bundle_key), month);
        outState.putParcelableArrayList(getString(R.string.forecast_bundle_key), mForecastChunks);
        super.onSaveInstanceState(outState);
    }


    public int getmYear() {
        return mYear;
    }

    public void setmYear(int mYear) {
        this.mYear = mYear;
    }

    public int getmMonth() {
        return mMonth;
    }

    public void setmMonth(int mMonth) {
        this.mMonth = mMonth;
    }

    private void initializemCalendar(CalendarView view){
        Log.d(TAG, "InitializemCalendar fired");

        try {
            view.setDate(mCalendar);
        } catch (OutOfDateRangeException e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }
        view.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                Intent intentToStartDayDetailActivity = new Intent(getContext(), DayDetailActivity.class);
                intentToStartDayDetailActivity.putExtra(getActivity().getString(R.string.year_bundle_key), eventDay.getCalendar().get(Calendar.YEAR));
                intentToStartDayDetailActivity.putExtra(getActivity().getString(R.string.month_bundle_key), eventDay.getCalendar().get(Calendar.MONTH));
                intentToStartDayDetailActivity.putExtra(getActivity().getString(R.string.day_bundle_key), eventDay.getCalendar().get(Calendar.DAY_OF_MONTH));
                intentToStartDayDetailActivity.putParcelableArrayListExtra(getActivity().getString(R.string.forecast_bundle_key), mForecastChunks);
                startActivity(intentToStartDayDetailActivity);
            }
        });
    }

    private void addWeatherIconsToCalendar(CalendarView view){
        Log.d(TAG, "addWeatherIconsToCalendar fired");
        if(mForecastChunks != null && mForecastChunks.size()!=0){
            List<EventDay> events = new ArrayList<>();
               for(ForecastChunk chunk:mForecastChunks){
                   Calendar calendar = Calendar.getInstance();
                   calendar.setTimeInMillis(chunk.getmDate() * 1000L);
                   if(calendar.get(Calendar.HOUR_OF_DAY) <= 17 && calendar.get(Calendar.HOUR_OF_DAY) >14){
                       events.add(new EventDay(calendar, OpenWeatherJsonUtils.getDrawableIdFromWeatherCode(chunk.getmChunkWeather().getmId())));
                   }

            }
            Log.d(TAG, "Setting Events");
            view.setEvents(events);
        }
    }

    private void removeWeatherIconsFromCalendar(CalendarView view){
        view.setEvents(null);
    }

}
