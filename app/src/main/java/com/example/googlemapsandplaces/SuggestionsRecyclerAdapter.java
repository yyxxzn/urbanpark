package com.example.googlemapsandplaces;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.googlemapsandplaces.firebasedb.FirebaseHelper;
import com.example.googlemapsandplaces.gpay.GPayActivity;
import com.example.googlemapsandplaces.gpay.SuccessActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class SuggestionsRecyclerAdapter extends RecyclerView.Adapter<SuggestionsRecyclerAdapter.SuggestionViewHolder> {

    final static private String TAG = "SuggestionsRecyclerAdapter";
    private List<Parking> suggestionsList;
    private Context context;
    private Activity activity;
    private OnSuggestionClickListener suggestionClickListener;
    private String useIt;
    private DateTime dateTime;

    private FirebaseHelper firebaseHelper;

    public SuggestionsRecyclerAdapter(Context context, Activity activity, List<Parking> suggestionsList, String useIt) {
        this.context = context;
        this.activity = activity;
        this.suggestionsList = suggestionsList;
        this.useIt = useIt;
    }

    public void updateSuggestions(List<Parking> suggestions) {
        suggestionsList.clear();
        suggestionsList.addAll(suggestions);
        notifyDataSetChanged();

        Log.d(TAG, "updateSuggestions: SuggestionRecyclerAdapter class finished");
    }

    @NonNull
    @Override
    public SuggestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(context).
                inflate(R.layout.recycler_items_viewholder_1, parent, false);
        return new SuggestionsRecyclerAdapter.SuggestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestionViewHolder holder, int position) {
        Parking parking = suggestionsList.get(position);

        holder.placeName.setText(parking.getPlaceName());
        holder.address.setText(parking.getAddress());
        holder.price.setText(parking.getPrice());
        holder.totalPlaces.setText(parking.getTotalPlaces());
        holder.remPlaces.setText(parking.getRemPlaces());

        // Call this to get the current location and the destination
        parking.getDistanceAndTime(activity, new Parking.DistanceCallback() {
            @Override
            public void onDistanceReceived(String distance, String time) {
                // Update the UI with the calculated distance and time.
                holder.distance.setText(distance);
                holder.time.setText(time);

                parking.setDistance(distance);
                parking.setTime(time);
            }
        });

        if (useIt.equals("booking")){
            holder.endBtn.setVisibility(View.VISIBLE);
            holder.extendBtn.setVisibility(View.VISIBLE);
            holder.startDateTime.setVisibility(View.VISIBLE);
            holder.endDateTime.setVisibility(View.VISIBLE);

            holder.startDateTime.setText(parking.getStartDateTime());
            holder.endDateTime.setText(parking.getEndDateTime());

            // Set click listeners for your buttons
            holder.endBtn.setOnClickListener(v -> {
                // Handle the End Parking button click here
                // For example, start a new activity:
                Intent intent = new Intent(context, SuccessActivity.class);
                // intent.putExtra("parkingId", data.getParkingId()); // Pass relevant data if needed
                context.startActivity(intent);
            });

            // Set up the click listener for the extend button
            holder.extendBtn.setOnClickListener(view -> {
                // Show the date and time picker dialog
                dateTime.showDateTimePicker((selectedDate, selectedTime) -> {
                    // Handle the selected date and time here
                    // You can update the UI or perform other actions
                    holder.endDateTime.setText("Date: " + selectedDate + "\nTime: " + selectedTime);

                    parking.setStartDateTime((String) holder.startDateTime.getText());
                    parking.setEndDateTime((String) holder.endDateTime.getText());

                    firebaseHelper.updateParking(parking);

                    Intent intent = new Intent(context, GPayActivity.class);
                    intent.putExtra("parkingObject", parking);
                    context.startActivity(intent);
                });
            });

        } else {
            holder.endBtn.setVisibility(View.GONE);
            holder.extendBtn.setVisibility(View.GONE);
            holder.startDateTime.setVisibility(View.GONE);
            holder.endDateTime.setVisibility(View.GONE);
        }

//        parking.setStartDateTime((String) holder.startDateTime.getText());
//        parking.setEndDateTime((String) holder.endDateTime.getText());
//        parking.setDistance((String) holder.distance.getText());
//        parking.setTime((String) holder.time.getText());
//        firebaseHelper.updateParking(parking);
    }

    @Override
    public int getItemCount() {
        int s = suggestionsList.size();
        Log.d(TAG, "getItemCount: The total Item size is  = "+s);
        return s;
    }

    // This interface is for responding to recycler view clicks
    public interface OnSuggestionClickListener {
        void onSuggestionClick(Parking suggestion);
    }

    public void setOnSuggestionClickListener(OnSuggestionClickListener listener) {
        this.suggestionClickListener = listener;
    }

    public class SuggestionViewHolder extends RecyclerView.ViewHolder {

        private TextView placeName, address, distance, time, price, totalPlaces, remPlaces, startDateTime, endDateTime;

        private Button endBtn, extendBtn;

        public SuggestionViewHolder(@NonNull View itemView) {
            super(itemView);

            placeName = itemView.findViewById(R.id.placeName);
            address = itemView.findViewById(R.id.address);
            price = itemView.findViewById(R.id.price);
            distance = itemView.findViewById(R.id.distance);
            time = itemView.findViewById(R.id.time);
            totalPlaces = itemView.findViewById(R.id.totalPlaces);
            remPlaces = itemView.findViewById(R.id.remPlaces);
            startDateTime = itemView.findViewById(R.id.startDateTime);
            endDateTime = itemView.findViewById(R.id.endDateTime);

            endBtn = itemView.findViewById(R.id.endParking);
            extendBtn = itemView.findViewById(R.id.extendParking);

            // Initialize dateTimeUtil here if needed
            dateTime = new DateTime(itemView.getContext());

            firebaseHelper = new FirebaseHelper();

            // Responds to recycler view clicks
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (suggestionClickListener != null) {
                        suggestionClickListener.onSuggestionClick(suggestionsList.get(getAdapterPosition()));
                    }
                    Log.d(TAG, "SuggestionViewHolder: SuggestionRecyclerAdapter class");
                }
            });
        }
    }
}
