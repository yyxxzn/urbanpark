package com.example.googlemapsandplaces.ManageParking;

import androidx.fragment.app.Fragment;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
public class DeletegaraggeFragment extends Fragment {
    private EditText nameEditText, addressEditText, priceEditText;
    private Button addButton;
    private DatabaseReference databaseReference;

    private FirebaseHelper firebaseHelper;

    public DeletegaraggeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_deletegarage, container, false);


        addressEditText = view.findViewById(R.id.Gaddress);
        addButton = view.findViewById(R.id.submit);
        databaseReference = FirebaseDatabase.getInstance().getReference("parking");

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String address = addressEditText.getText().toString().trim();

                String key = address;

                // Delete data in Firebase database
                databaseReference.child(key).removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Data stored successfully, show a toast message
                                Toast.makeText(getActivity(), "Data Deleted successfully", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Data storage failed, show an error toast message if needed
                                Toast.makeText(getActivity(), "Data Deleted failed", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        return view;
    }
}

