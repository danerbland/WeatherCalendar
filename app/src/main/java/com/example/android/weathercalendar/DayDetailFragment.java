package com.example.android.weathercalendar;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.android.model.ForecastChunk;
import com.example.android.utils.CalendarUtils;

import java.util.ArrayList;
import java.util.Calendar;



/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DayDetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DayDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DayDetailFragment extends Fragment implements EventAdapter.EventOnClickHandler {

    private static final String TAG = DayDetailFragment.class.getSimpleName();

    private static final long DAY_IN_MILLISECONDS = 86400000;

    private Cursor mEventCursor;
    private Calendar mDayCalendar;
    private ArrayList<ForecastChunk> mForecastChunks;
    private ForecastAdapter mForecastAdapter;
    private EventAdapter mEventAdapter;
    private OnFragmentInteractionListener mListener;
    private RecyclerView mForecastRecyclerView;
    private RecyclerView mEventsRecyclerView;

    private TextView mNoForecastTextView;
    private TextView mNoEventsTextView;

    private boolean mHasForecast = false;
    private boolean mForecastIsHidden = false;
    private boolean mHasEvents = false;
    private boolean mEventsAreHidden = false;


    public DayDetailFragment() {
        // Required empty public constructor
    }


    // Initializes the Fragment with a Calendar and the Forecast
    public static DayDetailFragment newInstance(Calendar dayCalendar, ArrayList<ForecastChunk> forecastChunks) {
        DayDetailFragment fragment = new DayDetailFragment();
        fragment.setmDayCalendar(dayCalendar);
        fragment.setmForecastChunks(forecastChunks);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if(savedInstanceState != null){
            mDayCalendar = (Calendar) savedInstanceState.getSerializable(getString(R.string.calendar_bundle_key));
            mForecastChunks = savedInstanceState.getParcelableArrayList(getString(R.string.forecast_bundle_key));
        }


            ArrayList<ForecastChunk> dayChunks = getDayChunks();
            if (dayChunks.size() != 0) {
                mForecastAdapter = new ForecastAdapter(getContext(), dayChunks);
            } else {
                mForecastAdapter = null;
            }

        //Initialize the Event Adapter
            //get the day's from the CalendarProvider
        mEventCursor = CalendarUtils.queryEventsFromEventTable(getContext(), mDayCalendar.getTimeInMillis(), mDayCalendar.getTimeInMillis() + DAY_IN_MILLISECONDS);
        if(mEventCursor != null && mEventCursor.getCount() != 0) {
            mEventAdapter = new EventAdapter(getContext(), dayChunks, mEventCursor, this);
        } else {
            mEventAdapter = null;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

            Log.d(TAG, "inflating Rootview");
            // Inflate the layout for this fragment
            final View rootView = inflater.inflate(R.layout.fragment_day_detail, container, false);

            mForecastRecyclerView = rootView.findViewById(R.id.recyclerview_day_detail_forecast);
            mEventsRecyclerView = rootView.findViewById(R.id.recyclerview_day_detail_events);
            mNoForecastTextView = rootView.findViewById(R.id.textview_no_forecast_message);
            mNoEventsTextView = rootView.findViewById(R.id.textview_no_events_message);


            //If mForecastAdapter is not null, then we have forecast information and should show the recyclerview.
            if (mForecastAdapter != null) {
                LinearLayoutManager forecastLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                mForecastRecyclerView.setLayoutManager(forecastLayoutManager);
                mForecastRecyclerView.setAdapter(mForecastAdapter);
                mHasForecast = true;
            } else {
                mForecastRecyclerView.setVisibility(View.GONE);
                mNoForecastTextView.setVisibility(View.VISIBLE);
            }

            //if mEventAdapter is not null, then we have events and should show the Recyclerview
            if (mEventAdapter != null) {
                LinearLayoutManager eventLayoutManger = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                mEventsRecyclerView.setLayoutManager(eventLayoutManger);
                mEventsRecyclerView.setAdapter(mEventAdapter);
                mHasEvents = true;
            } else {
                mEventsRecyclerView.setVisibility(View.GONE);
                mNoEventsTextView.setVisibility(View.VISIBLE);
            }

            configureLabelsOnClickListeners(rootView);

            return rootView;


    }

    @Override
    public void onResume() {

        Log.d(TAG, "onResume Fired!");
        //on Resume, we may need to update our Event RecyclerView with new events.
        ArrayList<ForecastChunk> dayChunks = getDayChunks();
        mEventCursor = CalendarUtils.queryEventsFromEventTable(getContext(), mDayCalendar.getTimeInMillis(), mDayCalendar.getTimeInMillis() + DAY_IN_MILLISECONDS);

        //Updating UI. If we have events
        if(mEventCursor != null && mEventCursor.getCount() != 0) {

            //If there were no events before this.
            if (mEventAdapter == null) {
                mEventAdapter = new EventAdapter(getContext(), dayChunks, mEventCursor, this);
                LinearLayoutManager eventLayoutManger = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                mEventsRecyclerView.setLayoutManager(eventLayoutManger);
                mEventsRecyclerView.setAdapter(mEventAdapter);
                mNoEventsTextView.setVisibility(View.GONE);
                mEventsRecyclerView.setVisibility(View.VISIBLE);

                mHasEvents = true;
            }
            //If there were events before this.
            else{
                mEventAdapter.setmCursor(mEventCursor);
                mEventAdapter.notifyDataSetChanged();
            }
        }//End if we have events

        //If we don't have events
        else{
            mEventsRecyclerView.setVisibility(View.GONE);
            mNoEventsTextView.setVisibility(View.VISIBLE);
        }

        super.onResume();
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(getString(R.string.calendar_bundle_key), mDayCalendar);
        outState.putParcelableArrayList(getString(R.string.forecast_bundle_key), mForecastChunks);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setmDayCalendar(Calendar mDayCalendar) {
        this.mDayCalendar = mDayCalendar;
    }

    public void setmForecastChunks(ArrayList<ForecastChunk> forecastChunks) {
        this.mForecastChunks = forecastChunks;
        if(mForecastAdapter!=null && mForecastChunks != null){
            mForecastAdapter = new ForecastAdapter(getContext(), getDayChunks());
            mForecastRecyclerView.swapAdapter(mForecastAdapter, true);
        }
    }

    @Override
    public void onClick() {
        mListener.onEventClicked();
    }

    public interface OnFragmentInteractionListener {
        void onEventClicked();
    }


    public ArrayList<ForecastChunk> getDayChunks (){
        int day = mDayCalendar.get(Calendar.DAY_OF_MONTH);
        int month = mDayCalendar.get(Calendar.MONTH);
        int year = mDayCalendar.get(Calendar.YEAR);
        ArrayList<ForecastChunk> dayChunks = new ArrayList<>();

        //Initialize the Forecast Adapter
        //Select the Forecast Chunks that belong to the Day that we are looking at.
        //Add those chunks to a new array list, and use that list to initialize the recyclerview's adapter.
        if(mForecastChunks == null){
            return dayChunks;
        }

        for(int i = 0; i < mForecastChunks.size(); i++){
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(mForecastChunks.get(i).getmDate() * 1000L);
            if(c.get(Calendar.DAY_OF_MONTH) == day && c.get(Calendar.HOUR_OF_DAY) >= 8 && c.get(Calendar.MONTH) == month && c.get(Calendar.YEAR) == year){
                dayChunks.add(mForecastChunks.get(i));
            }
        }
        return dayChunks;
    }




    public void configureLabelsOnClickListeners(final View rootView){
        FrameLayout forecastLabelFrameLayout = rootView.findViewById(R.id.framelayout_day_detail_forecast_label);
        FrameLayout eventLabelFrameLayout = rootView.findViewById(R.id.framelayout_day_detail_events_label);

        forecastLabelFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mForecastIsHidden) {
                    if (mHasForecast) {
                        mForecastRecyclerView.setVisibility(View.GONE);
                    } else {
                        rootView.findViewById(R.id.textview_no_forecast_message).setVisibility(View.GONE);
                    }

                } else{
                    if (mHasForecast) {
                        mForecastRecyclerView.setVisibility(View.VISIBLE);
                    } else {
                        rootView.findViewById(R.id.textview_no_forecast_message).setVisibility(View.VISIBLE);
                    }
                }
                mForecastIsHidden = !mForecastIsHidden;
                configureLabelsOnClickListeners(rootView);
            }
        });

        eventLabelFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mEventsAreHidden){
                    if(mHasEvents){
                        mEventsRecyclerView.setVisibility(View.GONE);
                    } else {
                        rootView.findViewById(R.id.textview_no_events_message).setVisibility(View.GONE);
                    }
                } else {
                    if(mHasEvents){
                        mEventsRecyclerView.setVisibility(View.VISIBLE);
                    } else {
                        rootView.findViewById(R.id.textview_no_events_message).setVisibility(View.VISIBLE);
                    }
                }
                mEventsAreHidden = !mEventsAreHidden;
                configureLabelsOnClickListeners(rootView);
            }
        });


    }
}
