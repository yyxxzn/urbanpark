package com.example.googlemapsandplaces;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.googlemapsandplaces.databinding.ActivityMainBinding;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

/**
 * Google Doc: https://developers.google.com/maps/documentation/android-sdk/start#api-key
 * Youtoube: https://www.youtube.com/watch?v=Vt6H9TOmsuo&list=PLgCYzUzKIBE-vInwQhGSdnbyJ62nixHCt&index=4&ab_channel=CodingWithMitch
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static  final int ERROR_DIALOG_REQUEST = 9001;

    ActivityMainBinding activityMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());

        replaceFragment(new MapFragment());

        if (isServicesOk()){
            init();
        }

    }

    private void init(){
        activityMainBinding.bottomNav.setOnItemSelectedListener(item -> {

            if (item.getItemId() == R.id.bottom_nav_home) {
                replaceFragment(new MapFragment());
            } else if (item.getItemId() == R.id.bottom_nav_search) {
                replaceFragment(new SearchFragment());
            } else if (item.getItemId() == R.id.bottom_nav_profile) {
                replaceFragment(new ProfileFragment());
            } else if (item.getItemId() == R.id.bottom_nav_settings) {
                replaceFragment(new SettingsFragment());
            }
            return true;
        });


//        Button btnMap = (Button) findViewById(R.id.btnMap);
//        btnMap.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(MainActivity.this, MapActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        Button btnSearch = (Button) findViewById(R.id.btnSearch);
//        btnSearch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
//                startActivity(intent);
//            }
//        });

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
}