package com.example.googlemapsandplaces;

import android.app.Activity;
import android.util.Log;

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

    public void getDeviceLocationLatlong(Activity activity){
//        latLng = MapUtils.getDeviceLocationLatlong(activity);

        LatLng latLng = new LatLng(49, 12);

        Log.i(TAG, "Parking: getDeviceLocationLatlong(Activity activity): \n\norigin latLng: "+latLng.toString()+"\ndest laLng: "+lat+" "+lng);

        DistanceAndTime distanceAndTime = new DistanceAndTime(latLng, new LatLng(lat, lng));

        List<String> ls = distanceAndTime.getDistanceAndTime();

        this.distance = "Dis: " + ls.get(0);
        this.time = "Time: " + ls.get(1);
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
