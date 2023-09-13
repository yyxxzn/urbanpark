package com.example.googlemapsandplaces.gpay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.googlemapsandplaces.DateTime;
import com.example.googlemapsandplaces.Parking;
import com.example.googlemapsandplaces.R;
import com.example.googlemapsandplaces.databinding.ActivityGpayBinding;
import com.example.googlemapsandplaces.databinding.ActivitySetupPaymentBinding;
import com.example.googlemapsandplaces.firebasedb.FirebaseHelper;
import com.google.android.gms.wallet.button.ButtonOptions;

import org.json.JSONException;

public class SetupPaymentActivity extends AppCompatActivity {

    private static final String TAG = ".SetupPaymentActivity";
    private Parking parking = new Parking();
    private DateTime dateTime;

    private FirebaseHelper firebaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_payment);

        initializeUi();
    }

    private void initializeUi() {

        dateTime = new DateTime(peekAvailableContext());
        firebaseHelper = new FirebaseHelper();

        // Use view binding to access the UI elements
        ActivitySetupPaymentBinding layoutBinding = ActivitySetupPaymentBinding.inflate(getLayoutInflater());
        setContentView(layoutBinding.getRoot());

        parking = (Parking) getIntent().getSerializableExtra("parkingObject");
        layoutBinding.parkingLayout.placeName.setText(parking.getPlaceName());
        layoutBinding.parkingLayout.address.setText(parking.getAddress());
        layoutBinding.parkingLayout.totalPlaces.setText(parking.getTotalPlaces());
        layoutBinding.parkingLayout.remPlaces.setText(parking.getRemPlaces());
        layoutBinding.parkingLayout.price.setText(parking.getPrice());

        layoutBinding.parkingStart.setVisibility(View.GONE);
        layoutBinding.parkingEnd.setVisibility(View.GONE);
        layoutBinding.payPage.setVisibility(View.GONE);

        // Call this to get the current location and the destination
        parking.getDistanceAndTime(this, new Parking.DistanceCallback() {
            @Override
            public void onDistanceReceived(String distance, String time) {
                // Update the UI with the calculated distance and time.
                layoutBinding.parkingLayout.distance.setText(distance);
                layoutBinding.parkingLayout.time.setText(time);
            }
        });

        layoutBinding.parkingLayout.distance.setText(parking.getDistance());
        layoutBinding.parkingLayout.time.setText(parking.getTime());

        layoutBinding.parkingStart.setVisibility(View.VISIBLE);
        layoutBinding.parkingStart.setOnClickListener(view -> {
            // Show the date and time picker dialog
            dateTime.showDateTimePicker((selectedDate, selectedTime) -> {
                // Handle the selected date and time here
                // You can update the UI or perform other actions
                layoutBinding.parkingLayout.startDateTime.setText("Date: " + selectedDate + "\nTime: " + selectedTime);
                parking.setStartDateTime((String) layoutBinding.parkingLayout.startDateTime.getText());
                layoutBinding.parkingEnd.setVisibility(View.VISIBLE);
            });
        });

        layoutBinding.parkingEnd.setOnClickListener(view -> {
            // Show the date and time picker dialog
            dateTime.showDateTimePicker((selectedDate, selectedTime) -> {
                // Handle the selected date and time here
                // You can update the UI or perform other actions
                layoutBinding.parkingLayout.endDateTime.setText("Date: " + selectedDate + "\nTime: " + selectedTime);
                parking.setEndDateTime((String) layoutBinding.parkingLayout.endDateTime.getText());
                layoutBinding.payPage.setVisibility(View.VISIBLE);
            });
        });

        layoutBinding.payPage.setOnClickListener(view -> {

            firebaseHelper.updateParking(parking);

            layoutBinding.parkingStart.setVisibility(View.GONE);
            layoutBinding.parkingEnd.setVisibility(View.GONE);
            layoutBinding.payPage.setVisibility(View.GONE);

            Intent intent = new Intent(this, GPayActivity.class);
            intent.putExtra("parkingObject", parking);
            startActivity(intent);
        });

    }
}