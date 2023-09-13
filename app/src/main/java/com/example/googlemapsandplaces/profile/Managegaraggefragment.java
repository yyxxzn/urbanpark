package com.example.googlemapsandplaces.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.viewmodel.CreationExtras;

import com.example.googlemapsandplaces.ManageParking.AddgaraggeFragment;
import com.example.googlemapsandplaces.ManageParking.DeletegaraggeFragment;
import com.example.googlemapsandplaces.ManageParking.ModifygaraggeFragment;
import com.example.googlemapsandplaces.R;

public class Managegaraggefragment extends Fragment implements View.OnClickListener{
    private RadioGroup radioGroup;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_modifygarage, container, false);

        // Add Garage
        RadioButton addParkingRadioButton = view.findViewById(R.id.addParkingRadioButton);
        // Modify Garage
        RadioButton modifyParkingRadioButton = view.findViewById(R.id.newParkingRadioButton);
        // Remove Garage
        RadioButton deleteParkingRadioButton = view.findViewById(R.id.deleteParkingRadioButton);



        // Set the click listener for all buttons
        addParkingRadioButton.setOnClickListener(this);
        modifyParkingRadioButton.setOnClickListener(this);
        deleteParkingRadioButton.setOnClickListener(this);



        return view;
    }

    @NonNull
    @Override
    public CreationExtras getDefaultViewModelCreationExtras() {
        return super.getDefaultViewModelCreationExtras();
    }

    @Override
    public void onClick(View v) {
        // AddGaragge
        if (v.getId() == R.id.addParkingRadioButton) {
            replaceFragment(new AddgaraggeFragment());
        }
        // ModifyGaragge
        if (v.getId() == R.id.newParkingRadioButton) {
            replaceFragment(new ModifygaraggeFragment());
        }
        // DeleteGaragge
        if (v.getId() == R.id.deleteParkingRadioButton) {
            replaceFragment(new DeletegaraggeFragment());
        }
/*
            // Delete account button
            if (v.getId() == R.id.button) {
                replaceFragment(new DeleteaccountFragment());
            }
*/
    }

    public void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}

