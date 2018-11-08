package com.example.android.weathercalendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;

import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import android.widget.TextView;
import android.widget.TimePicker;

import com.example.android.utils.CalendarUtils;

import java.util.Calendar;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.text.format.DateUtils.HOUR_IN_MILLIS;

public class AddEventPopup extends Activity {

    private String TAG = AddEventPopup.class.getSimpleName();

    @BindView(R.id.edittext_add_event_description) EditText descriptionEditText;
    @BindView(R.id.textview_add_event_start_date) TextView startDateTextview;
    @BindView(R.id.textview_add_event_end_date) TextView endDateTextView;
    @BindView(R.id.textview_add_event_start_time) TextView startTimeTextView;
    @BindView(R.id.textview_add_event_end_time) TextView endTimeTextView;
    @BindView(R.id.constraintlayout_add_event_start_date) ConstraintLayout mStartDateConstraintLayout;
    @BindView(R.id.constraintlayout_add_event_end_date) ConstraintLayout mEndDateConstraintLayout;
    @BindView(R.id.button_cancel) Button mCancelButton;
    @BindView(R.id.button_save) Button mSaveButton;

    private DatePickerDialog.OnDateSetListener mStartDateSetListener;
    private TimePickerDialog.OnTimeSetListener mStartTimeSetListener;
    private DatePickerDialog.OnDateSetListener mEndDateSetListener;
    private TimePickerDialog.OnTimeSetListener mEndTimeSetListener;
    private Calendar mStartDateCalendar;
    private Calendar mEndDateCalendar;

    private long mEventId = -1;

