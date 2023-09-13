package com.example.googlemapsandplaces.gpay;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.googlemapsandplaces.BookingFragment;
import com.example.googlemapsandplaces.MainActivity;
import com.example.googlemapsandplaces.MapFragment;
import com.example.googlemapsandplaces.Parking;
import com.example.googlemapsandplaces.SearchFragment;
import com.example.googlemapsandplaces.databinding.ActivitySuccessBinding;

public class SuccessActivity extends AppCompatActivity {

    private ActivitySuccessBinding layoutBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        layoutBinding = ActivitySuccessBinding.inflate(getLayoutInflater());
        setContentView(layoutBinding.getRoot());

        init();

    }

    public void init(){

        layoutBinding.gotoMap.setOnClickListener(view -> {

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("fragment", "map");
            startActivity(intent);
        });

        layoutBinding.gotoSearch.setOnClickListener(view -> {

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("fragment", "search");
            startActivity(intent);
        });

        layoutBinding.gotoBooking.setOnClickListener(view -> {

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("fragment", "booking");
            startActivity(intent);
        });
    }
}