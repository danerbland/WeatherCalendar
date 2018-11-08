package com.example.android.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.util.Calendar;


public class CalendarUtils {

    public static String TAG = CalendarUtils.class.getSimpleName();

    private static int MY_PERMISSIONS_REQUEST_WRITE_CALENDAR = 777;


    public static void insertEvent(Context context, long starttime, long endtime, String description, long calendarId, String timezone) {

        ContentResolver cr = context.getContentResolver();
        ContentValues contentValues = new ContentValues();

        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, starttime);
        values.put(CalendarContract.Events.DTEND, endtime);
        values.put(CalendarContract.Events.TITLE, description);
        values.put(CalendarContract.Events.DESCRIPTION, "");
        values.put(CalendarContract.Events.CALENDAR_ID, calendarId);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, timezone);


        cr.insert(CalendarContract.Events.CONTENT_URI, values);
    }

    public static void insertEvent2(Context c){
        long calID = 3;
        long startMillis;
        long endMillis;
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(2012, 9, 14, 7, 30);
        startMillis = beginTime.getTimeInMillis();
        Calendar endTime = Calendar.getInstance();
        endTime.set(2012, 9, 14, 8, 45);
        endMillis = endTime.getTimeInMillis();

        ContentResolver cr = c.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.TITLE, "Jazzercise");
        values.put(CalendarContract.Events.DESCRIPTION, "Group workout");
        values.put(CalendarContract.Events.CALENDAR_ID, calID);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "America/Los_Angeles");
        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

