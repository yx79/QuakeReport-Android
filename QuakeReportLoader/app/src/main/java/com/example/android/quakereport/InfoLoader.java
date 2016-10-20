package com.example.android.quakereport;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

/**
 * Loads a list of earthquakes by using an AsyncTask to perform the network request to the given URL
 */
public class InfoLoader extends AsyncTaskLoader<List<Info>> {
    /** Tag for long messages */
    private static final String LOG_TAG = InfoLoader.class.getName();

    /** Query URL */
    private String mUrl;
    /**
     * Constructs a new {@link InfoLoader}
     * @param context of the activity
     * @param url to load data from
     */
    public InfoLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This is on a background thread
     */
    @Override
    public List<Info> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of Info
        List<Info> earthquakes = QueryUtils.fetchEarthquakeData(mUrl);
        return earthquakes;
    }









}
