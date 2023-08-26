package com.example.googlemapsandplaces;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class MapUtils {

    // Constants
    private static final String TAG = "MapUtils";
    private static final String MAP_TITLE = "My Location";

    // To zoom the camera to the blue dot, to the user's location. Can be changed
    private static final float DEFAULT_ZOOM = 15f;
    private static final int MAX_SEARCH_RESULTS = 20;
    private static final boolean moveCam = true;

    private List<Marker> displayedMarkers = new ArrayList<>();
    private Marker deviceCurrentMarker;
    private Context context;
    private Activity activity;
    private GoogleMap mMap;
    private GeneralUtils generalUtils;
    private Handler suggestionHandler = new Handler(Looper.getMainLooper());
    private Runnable suggestionRunnable;
    private ThreadManagerViewModel viewModel;

    public MapUtils(Context context, Activity activity, GoogleMap mMap, GeneralUtils generalUtils) {
        this.context = context;
        this.activity = activity;
        this.mMap = mMap;
        this.generalUtils = generalUtils;
    }

    /**
     * This initializes the map with the data available in the database on map startup.
     * @param parkingList
     */
    @SuppressLint("MissingPermission")
    public void initializeMapWithMarkers(List<Parking> parkingList) {
        clearDisplayedMarkers();
        for (Parking p : parkingList) {
            LatLng latLng = new LatLng(p.getLat(), p.getLng());
            addMarkerOnMaps(latLng, p.getPlaceName());
        }
        getDeviceLocation(moveCam);

        // This will mark a little blue dot on the user's current location
        mMap.setMyLocationEnabled(true);

        // This takes you to the blue dot -- device location.
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        //TODO: play with these Ui settings to see their effects.
    }

    /**
     * Add the position on the map marked with a pin
     * @param latLng
     * @param title
     */
    public Marker addMarkerOnMaps(LatLng latLng, String title){
        Log.d(TAG, "addMarkerOnMaps(LatLng "+latLng+", String "+title+"): Mark the location with a pin");
        MarkerOptions options = new MarkerOptions().position(latLng).title(title);
        Marker marker = mMap.addMarker(options);
        displayedMarkers.add(marker);
        return marker;
    }

    /**
     * A helper method to move the camera to user's current location
     *
     * @param latLng
     * @param zoom
     */
    public void moveCamera(LatLng latLng, float zoom) {
        Log.d(TAG, "moveCamera:\n\t moving camera to: \n\t\tlat:" + latLng.latitude + ", \n\t\tlong:" + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        //addMarkerOnMaps(latLng, title);
    }

    /**
     * Used when enter key is pressed or search button is clicked
     * @param searchStr
     */
    public void handleSearch(String searchStr) {
        clearDisplayedMarkers();
        getDeviceLocation(!moveCam);
        if (searchStr.isEmpty()) {
            initializeMapWithMarkers(generalUtils.prePopulateParking());
        } else {
            geoLocateWithBackend(searchStr);
        }
    }

    private void clearDisplayedMarkers() {
        for (Marker marker : displayedMarkers) {
            marker.remove();
        }
        displayedMarkers.clear();
        deviceCurrentMarker = null;
    }

    /**
     * Gets the devices current location
     * Moves the camera there (something like zoom into the current device location)
     * Mark the location with a red pin-like.
     */
    public void getDeviceLocation(Boolean moveCam) {
        Log.d(TAG, "getDeviceLocation(): getting the device's current location");

        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.
                getFusedLocationProviderClient(this.activity);

        try {
            Task location = fusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "onComplete: found location");
                        Location currentLocation = (Location) task.getResult();
                        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

                        // Update the map with the new device location
                        if (moveCam) {
                            moveCamera(latLng, DEFAULT_ZOOM);
                        }

                        // Check if a marker already exists, and update its position
                        if (deviceCurrentMarker == null) {
                            // Add a new marker if it doesn't exist
                            deviceCurrentMarker = addMarkerOnMaps(latLng, MAP_TITLE);
                        } else {
                            // Update the marker position
                            deviceCurrentMarker.setPosition(latLng);
                        }

                    } else {
                        Log.d(TAG, "onComplete: current location is null");
                        Toast.makeText(context, "Unable to get current location", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    public void geoLocateWithBackend(String newText) {
        if (suggestionRunnable != null) {
            suggestionHandler.removeCallbacks(suggestionRunnable);
        }

        suggestionRunnable = new Runnable() {
            @Override
            public void run() {
                ExecutorService executorService = generalUtils.viewModel.getExecutorService();

                executorService.submit(() -> {
                    final List<Parking> parkings = generalUtils.fetchSuggestionsFromBackend(newText);

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!parkings.isEmpty()) {
                                for (int i=0; i < parkings.size(); i++) {
                                    Parking parking = parkings.get(i);
                                    LatLng latLng = new LatLng(parking.getLat(), parking.getLng());
                                    addMarkerOnMaps(latLng, parking.getPlaceName());

                                    if (i == 0) {
                                        // Move the camera to only the first item
                                        moveCamera(latLng, DEFAULT_ZOOM);
                                    }
                                }
                            }
                        }
                    });
                });
            }
        };

        suggestionHandler.postDelayed(suggestionRunnable, generalUtils.SUGGESTION_DELAY_MILLIS);
    }

    public void geoLocate(String searchStr) {
        Geocoder geocoder = new Geocoder(this.context);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchStr, MAX_SEARCH_RESULTS);
        } catch (IOException e) {
            Log.e(TAG, "geoLocate: IOException: \n\t\t" + e.getMessage());
        }

        if (!list.isEmpty()) {
            for (int i=0; i < list.size(); i++) {
                Address address = list.get(i);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                addMarkerOnMaps(latLng, address.getAddressLine(0));

                if (i == 0) {
                    // Move the camera to only the first item and mark it
                    moveCamera(latLng, DEFAULT_ZOOM);
                    //you can also use address.getLocality() if not null
                }
            }
        }
    }
}
