package com.example.googlemapsandplaces;

import static com.example.googlemapsandplaces.GeneralUtils.EMAIL;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.googlemapsandplaces.databinding.FragmentBookingBinding;
import com.example.googlemapsandplaces.databinding.FragmentSearchBinding;
import com.example.googlemapsandplaces.firebasedb.FirebaseHelper;
import com.example.googlemapsandplaces.gpay.GPayActivity;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookingFragment extends Fragment {

    private static final String TAG = ".BookingFragment";

    private AutoCompleteTextView searchAutoComplete;
    private RecyclerView suggestionsRecyclerView;
    private SuggestionsRecyclerAdapter suggestionsAdapter;
    private ThreadManagerViewModel viewModel;

    private List<Booking> bookingList = new ArrayList<>();
    private List<Parking> parkingList = new ArrayList<>();
    private List<Parking> parkingListAlternate = new ArrayList<>();

    // Create an instance of FirebaseHelper
    private FirebaseHelper firebaseHelper = new FirebaseHelper();
    private FirebaseHelper fbHelper = new FirebaseHelper();

    FirebaseDatabase firebaseDatabase;
    DatabaseReference parkingRef;
    DatabaseReference bookingDbRef = FirebaseDatabase.getInstance().getReference("booking");
    Query query = bookingDbRef.orderByChild("email").equalTo(EMAIL);

    private GeneralUtils generalUtils;
    private FragmentBookingBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentBookingBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initComponents();

        /*query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                parkingList.clear();
                parkingListAlternate.clear();

                FirebaseHelper.LocationSortOption sortOption = FirebaseHelper.LocationSortOption.DISTANCE;
                List<Parking> parkingObjects = new ArrayList<>(); // Create a list to store parking objects

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    // Access each history record and retrieve parkingID
                    String parkingID = snapshot.child("parkingID").getValue(String.class);

                    Log.d(TAG, "parkingID: "+parkingID);

                    // Retrieve parking information based on parkingID and add them to the parkinglist
                    retrieveParkingInfo(parkingID, sortOption, parkingObjects);
                }

                initComponents();

                suggestionsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                String msg = "\n\nBookingFragment getAllBookings onError(): " + databaseError.toString() + "\n\n";
                Log.e(TAG, msg);
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });*/
    }

    private void retrieveParkingInfo(String parkingID, FirebaseHelper.LocationSortOption sortOption, List<Parking> parkingObjects) {
        DatabaseReference parkingRef = FirebaseDatabase.getInstance().getReference("parking").child(parkingID);
        parkingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot parkingSnapshot) {

                Parking parking = parkingSnapshot.getValue(Parking.class);
                parkingObjects.add(parking);

                parkingList.add(parking);
                parkingListAlternate.add(parking);

                Log.d(TAG, "parkingList retrieveParkingInfo onDataChange: "+parkingList);

                suggestionsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors if any
            }
        });
    }

    public void initComponents(){

        viewModel = new ViewModelProvider(this).get(ThreadManagerViewModel.class);

        if (getContext() == null || getActivity() == null) {
            throw new RuntimeException("Booking: getContext() == null || getActivity() == null \ngetContext(): "+getContext()+"\ngetActivity(): "+getActivity());
        }

        searchAutoComplete = binding.autoCompleteSearch;
        suggestionsRecyclerView = binding.searchRecyclerView;
        suggestionsAdapter = new SuggestionsRecyclerAdapter(getContext(), getActivity(),  parkingList, "booking");

        suggestionsRecyclerView.setAdapter(suggestionsAdapter);
        suggestionsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        generalUtils = new GeneralUtils(getContext(), getActivity(), viewModel);

        // Initialize AutoCompleteTextView with parking names
        List<String> parkingNames = new ArrayList<>();
        for (Parking parking : parkingList) {
            parkingNames.add(parking.getPlaceName());
        }
        ArrayAdapter<String> autoCompleteAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, parkingNames);
        searchAutoComplete.setAdapter(autoCompleteAdapter);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                parkingList.clear();
                parkingListAlternate.clear();

                FirebaseHelper.LocationSortOption sortOption = FirebaseHelper.LocationSortOption.DISTANCE;
                List<Parking> parkingObjects = new ArrayList<>(); // Create a list to store parking objects

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    // Access each history record and retrieve parkingID
                    String parkingID = snapshot.child("parkingID").getValue(String.class);

                    Log.d(TAG, "parkingID: "+parkingID);

                    // Retrieve parking information based on parkingID and add them to the parkinglist
                    retrieveParkingInfo(parkingID, sortOption, parkingObjects);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                String msg = "\n\nBookingFragment getAllBookings onError(): " + databaseError.toString() + "\n\n";
                Log.e(TAG, msg);
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });

        // When "enter" is pressed in the search bar, it doesn't go to a new line
        // instead it executes an action (same as searching)
        searchAutoComplete.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH
                    || i == EditorInfo.IME_ACTION_DONE
                    || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                    || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {

                String searchStr = searchAutoComplete.getText().toString();
                FirebaseHelper.LocationSortOption sortOption = FirebaseHelper.LocationSortOption.DISTANCE;
                parkingList.clear();

                for (Parking parking: parkingListAlternate) {
                    if (parking != null &&
                            (parking.getPlaceName().toLowerCase().contains(searchStr.toLowerCase()) ||
                                    parking.getAddress().toLowerCase().contains(searchStr.toLowerCase()))
                    ) {
                        parkingList.add(parking);
                    }
                }

                firebaseHelper.sortParking(parkingList, sortOption);

                suggestionsAdapter.notifyDataSetChanged();

                // Close the keyboard and auto suggestions dropdown
                generalUtils.closeKeyboard(searchAutoComplete);
                searchAutoComplete.dismissDropDown();
            }
            return false;
        });

        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                generalUtils.closeKeyboard(searchAutoComplete);
                String searchStr = autoCompleteAdapter.getItem(position);
                FirebaseHelper.LocationSortOption sortOption = FirebaseHelper.LocationSortOption.DISTANCE;
                parkingList.clear();

                for (Parking parking: parkingListAlternate) {
                    if (parking != null &&
                            (parking.getPlaceName().toLowerCase().contains(searchStr.toLowerCase()) ||
                                    parking.getAddress().toLowerCase().contains(searchStr.toLowerCase()))
                    ) {
                        parkingList.add(parking);
                    }
                }

                firebaseHelper.sortParking(parkingList, sortOption);

                suggestionsAdapter.notifyDataSetChanged();

                // Close the keyboard and auto suggestions dropdown
                generalUtils.closeKeyboard(searchAutoComplete);
                searchAutoComplete.dismissDropDown();
            }
        });

        suggestionsAdapter.setOnSuggestionClickListener(new SuggestionsRecyclerAdapter.OnSuggestionClickListener() {
            @Override
            public void onSuggestionClick(Parking suggestion) {
                searchAutoComplete.setText(suggestion.getAddress());

                parkingList.clear();
                parkingList.add(suggestion);
                suggestionsAdapter.notifyDataSetChanged();

                Intent intent = new Intent(getContext(), GPayActivity.class);
                intent.putExtra("parkingObject", suggestion);
                startActivity(intent);
            }
        });
    }

}