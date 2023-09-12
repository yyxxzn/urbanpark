package com.example.googlemapsandplaces.firebasedb;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.googlemapsandplaces.Parking;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FirebaseHelper {

    private DatabaseReference databaseReference;
    private List<Parking> parkingList;

    // Define a timeout value (in milliseconds)
    private static final long TIMEOUT_VALUE = 1 * 60 * 1000; // 1 minute
    private long lastFetchTime = 0; // Initialize with 0

    public FirebaseHelper() {
        // Initialize the Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("parking");
        parkingList = new ArrayList<>();
    }

    public FirebaseHelper(String parkingID) {
        // Initialize the Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("parking").child(parkingID);
        parkingList = new ArrayList<>();
    }

    // Create a new parking item
    public void createParking(Parking parking) {
//        String key = databaseReference.push().getKey();
        String key = parking.getAddress();
        databaseReference.child(key).setValue(parking);
    }

    // Update an existing parking item
    public void updateParking(Parking updatedParking) {
        String key = updatedParking.getAddress();
        databaseReference.child(key).setValue(updatedParking);
    }

    // Delete a parking item
    public void deleteParking(String key) {
        databaseReference.child(key).removeValue();
    }

    // Get a single parking item by key
    public void getParking(String key, final OnParkingDataReceivedListener listener) {
        databaseReference.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Parking parking = dataSnapshot.getValue(Parking.class);
                listener.onDataReceived(parking);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onError(databaseError.getMessage());
            }
        });
    }

    // Get all parking items
    public void getAllParkings(LocationSortOption sortOption, Activity activity, final OnParkingsDataReceivedListener listener) {

        parkingList.clear();
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                Parking parking = dataSnapshot.getValue(Parking.class);

                // Call this to get the current location and the destination
                parking.getDistanceAndTime(activity, new Parking.DistanceCallback() {
                    @Override
                    public void onDistanceReceived(String distance, String time) {
                        // Update the UI with the calculated distance and time.
                        parking.setDistance(distance);
                        parking.setTime(time);

                        parkingList.add(parking);
                        sortParking(parkingList, sortOption);

                        listener.onDataReceived(parkingList);
                    }
                });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
                // Handle changes to child data
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                // Handle removed child data
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {
                // Handle moved child data
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onError(databaseError.getMessage());
            }
        });
    }

    // Search for parking items based on placeName or address and sort by distance
    /*public void searchAndSortParking(final String query, final LocationSortOption sortOption, final OnParkingsDataReceivedListener listener) {
        // Create a new list to store the search results
        final List<Parking> searchResults = new ArrayList<>();

        long currentTime = System.currentTimeMillis();
        long elapsedTimeSinceLastFetch = currentTime - lastFetchTime;

        // Check if parking data is empty or if the timeout has exceeded
        if (parkingList.isEmpty() || elapsedTimeSinceLastFetch > TIMEOUT_VALUE) {
            // Update the last fetch time
            lastFetchTime = currentTime;

            // Fetch all parking items from the database
            getAllParkings(sortOption, new OnParkingsDataReceivedListener() {
                @Override
                public void onDataReceived(List<Parking> parkings) {
                    // Now that parkingList is populated, perform the search
                    performSearch(query, listener, sortOption, searchResults);
                }

                @Override
                public void onError(String errorMessage) {
                    listener.onError(errorMessage);
                }
            });
        } else {
            // Parking data is already available, perform the search
            performSearch(query, listener, sortOption, searchResults);
        }
    }
*/
    /**
     * Search for parkings and return them sorted either ny distance ot time
     * @param query
     * @param listener
     * @param sortOption -> sort the returned search elements by distance or time
     * @param searchResults
     */
    private void performSearch(String query, final OnParkingsDataReceivedListener listener, final LocationSortOption sortOption, final List<Parking> searchResults) {
        for (Parking parking : parkingList) {
            // Check if the query matches the placeName or address (case-insensitive)
            if (parking.getPlaceName().toLowerCase().contains(query.toLowerCase()) ||
                    parking.getAddress().toLowerCase().contains(query.toLowerCase())) {
                searchResults.add(parking);
            }
        }

        // Sort the search results based on the selected sort option
        sortParking(searchResults, sortOption);

        // Pass the sorted search results to the listener
        listener.onDataReceived(searchResults);
    }

    // Define an enum to specify the sorting option
    public enum LocationSortOption {
        DISTANCE,
        TIME
    }

    // Define interfaces for data callbacks
    public interface OnParkingDataReceivedListener {
        void onDataReceived(Parking parking);

        void onError(String errorMessage);
    }

    public interface OnParkingsDataReceivedListener {
        void onDataReceived(List<Parking> parkings);

        void onError(String errorMessage);
    }

    public void sortParking(List<Parking> parkings, LocationSortOption locationSortOption){
        Collections.sort(parkings, new Comparator<Parking>() {
            @Override
            public int compare(Parking parking1, Parking parking2) {
                if (locationSortOption == LocationSortOption.DISTANCE) {
                    // Parse the distance strings to extract the numeric values
                    int distance1 = Integer.parseInt(parking1.getDistance().replaceAll("\\D+", ""));
                    int distance2 = Integer.parseInt(parking2.getDistance().replaceAll("\\D+", ""));

                    Log.d(".FirebaseHelper", "\n\nFirebaseHelper -> sortParking: parking1: "+parking1.getDistance()+"\ndistance1: "+distance1);
                    Log.d(".FirebaseHelper", "\n\nFirebaseHelper -> sortParking: parking2: "+parking2.getAddress()+"\ndistance2: "+distance2);

                    // Compare based on distance
                    return Integer.compare(distance1, distance2);
                } else {
                    // Parse the time strings to extract the numeric values
                    int time1 = Integer.parseInt(parking1.getTime().replaceAll("\\D+", ""));
                    int time2 = Integer.parseInt(parking2.getTime().replaceAll("\\D+", ""));

                    // Compare based on time
                    return Integer.compare(time1, time2);
                }
            }
        });

        Log.d(".FirebaseHelper", "FirebaseHelper -> sortParking: parkings: "+parkings);

//        return parkings;
    }
}

