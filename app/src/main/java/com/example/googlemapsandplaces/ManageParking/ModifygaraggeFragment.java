package com.example.googlemapsandplaces.ManageParking;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.googlemapsandplaces.Parking;
import com.example.googlemapsandplaces.R;
import com.example.googlemapsandplaces.firebasedb.FirebaseHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ModifygaraggeFragment extends Fragment {
    private EditText nameEditText, addressEditText, priceEditText;
    private Button addButton;
    private DatabaseReference databaseReference;

    private FirebaseHelper firebaseHelper;

    public ModifygaraggeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_changegaragge, container, false);

        nameEditText = view.findViewById(R.id.GName);
        addressEditText = view.findViewById(R.id.Gaddress);
        priceEditText = view.findViewById(R.id.Gcost);
        addButton = view.findViewById(R.id.submit);
        databaseReference = FirebaseDatabase.getInstance().getReference("parking");

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String placeName = nameEditText.getText().toString().trim();
                String address = addressEditText.getText().toString().trim();
                String price = priceEditText.getText().toString().trim();
                Double lat = 41.857095;
                Double lng = 12.563273;
                String rem = "Rem: 20";
                String total = "Total: 100";
                String time = "20min";
                Parking parking = new Parking(lat, lng, price, placeName, address, total, rem);
                String key = parking.getAddress();
                databaseReference.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // Modify the existing data
                            Parking existingParking = snapshot.getValue(Parking.class);
                            existingParking.setPlaceName(placeName);
                            existingParking.setAddress(address);
                            existingParking.setPrice(price);

                            // Update data in Firebase database
                            databaseReference.child(key).setValue(existingParking)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Data updated successfully, show a toast message
                                            Toast.makeText(getActivity(), "Data Modified successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Data update failed, show an error toast message if needed
                                            Toast.makeText(getActivity(), "Data Modification failed", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            // Data does not exist, show an error toast message if needed
                            Toast.makeText(getActivity(), "Data does not exist", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Data retrieval failed, show an error toast message if needed
                        Toast.makeText(getActivity(), "Data retrieval failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        return view;
    }
}

