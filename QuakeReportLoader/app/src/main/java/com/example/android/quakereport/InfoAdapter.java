package com.example.android.quakereport;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.graphics.drawable.GradientDrawable;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Pomme on 9/12/16.
 */
public class InfoAdapter extends ArrayAdapter<Info> {

    /**
     * This is the custom Adapter constructor
     * @param context
     * @param earthquakeInfo
     */
    public InfoAdapter(Activity context, ArrayList<Info> earthquakeInfo) {
        super(context, 0, earthquakeInfo);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listView = convertView;
        // Check if the existing view is being reused, otherwise inflate the view
        if (listView == null) {
            listView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        // Get (@link Info) object located at this position in the list
        Info currentInfo = getItem(position);


        double magnitue = currentInfo.getMag();
        // Use DecimalFormat to format the magnitue to be one digit after ".".
        DecimalFormat decimalFomatter = new DecimalFormat("0.0");
        String mag = decimalFomatter.format(magnitue);

        // find the textView in the list_item.xml layout with the location
        TextView magTextView = (TextView) listView.findViewById(R.id.text_mag);
        // Get the location from current Info object and set this text on the location TextView
        magTextView.setText(mag);


        // Set the proper background color on the magnitude circle.
        // Fetch the background from the TextView, which is a GradientDrawable.
        GradientDrawable magnitudeCircle = (GradientDrawable) magTextView.getBackground();

        // Get the appropriate background color based on the current earthquake magnitude
        int magnitudeColor = getMagnitudeColor(magnitue);

        // Set the color on the magnitude circle
        magnitudeCircle.setColor(magnitudeColor);







        String fullLocation = currentInfo.getLocation();
        String primaryLoctionStr = "";
        String distanceStr = "Near the";
        if (fullLocation.contains(" of ")) {
            int index = fullLocation.indexOf(" of ");
            primaryLoctionStr = fullLocation.substring(index + 4);
            distanceStr = fullLocation.substring(0, index + 3);
        }
        else {
            primaryLoctionStr = fullLocation;
        }
        // find the textView in the list_item.xml layout with the primarylocation
        TextView primarylocationTextView = (TextView) listView.findViewById(R.id.text_primary_location);
        // find the textView in the list_item.xml layout with the distance
        TextView distanceTextView = (TextView) listView.findViewById(R.id.text_distance);
        // Get the location from current Info object and set this text on the TextView
        primarylocationTextView.setText(primaryLoctionStr);
        distanceTextView.setText(distanceStr);


        // find the textView in the list_item.xml layout with the location
        TextView timeTextView = (TextView) listView.findViewById(R.id.text_time);
        // Get the location from current Info object and set this text on the location TextView
        timeTextView.setText(currentInfo.getTime());


        return listView;
    }





    private int getMagnitudeColor(double mag) {
        int roundUpMagnitude = (int) Math.floor(mag);
        int backgroundColorID;
        switch (roundUpMagnitude) {
            case 0:
            case 1:
                backgroundColorID = R.color.magnitude1;
                break;
            case 2: backgroundColorID = R.color.magnitude2;
                break;
            case 3: backgroundColorID = R.color.magnitude3;
                break;
            case 4: backgroundColorID = R.color.magnitude4;
                break;
            case 5: backgroundColorID = R.color.magnitude5;
                break;
            case 6: backgroundColorID = R.color.magnitude6;
                break;
            case 7: backgroundColorID = R.color.magnitude7;
                break;
            case 8: backgroundColorID = R.color.magnitude8;
                break;
            case 9: backgroundColorID = R.color.magnitude9;
                break;
            default: backgroundColorID = R.color.magnitude10plus;
                break;
        }
        return ContextCompat.getColor(getContext(), backgroundColorID);
    }



}
