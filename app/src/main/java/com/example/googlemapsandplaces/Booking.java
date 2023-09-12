package com.example.googlemapsandplaces;

import java.io.Serializable;

public class Booking implements Serializable {

    private static final String TAG = ".Booking";

    private String id, email, parkingID;

    public Booking() {}

    public Booking(String id, String email, String parkingID) {
        this.id = id;
        this.email = email;
        this.parkingID = parkingID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getParkingID() {
        return parkingID;
    }

    public void setParkingID(String parkingID) {
        this.parkingID = parkingID;
    }
}
