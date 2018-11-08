package com.example.android.weathercalendar.sync;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class WeatherFirebaseJobService extends JobService{

    private static final String TAG = WeatherFirebaseJobService.class.getSimpleName();
    private AsyncTask<Void, Void, Void> mAsyncTask;

    @Override
    public boolean onStartJob(final JobParameters params) {
        Log.e(TAG, "onStartJob Fired");
        mAsyncTask = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                Context context = getApplicationContext();
                WeatherSyncTask.syncWeather(context);
                jobFinished(params, false);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                jobFinished(params, false);
            }
        };
        mAsyncTask.execute();

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        if (mAsyncTask != null) {
            mAsyncTask.cancel(true);
        }
        return true;
    }
}
