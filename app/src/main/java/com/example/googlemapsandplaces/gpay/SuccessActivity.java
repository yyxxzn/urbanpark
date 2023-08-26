package com.example.googlemapsandplaces.gpay;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.googlemapsandplaces.databinding.ActivitySuccessBinding;

public class SuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivitySuccessBinding layoutBinding = ActivitySuccessBinding.inflate(getLayoutInflater());
        setContentView(layoutBinding.getRoot());
    }
}