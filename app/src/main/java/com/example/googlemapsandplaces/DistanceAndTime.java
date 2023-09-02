package com.example.googlemapsandplaces;

import android.util.Log;

import com.example.googlemapsandplaces.gpay.Constants;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DistanceAndTime {

    private static final String TAG = ".DistanceAndTime";

    private LatLng curr_location, dest_location;

    public DistanceAndTime(LatLng curr_location, LatLng dest_location) {
        this.curr_location = curr_location;
        this.dest_location = dest_location;
    }

    public List<String> getDistanceAndTime(){
        JSONObject jsonObject = new JSONObject();

        List<String> disDur = new ArrayList<>(2);
        try {

            StringBuilder stringBuilder = getDistanceAndTimeJsonObject();

            Log.d(TAG, "getDistanceAndTime(): stringBuilder = getDistanceAndTimeJsonObject(): \n\n"+stringBuilder+"\n\n");
            Log.d(TAG, "getDistanceAndTime(): stringBuilder.toString(): \n\n"+stringBuilder.toString()+"\n\n");

            jsonObject = new JSONObject(stringBuilder.toString());

            Log.d(TAG, "getDistanceAndTime(): jsonObject = new JSONObject(stringBuilder.toString()) \n\n"+jsonObject+"\n\n");

            Log.d(TAG, "getDistanceAndTime(): jsonObject: \n"+jsonObject);

            JSONArray array = jsonObject.getJSONArray("routes");

            JSONObject routes = array.getJSONObject(0);

            JSONArray legs = routes.getJSONArray("legs");

            JSONObject steps = legs.getJSONObject(0);

            JSONObject distance = steps.getJSONObject("distance");
            JSONObject duration = steps.getJSONObject("duration");

            Log.d(TAG, "getDistanceAndTime(): distance: \n"+distance);

            String distanceText = distance.getString("text");
            String durationText = duration.getString("text");

            Log.d(TAG, "getDistanceAndTime(): distance.getString(\"text\"): \n"+distanceText);
            Log.d(TAG, "getDistanceAndTime(): duration.getString(\"text\"): \n"+durationText);

            disDur.add(distanceText);
            disDur.add(durationText);

            Log.i("Distance", distance.toString());
            Log.i("Duration", duration.toString());

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.e(TAG, "getDistanceAndTime(): JSONException: \n"+e.toString());
            e.printStackTrace();
        }

        return disDur;
    }

    public StringBuilder getDistanceAndTimeJsonObject(){

        HttpURLConnection mUrlConnection = null;
        StringBuilder mJsonResults = new StringBuilder();

        try {
            StringBuilder sb = getDirectionAndTimeUrl();
            Log.i(TAG, "getDistanceAndTimeJsonObject(): sb = getDirectionAndTimeUrl(): \n\n"+sb+"\n\n\n");
            URL url = new URL(sb.toString());
            mUrlConnection = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(mUrlConnection.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                mJsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(TAG, "getDistanceAndTimeJsonObject(): MalformedURLException Error processing Distance Matrix API URL");
            return null;

        } catch (IOException e) {
            Log.e(TAG, "getDistanceAndTimeJsonObject(): IOException Error connecting to Distance Matrix");
            return null;
        } finally {
            if (mUrlConnection != null) {
                mUrlConnection.disconnect();
            }
        }

        return mJsonResults;
    }

    private StringBuilder getDirectionAndTimeUrl() {

        // API KEY of the project
        final String MAP_API_KEY  = Constants.MAP_API_KEY ;

//        https://maps.googleapis.com/maps/api/directions/json?origin=41.889810,12.473360&destination=41.897850,12.518360&sensor=false&mode=driving&key=AIzaSyBxw6rx7AsUl0gvuFu81mKtm94QHAbuoOc
        String output = "json";
        String str_origin = "origin=" + curr_location.latitude + "," + curr_location.longitude;
        String str_dest = "destination=" + dest_location.latitude + "," + dest_location.longitude;
        String sensor = "sensor=false";
        String mode = "mode=driving";

        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode + "&key=" + MAP_API_KEY ;

        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters);

        return url;
    }
}
