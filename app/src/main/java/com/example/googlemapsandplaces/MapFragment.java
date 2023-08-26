package com.example.googlemapsandplaces;

import android.os.Bundle;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.googlemapsandplaces.databinding.ActivityMainBinding;
import com.example.googlemapsandplaces.databinding.FragmentMapBinding;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import android.Manifest;


import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.OnMapReadyCallback;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "MapFragment";
    private Boolean locationPermissionGranted = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private AutoCompleteTextView searchAutoComplete;
    private GoogleMap mMap;
    private ThreadManagerViewModel viewModel;
    private MapUtils mapUtils;
    private List<Parking> parkingList;
    private GeneralUtils generalUtils;
    private FragmentMapBinding fragmentMapBinding;

    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentMapBinding = FragmentMapBinding.inflate(inflater, container, false);
        return fragmentMapBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(ThreadManagerViewModel.class);

        parkingList = GeneralUtils.prePopulateParking();
        searchAutoComplete = fragmentMapBinding.autoCompleteSearch;

        getLocationPermission();

        // Go ahead only when the device's location permission is granted by the user
        if (locationPermissionGranted) {
            initMap();
            initMapComponents();
        }
    }

    private void initMap() {

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFrag);
        mapFragment.getMapAsync(MapFragment.this);
    }

    private void initMapComponents() {

        generalUtils = new GeneralUtils(requireContext(), requireActivity(), viewModel);

        // Initialize AutoCompleteTextView with parking names
        List<String> parkingNames = new ArrayList<>();
        for (Parking parking : parkingList) {
            parkingNames.add(parking.getPlaceName());
        }
        ArrayAdapter<String> autoCompleteAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, parkingNames);
        searchAutoComplete.setAdapter(autoCompleteAdapter);

        // When "enter" is pressed in the search bar, it doesn't go to a new line
        // instead it executes an action (same as searching)
        searchAutoComplete.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH
                    || i == EditorInfo.IME_ACTION_DONE
                    || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                    || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {

                String searchStr = searchAutoComplete.getText().toString();
                mapUtils.handleSearch(searchStr); // Note that this line is different from the one in SearchActivity

                // Close the keyboard and auto suggestions dropdown
                generalUtils.closeKeyboard(searchAutoComplete);
                searchAutoComplete.dismissDropDown();
            }
            return false;
        });

        /**
         * When an Auto Suggestion Item is clicked. Different from SearchActivity
         */
        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedParkingName = autoCompleteAdapter.getItem(position);
                if (selectedParkingName != null) {
                    mapUtils.handleSearch(selectedParkingName); //Note: this line is different from the one in SearchActivity
                    // Note: this is inefficient sending a request to the database,
                    // when we can get the item from the auto suggestion array adapter
                    // But how? TODO: modify this
                }
                generalUtils.closeKeyboard(searchAutoComplete);
            }
        });
    }


    /**
     * This actually prepares and makes our map ready.
     * Note: make sure to ask for location permissions.
     *
     * @param googleMap
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mapUtils = new MapUtils(getContext(), getActivity(), mMap, generalUtils);
        mapUtils.initializeMapWithMarkers(generalUtils.prePopulateParking());
    }


    /**
     * A helper function to explicitly request for the locations' permissions.
     * If granted, locationPermissionGranted = true; else false.
     */
    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                ||
                ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
            Log.d(TAG, "getLocationPermission: \n\t permission check shows that permissions are granted");
        } else {
            // Requests permissions from onRequestPermissionsResult() method below
            Log.d(TAG, "getLocationPermission: \n\tPermissions not granted. " +
                    "Request permissions from onRequestPermissionsResult() method");
            ActivityCompat.requestPermissions(getActivity(), permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");

        locationPermissionGranted = false;

        // if the app didn't ask for permissions, then ask now
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            Log.d(TAG, "onRequestPermissionsResult: " +
                    "\n\trequestCode != LOCATION_PERMISSION_REQUEST_CODE, request permission from super");
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        //Check that all requested permissions are granted.
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = false;
                Log.d(TAG, "onRequestPermissionsResult: at least one of the requested permissions not granted.");
                return;
            }
        }
        Log.d(TAG, "onRequestPermissionsResult: permission granted.");
        locationPermissionGranted = true;
    }

}