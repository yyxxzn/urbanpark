package com.example.googlemapsandplaces;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import com.example.googlemapsandplaces.profile.ProfileFragment;
import com.example.googlemapsandplaces.databinding.ActivityMainBinding;
import com.example.googlemapsandplaces.firebasedb.FirebaseHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.ArrayList;
import java.util.List;

/**
 * Google Doc: https://developers.google.com/maps/documentation/android-sdk/start#api-key
 * Youtoube: https://www.youtube.com/watch?v=Vt6H9TOmsuo&list=PLgCYzUzKIBE-vInwQhGSdnbyJ62nixHCt&index=4&ab_channel=CodingWithMitch
 */
public class MainActivity extends AppCompatActivity{

    private static final String TAG = "MainActivity";
    private static  final int ERROR_DIALOG_REQUEST = 9001;

    ActivityMainBinding activityMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());

        // This sets the thread policy. It's better to be at the root
        setThreadPolicy();

        if (isServicesOk()){

            // Comment this once the parkings are added to the database.
            // createParkingInit();

            // Check if an extra specifying the fragment is present
            if (getIntent().hasExtra("fragment")) {
                String fragmentTag = getIntent().getStringExtra("fragment");
                switchFragment(fragmentTag);
            } else {
                // Default fragment when no extra is provided
                replaceFragment(new MapFragment());
            }

            init();
        }

    }

    private void init(){
        activityMainBinding.bottomNav.setOnItemSelectedListener(item -> {

            if (item.getItemId() == R.id.bottom_nav_home) {
                replaceFragment(new MapFragment());
            } else if (item.getItemId() == R.id.bottom_nav_search) {
                replaceFragment(new SearchFragment());
            } else if (item.getItemId() == R.id.bottom_nav_booking) {
                replaceFragment(new BookingFragment());
            } else if (item.getItemId() == R.id.bottom_nav_profile) {
                replaceFragment(new ProfileFragment());
            }
            return true;
        });
    }

    /**
     * Every Android device has a Google Play Servives built into it.
     * So, to use the map, they need to have a certain version or higher.
     * This method checks that
     * @return
     */
    public boolean isServicesOk() {
        Log.d(TAG, "isServicesOk(): checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if (available == ConnectionResult.SUCCESS){
            //Everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOk(): Google Services is working");
            return true;
        } else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Log.d(TAG, "isServicesOk(): A resolvable error has occurred. Let's resolve");

            // Google will provide us a dialog, where we can find the error.
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout,fragment)
                .commit();
    }

    // Method to replace the fragment based on the tag received from SuccessActivity
    private void switchFragment(String fragmentTag) {
        if (fragmentTag.equals("map")) {
            replaceFragment(new MapFragment());
        } else if (fragmentTag.equals("search")) {
            replaceFragment(new SearchFragment());
        } else if (fragmentTag.equals("booking")) {
            replaceFragment(new BookingFragment());
        }
    }

    // This is so bec of the distance and time api calls. But when use threads, you can safely comment it
    public void setThreadPolicy(){
        if (android.os.Build.VERSION.SDK_INT > 9){
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    public void createParkingInit(){
        List<Parking> parkings = new ArrayList<>();
        Parking p1, p2, p3, p4;

        p1 = new Parking(41.889810, 12.473360, "€2.04/hr", "Parking SantAgata Roma Centro", "Via Panisperna, 261, 00184 Roma RM", "Total: 100", "Rem: 8");
        p2 = new Parking(41.893830, 12.514420, "€1.50/hr", "Via di Porta Labicana, 46 Parking", "Via di Porta Labicana, 46, 00185 Roma RM", "Total: 10", "Rem: 4");
        p3 = new Parking(41.891350, 12.515730, "€5.10/hr", "Piazzale Labicano Parking", "Piazzale Labicano, 00182 Roma RM", "Total: 20", "Rem: 20");
        p4 = new Parking(41.897850, 12.518360, "€8.90/hr", "Autoparking S. Lorenzo", "Via dei Piceni, 15, 00185 Roma RM", "Total: 10", "Rem: 3");

        FirebaseHelper firebaseHelper = new FirebaseHelper();

        // Create a new parking item
        firebaseHelper.createParking(p1);
        firebaseHelper.createParking(p2);
        firebaseHelper.createParking(p3);
        firebaseHelper.createParking(p4);
    }
}