    private TimePicker mTimePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_add_event);
        ButterKnife.bind(this);

        //Created with help from Filip Vujovic:
        //https://www.youtube.com/watch?v=fn5OlqQuOCk
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * 0.9), (int) (height * 0.4));



        //Initialize our calendars to track the start time and end time of the event.
        //There are 3 cases to consider:  new event from calendar screen, new event from day detail screen,
        //and updating event from day detail screen.
        if (getIntent().getExtras() != null) {
            Bundle extras = getIntent().getExtras();
            try {
                mEventId = Integer.valueOf(extras.getString(getString(R.string.event_extras_id_key)));
            } catch(NumberFormatException e){
                e.printStackTrace();
                mEventId  = -1;
            }
            Log.e(TAG, "Retrieving event id: " + mEventId);
            long startTime = extras.getLong(getString(R.string.event_extras_Start_Date_key));
            long endTime = extras.getLong(getString(R.string.event_extras_End_Date_key));
            String title = extras.getString(getString(R.string.event_extras_Title_key));

            mStartDateCalendar = Calendar.getInstance();
            mEndDateCalendar = Calendar.getInstance();
            mStartDateCalendar.setTimeInMillis(startTime);
            mEndDateCalendar.setTimeInMillis(endTime);

            if(title!=null) {
                descriptionEditText.setText(title);
            }
        }

        //If this is a new event
        if (getIntent().getExtras() == null){
            mStartDateCalendar = Calendar.getInstance();
            mEndDateCalendar = Calendar.getInstance();
            mEndDateCalendar.setTimeInMillis(mStartDateCalendar.getTimeInMillis() + 3600000);
        }

        updateUI();


        configureStartDateConstraintLayout();
        configureEndDateConstraintLayout();
        configureCancelButton();
        configureSaveButton();



    }

    //Used to update the start date/time and end date/time
    private void updateUI(){
        String startDateString = (mStartDateCalendar.get(Calendar.MONTH)+1)+ "/" + mStartDateCalendar.get(Calendar.DAY_OF_MONTH) + "/" + mStartDateCalendar.get(Calendar.YEAR);
        String startTimeString = CalendarUtils.getTimeString(mStartDateCalendar);

        startDateTextview.setText(startDateString);
        startTimeTextView.setText(startTimeString);

        String endDateString = (mEndDateCalendar.get(Calendar.MONTH)+1)+ "/" + mEndDateCalendar.get(Calendar.DAY_OF_MONTH) + "/" + mEndDateCalendar.get(Calendar.YEAR);
        String endTimeString = CalendarUtils.getTimeString(mEndDateCalendar);
        endDateTextView.setText(endDateString);
        endTimeTextView.setText(endTimeString);
    }

    private void configureStartDateConstraintLayout(){
        final int mStartYear = mStartDateCalendar.get(Calendar.YEAR);
        final int mStartMonth = mStartDateCalendar.get(Calendar.MONTH);
        final int mStartDay = mStartDateCalendar.get(Calendar.DAY_OF_MONTH);
        final int mStartHour = mStartDateCalendar.get(Calendar.HOUR_OF_DAY);
        final int mStartMinute = mStartDateCalendar.get(Calendar.MINUTE);
        updateUI();

        //Set OnClickListener for Start Date layout.

        mStartDateConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                DatePickerDialog dDialog = new DatePickerDialog(
                        AddEventPopup.this,
                        android.app.AlertDialog.THEME_HOLO_LIGHT,
                        mStartDateSetListener,
                        mStartYear, mStartMonth, mStartDay);
                dDialog.setTitle("Start Date");
                //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dDialog.show();


            }

        });

        mStartDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mStartDateCalendar = Calendar.getInstance();
                mStartDateCalendar.set(Calendar.YEAR, year);
                mStartDateCalendar.set(Calendar.MONTH, month);
                mStartDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                TimePickerDialog tDialog = new TimePickerDialog(
                        AddEventPopup.this,
                        android.app.AlertDialog.THEME_HOLO_LIGHT,
                        mStartTimeSetListener,
                        mStartHour,
                        mStartMinute,
                        false
                );
                tDialog.setTitle("Start Time");
                tDialog.show();

            }
        };

        mStartTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                mStartDateCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                mStartDateCalendar.set(Calendar.MINUTE, minute);
                mEndDateCalendar.setTimeInMillis(mStartDateCalendar.getTimeInMillis() + HOUR_IN_MILLIS);
                configureEndDateConstraintLayout();
                updateUI();
                configureStartDateConstraintLayout();
            }
        };

    }


    private void configureEndDateConstraintLayout() {

        final int mEndYear = mEndDateCalendar.get(Calendar.YEAR);
        final int mEndMonth = mEndDateCalendar.get(Calendar.MONTH);
        final int mEndDay = mEndDateCalendar.get(Calendar.DAY_OF_MONTH);
        final int mEndHour = mEndDateCalendar.get(Calendar.HOUR_OF_DAY);
        final int mEndMinute = mEndDateCalendar.get(Calendar.MINUTE);

        mEndDateConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                DatePickerDialog dDialog = new DatePickerDialog(
                        AddEventPopup.this,
                        android.app.AlertDialog.THEME_HOLO_LIGHT,
                        mEndDateSetListener,
                        mEndYear, mEndMonth, mEndDay);
                dDialog.setTitle("End Date");
                //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dDialog.show();


            }

        });

        mEndDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mEndDateCalendar = Calendar.getInstance();
                mEndDateCalendar.set(Calendar.YEAR, year);
                mEndDateCalendar.set(Calendar.MONTH, month);
                mEndDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                TimePickerDialog tDialog = new TimePickerDialog(
                        AddEventPopup.this,
                        android.app.AlertDialog.THEME_HOLO_LIGHT,
                        mEndTimeSetListener,
                        mEndHour,
                        mEndHour,
                        false
                );
                tDialog.setTitle("End Time");
                tDialog.show();

            }
        };

        mEndTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                mEndDateCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                mEndDateCalendar.set(Calendar.MINUTE, minute);
                updateUI();
                configureEndDateConstraintLayout();
            }
        };
    }


    private void configureSaveButton(){
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = descriptionEditText.getText().toString();
                if(description.matches("")){
                    description = "Untitled Event";
                }
                //If this is a new event, insert it. If it is not, update it.
                if (mEventId == -1){
                    CalendarUtils.insertEvent(getApplicationContext(), mStartDateCalendar.getTimeInMillis(),
                            mEndDateCalendar.getTimeInMillis(), description,
                            CalendarUtils.getWeatherCalendarId(getApplicationContext()), TimeZone.getDefault().getDisplayName());
                } else{
                    Log.e(TAG, "Updating Event: " + mEventId);
                    CalendarUtils.updateEvent(getApplicationContext(), mEventId, description, mStartDateCalendar.getTimeInMillis(), mEndDateCalendar.getTimeInMillis());
                }

                //Help from StackOverflow question 3455123
                Intent intent = new Intent(AddEventPopup.this, WeatherCalendarWidget.class);
                intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                int[]ids = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), WeatherCalendarWidget.class));
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
                sendBroadcast(intent);

                onBackPressed();
            }
        });

    }

    private void configureCancelButton(){
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
