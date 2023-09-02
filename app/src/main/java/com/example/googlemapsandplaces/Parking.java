package com.example.googlemapsandplaces;

import android.app.Activity;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.List;


// use Serializable in order to pass the packing object from one Intent to another
public class Parking implements Serializable {

    private static final String TAG = ".Parking";

    private Double lat, lng;
    private String placeName, address;

    private String distance = "Dis: 10km", time="Time: 20min", price="€5.20/hr";

    private String total = "Total: 100", rem="Rem: 20";

    private LatLng latLng;


    public Parking(Double lat,
                   Double lng,
                   String price,
                   String placeName,
                   String address,
                   String total,
                   String rem)
    {
        this.lat = lat;
        this.lng = lng;
        this.price = price;
        this.placeName = placeName;
        this.address = address;
        this.total = total;
        this.rem = rem;
    }

    public interface DistanceCallback {
        void onDistanceReceived(String distance, String time);
    }

    public void getDistanceAndTime(Activity activity, DistanceCallback callback) {
        // Use the Fused Location Provider to get the current location
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);

        try {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(activity, location -> {
                if (location != null) {
                    LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    calculateDistanceAndTime(currentLocation, callback);
                } else {
                    // Handle the case where the current location is null
                    callback.onDistanceReceived("N/A", "N/A");
                }
            }).addOnFailureListener(e -> {
                // Handle any errors that occurred while getting the location
                callback.onDistanceReceived("N/A", "N/A");
            });
        } catch (SecurityException e) {
            // Handle location permission issues
            callback.onDistanceReceived("N/A", "N/A");
        }
    }

    private void calculateDistanceAndTime(LatLng currentLocation, DistanceCallback callback) {
        // Use the current location and dest_location to calculate distance and time
        // This is where you can use your existing DistanceAndTime class
        DistanceAndTime distanceAndTime = new DistanceAndTime(currentLocation, new LatLng(getLat(), getLng()));

        List<String> disDur = distanceAndTime.getDistanceAndTime();
        String distance = disDur.get(0);
        String time = disDur.get(1);

        // Return the calculated distance and time via the callback
        callback.onDistanceReceived(distance, time);
    }

    // Getters and setters for properties

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTotalPlaces() {
        return total;
    }

    public void setTotalPlaces(String total) {
        this.total = total;
    }

    public String getRemPlaces() {
        return rem;
    }

    public void setRemPlaces(String rem) {
        this.rem = rem;
    }
}
