package com.example.android.quakereport;

import java.sql.Time;

/**
 * Created by Pomme on 9/12/16.
 */
public class Info {
    private String location;
    private double mag;
    private String time;
    private String url;

    public Info(String loc, double magnitude, String t, String urlString) {
        location = loc;
        mag = magnitude;
        time = t;
        url = urlString;
    }

    public String getLocation() {
        return location;
    }

    public double getMag() {
        return mag;
    }

    public String getTime() {
        return time;
    }

    public String getUrl() {
        return url;
    }
}
