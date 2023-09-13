package com.example.googlemapsandplaces.profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.viewmodel.CreationExtras;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.googlemapsandplaces.ManageParking.AddgaraggeFragment;
import com.example.googlemapsandplaces.R;


public class ProfileFragment extends Fragment implements View.OnClickListener {

    // ...

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        // Add garage button
        Button button7 = view.findViewById(R.id.button7);
        // change the name and username button
        Button button4 = view.findViewById(R.id.button4);
        // About us button
        Button button5 = view.findViewById(R.id.button5);
        // Contact us button
        Button button6 = view.findViewById(R.id.button6);


        // Set the click listener for all buttons
        button7.setOnClickListener(this);
        button4.setOnClickListener(this);
        button5.setOnClickListener(this);
        button6.setOnClickListener(this);


        return view;
    }

    @NonNull
    @Override
    public CreationExtras getDefaultViewModelCreationExtras() {
        return super.getDefaultViewModelCreationExtras();
    }

    @Override
    public void onClick(View v) {
       // AboutusFragment
        if (v.getId() == R.id.button5) {
            replaceFragment(new AboutusFragment());
        }
        // ContactusFragment
        if (v.getId() == R.id.button6) {
            replaceFragment(new ContactusFragment());
        }
        // ChangeinfoFragment
        if (v.getId() == R.id.button4) {
            replaceFragment(new ChangeinfoFragment());
        }

        // AddgaraggeFragment
        if (v.getId() == R.id.button7) {
            replaceFragment(new Managegaraggefragment());
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
        transaction.replace(R.id.profilelayout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
