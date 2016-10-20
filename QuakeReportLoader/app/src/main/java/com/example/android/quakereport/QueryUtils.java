package com.example.android.quakereport;

import android.os.AsyncTask;
import android.renderscript.ScriptGroup;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Pomme on 9/12/16.
 * QueryUtility class with methods to help perform the HTTP request and parse the response.
 */
public final class QueryUtils {

    /* Tag for the log messages */
    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */


    /**
     * Query the USGS dataset and return an {@link Info} object to represent a single earthquake
     */
    public static ArrayList<Info> fetchEarthquakeData(String requestUrl) {

        // Wait 2000 ms before fetch data.
//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }



        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        // Extract relevant fields from the JSON response and create an {@link Info} object
        ArrayList<Info> earthquake = extractEarthquakes(jsonResponse);
        return earthquake;
    }

    /**
     * @return new URL object from the given string URL
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     * @return response
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        // if the URL is null, then return early
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(1000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            // if the request was successful (response code 200), then read the input stream
            // and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results", e);
        }
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }


    /**
     * Convert the {@link InputStream} into a String which contains the whole JSON response from the server.
     * @return output string
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    public static ArrayList<Info> extractEarthquakes(String earthquakeJSON){

        // Create an empty ArrayList that we can start adding earthquakes to
        ArrayList<Info> earthquakes = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // TODO: Parse the response given by the SAMPLE_JSON_RESPONSE string and
            // convert a string into JSON Object
            JSONObject rootObject = new JSONObject(earthquakeJSON);
            // Extract "features" JSONArray
            JSONArray jsonArray = rootObject.getJSONArray("features");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject newObject = jsonArray.getJSONObject(i);
                JSONObject propertyObject = newObject.getJSONObject("properties");
                // Extract the value for the key called "mag" as Magnitude of earthquake
                Double mag = propertyObject.optDouble("mag");
                String location = propertyObject.optString("place").toString();

                // Extract the value for the key called "time"
                long timeInMilliseconds = propertyObject.optLong("time");
                // Create a new date object from the timeInMilliseconds
                Date dateObject = new Date(timeInMilliseconds);
                SimpleDateFormat newDateFormatter = new SimpleDateFormat("MMM dd, yyyy\nHH:mm a");
                String dateString = newDateFormatter.format(dateObject);

                // Extract the value for the key called "url"
                String urlStr = propertyObject.optString("url");
                // create a new {@link Info} object with the magnitude, location, time and url from the JSON response.
                earthquakes.add(new Info(location, mag, dateString, urlStr));
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of earthquakes
        return earthquakes;
    }




}
