package com.example.googlemapsandplaces;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


// use Serializable in order to pass the packing object from one Intent to another
public class Parking implements Serializable {

    private Double lat, lng;
    private String iban, placeName, address;

    public Parking(Double lat, Double lng, String iban, String placeName, String address) {
        this.lat = lat;
        this.lng = lng;
        this.iban = iban;
        this.placeName = placeName;
        this.address = address;
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

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }
}
