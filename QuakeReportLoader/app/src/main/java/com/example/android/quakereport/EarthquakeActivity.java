/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity implements LoaderCallbacks<List<Info>> {

    public static final String LOG_TAG = EarthquakeActivity.class.getName();
    //private static final String USGS_REQUEST_URL = "http://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=2016-01-01&minmag=6&limit=15";

    private static final String USGS_REQUEST_URL = "http://earthquake.usgs.gov/fdsnws/event/1/query";


    /**
     * Constant value for the earthquake loader ID. We can choose any integer.
     * This really only comes into play if you are using multiple loaders.
     */
    private static final int INFO_LOADER_ID = 1;
    /** TextView that is displayed when the list is empty */
    private TextView mEmptyStateTextView;
    private TextView mNoInternetTextView;
    private InfoAdapter adapter;
    private ProgressBar mbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);
        // Find a reference to the {@link ListView} in the layout
        ListView earthquakeListView = (ListView) findViewById(R.id.list);
        // Create a new {@link ArrayAdapter} of earthquakes
        adapter = new InfoAdapter(this, new ArrayList<Info>());
        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(adapter);
        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current earthquake that was clicked on
                Info currentInfo = adapter.getItem(position);
                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri earthquakeUri = Uri.parse(currentInfo.getUrl());
                // Create a new intent to view the earthquake URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);
                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });


        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // fetch data
            // Get a reference to the LoaderMananger, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();


            // Initialize the loader. pass in the int ID constant defined above and pass in null for the bundle.
            // pass in this activity for the LoaderCallbacks parameter (which is valid because this activity
            // implements the LoaderCallbacks interface)
            loaderManager.initLoader(INFO_LOADER_ID, null, this);


            // set the screen to "No Earthquakes found" TextView when there is no data fetched from USGS
            mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
            earthquakeListView.setEmptyView(mEmptyStateTextView);

            // show the progress bar during fetching data
            mbar = (ProgressBar) findViewById(R.id.progressBar);
        } else {
            // display error
            mNoInternetTextView = (TextView) findViewById(R.id.no_internet_view);
            mNoInternetTextView.setText("No Internet.");
            earthquakeListView.setEmptyView(mNoInternetTextView);

            mbar = (ProgressBar) findViewById(R.id.progressBar);
            mbar.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



//    @Override
//    public Loader<List<Info>> onCreateLoader(int i, Bundle bundle) {
//        // create a new loader for the given URL
//        return new InfoLoader(this, USGS_REQUEST_URL);
//    }

    @Override
    public Loader<List<Info>> onCreateLoader(int i, Bundle bundle) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String minMagnitude = sharedPrefs.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default));

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );

        Uri baseUri = Uri.parse(USGS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("limit", "10");
        uriBuilder.appendQueryParameter("minmag", minMagnitude);
        uriBuilder.appendQueryParameter("orderby", orderBy);

        return new InfoLoader(this, uriBuilder.toString());
    }



    @Override
    public void onLoadFinished(Loader<List<Info>> loader, List<Info> earthquakes) {
        mbar.setVisibility(View.GONE);
        // Set empty state text to display "No earthquakes found"
        mEmptyStateTextView.setText(R.string.empty_string);
        // Clear the adapter of previous earthquake data
        adapter.clear();

        // If there is a valid list of {@link Info}s, then add them to the adapter's data set.
        // This will trigger the ListView to update
        if (earthquakes != null && !earthquakes.isEmpty()) {
            adapter.addAll(earthquakes);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Info>> loader) {
        // Loader reset, so we can clear out our existing data.
        adapter.clear();
    }


}
