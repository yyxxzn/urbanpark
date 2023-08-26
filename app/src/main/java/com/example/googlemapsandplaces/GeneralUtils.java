package com.example.googlemapsandplaces;

import android.os.Handler;
import android.os.Looper;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class GeneralUtils {

    private static final String TAG = "GeneralUtils";
    private Context context;
    private Activity activity;
    public ThreadManagerViewModel viewModel;

    public static final long SUGGESTION_DELAY_MILLIS = 500; // Adjust the delay time as needed
    private Handler suggestionHandler = new Handler(Looper.getMainLooper());
    private Runnable suggestionRunnable;

    public GeneralUtils(Context context,
                        Activity activity,
                        ThreadManagerViewModel viewModel) {
        this.context = context;
        this.activity = activity;
        this.viewModel = viewModel;
    }

    public void onSearchTextChanged(String newText, SuggestionsRecyclerAdapter adapter, RecyclerView recyclerView) {
        if (suggestionRunnable != null) {
            suggestionHandler.removeCallbacks(suggestionRunnable);
        }

        suggestionRunnable = new Runnable() {
            @Override
            public void run() {
                ExecutorService executorService = viewModel.getExecutorService();

                executorService.submit(() -> {
                    final List<Parking> fetchedSuggestions = fetchSuggestionsFromBackend(newText);

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (adapter != null && recyclerView != null) {
                                adapter.updateSuggestions(fetchedSuggestions);
                                recyclerView.setVisibility(fetchedSuggestions.isEmpty() ? View.GONE : View.VISIBLE);

                                Log.d(TAG, "Suggestions updated!");
                            }
                        }
                    });
                });
            }
        };

        suggestionHandler.postDelayed(suggestionRunnable, SUGGESTION_DELAY_MILLIS);
    }

    public List<Parking> fetchSuggestionsFromBackend(String query) {
        List<Parking> allParkingList = prePopulateParking();
        List<Parking> suggestions = new ArrayList<>();

        for (Parking parking : allParkingList) {
            if (parking.getPlaceName().toLowerCase().contains(query.toLowerCase()) ||
                    parking.getAddress().toLowerCase().contains(query.toLowerCase())) {

                suggestions.add(parking);
            }
        }

        return suggestions;
    }

    public static List<Parking> prePopulateParking(){

        List<Parking> parkings = new ArrayList<>();
        Parking p1, p2, p3, p4;

        p1 = new Parking(41.889810, 12.473360, "", "Parking SantAgata Roma Centro", "Via Panisperna, 261, 00184 Roma RM");
        p2 = new Parking(41.893830, 12.514420, "", "Via di Porta Labicana, 46 Parking", "Via di Porta Labicana, 46, 00185 Roma RM");
        p3 = new Parking(41.891350, 12.515730, "", "Piazzale Labicano Parking", "Piazzale Labicano, 00182 Roma RM");
        p4 = new Parking(41.897850, 12.518360, "", "Autoparking S. Lorenzo", "Via dei Piceni, 15, 00185 Roma RM");

        parkings.add(p1);
        parkings.add(p2);
        parkings.add(p3);
        parkings.add(p4);

        return parkings;
    }

    public void closeKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