// get the event ID that is the last element in the Uri
        long eventID = Long.parseLong(uri.getLastPathSegment());
    }

    public static void addCalendar(Context context, Activity activity) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(CalendarContract.Calendars.ACCOUNT_NAME, "dan@danerbland.com");
        contentValues.put(CalendarContract.Calendars.ACCOUNT_TYPE, "danerbland.com");
        contentValues.put(CalendarContract.Calendars.NAME, "Weather Calendar");
        contentValues.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, "Weather Calendar");
        contentValues.put(CalendarContract.Calendars.CALENDAR_COLOR, "232323");
        contentValues.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
        contentValues.put(CalendarContract.Calendars.OWNER_ACCOUNT, "danerbland@gmail.com");
        contentValues.put(CalendarContract.Calendars.ALLOWED_REMINDERS, "METHOD_ALERT, METHOD_EMAIL, METHOD_ALARM");
        contentValues.put(CalendarContract.Calendars.ALLOWED_ATTENDEE_TYPES, "TYPE_OPTIONAL, TYPE_REQUIRED, TYPE_RESOURCE");
        contentValues.put(CalendarContract.Calendars.ALLOWED_AVAILABILITY, "AVAILABILITY_BUSY, AVAILABILITY_FREE, AVAILABILITY_TENTATIVE");


        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_CALENDAR}, MY_PERMISSIONS_REQUEST_WRITE_CALENDAR);
        }

        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        uri = uri.buildUpon().appendQueryParameter(android.provider.CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, "cal@zoftino.com")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, "cal.zoftino.com").build();
        context.getContentResolver().insert(uri, contentValues);
    }

    //
    public static void inspectCalendars(Context context) {

        Cursor cursor;

        if (android.os.Build.VERSION.SDK_INT <= 7) {
            cursor = context.getContentResolver().query(Uri.parse("content://calendar/calendars"), new String[]{"_id", "displayName"}, null,
                    null, null);

        } else if (android.os.Build.VERSION.SDK_INT <= 14) {
            cursor = context.getContentResolver().query(Uri.parse("content://com.android.calendar/calendars"),
                    new String[]{"_id", "displayName"}, null, null, null);

        } else {
            cursor = context.getContentResolver().query(Uri.parse("content://com.android.calendar/calendars"),
                    new String[]{"_id", "calendar_displayName", "ownerAccount"}, null, null, null);

        }

        // Get calendars name
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            String[] calendarNames = new String[cursor.getCount()];
            // Get calendars id
            int calendarIds[] = new int[cursor.getCount()];

            String[] calendarownerAccounts = new String[cursor.getCount()];
            for (int i = 0; i < cursor.getCount(); i++) {
                calendarIds[i] = cursor.getInt(0);
                calendarNames[i] = cursor.getString(1);
                calendarownerAccounts[i] = cursor.getString(2);
                Log.e(TAG, "Calendar Name : " + calendarNames[i] + "   id: " + calendarIds[i] + "   Owner Account: " + calendarownerAccounts[i]);
                cursor.moveToNext();
            }
        } else {
            Log.e(TAG, "No calendar found in the device");
        }

        cursor.close();

        return;
    }


    public static long getWeatherCalendarId(Context context) {

        Cursor cursor;

        if (android.os.Build.VERSION.SDK_INT <= 7) {
            cursor = context.getContentResolver().query(Uri.parse("content://calendar/calendars"), new String[]{"_id", "displayName"}, null,
                    null, null);

        } else if (android.os.Build.VERSION.SDK_INT <= 14) {
            cursor = context.getContentResolver().query(Uri.parse("content://com.android.calendar/calendars"),
                    new String[]{"_id", "displayName"}, null, null, null);

        } else {
            cursor = context.getContentResolver().query(Uri.parse("content://com.android.calendar/calendars"),
                    new String[]{"_id", "calendar_displayName", "ownerAccount"}, null, null, null);

        }

        // Get calendars name
        Log.i(TAG, "Cursor count " + cursor.getCount());
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            String[] calendarNames = new String[cursor.getCount()];
            // Get calendars id
            int calendarIds[] = new int[cursor.getCount()];

            String[] calendarownerAccounts = new String[cursor.getCount()];
            for (int i = 0; i < cursor.getCount(); i++) {
                calendarIds[i] = cursor.getInt(0);
                calendarNames[i] = cursor.getString(1);
                calendarownerAccounts[i] = cursor.getString(2);
                if(calendarownerAccounts[i].matches("danerbland@gmail.com")){
                    return (long)calendarIds[i];
                }
                cursor.moveToNext();
            }
        } else {
            Log.e(TAG, "No calendar found in the device");
        }

        cursor.close();

        return -1;
    }


    //Help from http://www.zoftino.com/how-to-read-and-write-calendar-data-in-android
    public static void getDataFromCalendarTable(Context context) {
        Cursor cur;
        ContentResolver cr = context.getContentResolver();

        String[] mProjection =
                {
                        CalendarContract.Calendars._ID,
                        CalendarContract.Calendars.ALLOWED_ATTENDEE_TYPES,
                        CalendarContract.Calendars.ACCOUNT_NAME,
                        CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                        CalendarContract.Calendars.CALENDAR_LOCATION,
                        CalendarContract.Calendars.CALENDAR_TIME_ZONE
                };

        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        String selection = "((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND ("
                + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?) AND ("
                + CalendarContract.Calendars.OWNER_ACCOUNT + " = ?))";
        String[] selectionArgs = new String[]{"cal@zoftino.com", "cal.zoftino.com",
                "cal@zoftino.com"};

        cur = cr.query(uri, mProjection, selection, selectionArgs, null);

        if(cur == null){
            Log.e(TAG, "getDataFromCalendarTable is FUCKED");
        }
        while (cur.moveToNext()) {
            Log.e(TAG, "getDataFromCalendatTable is SLIGHTLY LESS FUCKED");
            String displayName = cur.getString(cur.getColumnIndex(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME));
            String accountName = cur.getString(cur.getColumnIndex(CalendarContract.Calendars.ACCOUNT_NAME));
            int ID = cur.getInt(cur.getColumnIndex(CalendarContract.Calendars._ID));

            Log.e(TAG, displayName + "  " + accountName + "  " + ID);

        }

        cur.close();
    }


    public static Cursor queryEventsFromEventTable(Context c, long dtStart, long dtEnd){
        Cursor cur;
        ContentResolver cr = c.getContentResolver();

        String[] mProjection =
                {
                        "_id",
                        CalendarContract.Events.TITLE,
                        CalendarContract.Events.EVENT_LOCATION,
                        CalendarContract.Events.DTSTART,
                        CalendarContract.Events.DTEND,
                };

        Uri uri = CalendarContract.Events.CONTENT_URI;
        String selection = "((" + CalendarContract.Events.DTSTART + " >= ?) AND ( "
                + CalendarContract.Events.DTSTART + " <= ?))";
        String[] selectionArgs = new String[]{Long.toString(dtStart), Long.toString(dtEnd)};

        cur = cr.query(uri, mProjection, selection, selectionArgs, null);
        return cur;
    }

    public static void getDataFromEventTable(Context c) {

        Cursor cur;
        ContentResolver cr = c.getContentResolver();

        String[] mProjection =
                {
                        "_id",
                        CalendarContract.Events.TITLE,
                        CalendarContract.Events.EVENT_LOCATION,
                        CalendarContract.Events.DTSTART,
                        CalendarContract.Events.DTEND,
                };

        Uri uri = CalendarContract.Events.CONTENT_URI;
//        String selection = CalendarContract.Events.EVENT_LOCATION + " = ? ";
//        String[] selectionArgs = new String[]{"London"};

        cur = cr.query(uri, mProjection, null, null, null);

        while (cur.moveToNext()) {
            String title = cur.getString(cur.getColumnIndex(CalendarContract.Events.TITLE));
            int ID = cur.getInt(cur.getColumnIndex("_id"));
            Log.e(TAG, title + "\t" + Integer.toString(ID));
        }

        cur.close();

    }

    public static void getsingleEventFromEventTable(Context c, int id) {

        Cursor cur;
        ContentResolver cr = c.getContentResolver();

        String[] mProjection =
                {
                        "_id",
                        CalendarContract.Events.TITLE,
                        CalendarContract.Events.EVENT_LOCATION,
                        CalendarContract.Events.DTSTART,
                        CalendarContract.Events.DTEND,
                };

        Uri uri = CalendarContract.Events.CONTENT_URI;
        String selection = CalendarContract.Events._ID + " = ? ";
        String[] selectionArgs = new String[]{Integer.toString(id)};

        cur = cr.query(uri, mProjection, null, null, null);

        while (cur.moveToNext()) {
            String title = cur.getString(cur.getColumnIndex(CalendarContract.Events.TITLE));
            int ID = cur.getInt(cur.getColumnIndex("_id"));
            Log.e(TAG, title + "\t" + Integer.toString(ID));
        }

        cur.close();

    }

    public static void updateEvent(Context c, long eventId, String Description, long startTime, long endTime) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(CalendarContract.Events.TITLE, Description);
        contentValues.put(CalendarContract.Events.DTSTART, startTime);
        contentValues.put(CalendarContract.Events.DTEND, endTime);


        Uri uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId);

        int updCount = c.getContentResolver().update(uri, contentValues,null,null);
        Log.e(TAG, "events updated: " + updCount);

    }

    public static void deleteEvent(Context c, int id) {

        Uri uri = CalendarContract.Events.CONTENT_URI;

        String mSelectionClause = CalendarContract.Events._ID + " = ?";
        String[] mSelectionArgs = {Integer.toString(id)};

        int updCount = c.getContentResolver().delete(uri,mSelectionClause,mSelectionArgs);

    }


    public static String getAmOrPm(Calendar c){
        if(c.get(Calendar.HOUR_OF_DAY) >=12){
            return "PM";
        }
        else{
            return "AM";
        }
    }

    public static String getMinutesString(Calendar c){
        if(c.get(Calendar.MINUTE) < 10){
            return "0" + String.valueOf(c.get(Calendar.MINUTE));
        } else {
            return String.valueOf(c.get(Calendar.MINUTE));
        }
    }

    public static String getHoursString(Calendar c){
        if(c.get(Calendar.HOUR) == 0){
            return "12";
        } else {
            return String.valueOf(c.get(Calendar.HOUR));
        }
    }

    public static String getTimeString(Calendar c){
        return getHoursString(c) +":" + CalendarUtils.getMinutesString(c) + " " + CalendarUtils.getAmOrPm(c);
    }

    public static String getDateString(Calendar c){
        return c.get(Calendar.MONTH) + "/" + c.get(Calendar.DAY_OF_MONTH) + "/" + c.get(Calendar.YEAR);
    }

}
