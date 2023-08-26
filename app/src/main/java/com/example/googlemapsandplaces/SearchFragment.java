package com.example.googlemapsandplaces;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.googlemapsandplaces.databinding.FragmentSearchBinding;
import com.example.googlemapsandplaces.gpay.GPayActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    private AutoCompleteTextView searchAutoComplete;
    private RecyclerView suggestionsRecyclerView;
    private SuggestionsRecyclerAdapter suggestionsAdapter;
    private ThreadManagerViewModel viewModel;
    private List<Parking> parkingList;
    private GeneralUtils generalUtils;
    private FragmentSearchBinding binding;

    /*// TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SearchFragment() {
        // Required empty public constructor
    }


     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.

    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ThreadManagerViewModel.class);

        parkingList = generalUtils.prePopulateParking();

        searchAutoComplete = binding.autoCompleteSearch;
        suggestionsRecyclerView = binding.searchRecyclerView;
        suggestionsAdapter = new SuggestionsRecyclerAdapter(getContext(), parkingList);

        suggestionsRecyclerView.setAdapter(suggestionsAdapter);
        suggestionsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        generalUtils = new GeneralUtils(getContext(), getActivity(), viewModel);
        //generalUtils.setSuggestionsAdapterAndRecyclerView(suggestionsAdapter, suggestionsRecyclerView);

        // Initialize AutoCompleteTextView with parking names
        List<String> parkingNames = new ArrayList<>();
        for (Parking parking : parkingList) {
            parkingNames.add(parking.getPlaceName());
        }
        ArrayAdapter<String> autoCompleteAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, parkingNames);
        searchAutoComplete.setAdapter(autoCompleteAdapter);

        // When "enter" is pressed in the search bar, it doesn't go to a new line
        // instead it executes an action (same as searching)
        searchAutoComplete.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH
                    || i == EditorInfo.IME_ACTION_DONE
                    || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                    || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {

                String searchStr = searchAutoComplete.getText().toString();
                generalUtils.onSearchTextChanged(searchStr, suggestionsAdapter, suggestionsRecyclerView);

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
                String selectedParkingName = autoCompleteAdapter.getItem(position);
                generalUtils.onSearchTextChanged(selectedParkingName, suggestionsAdapter, suggestionsRecyclerView);
//                if (selectedParkingName != null) {
//                    generalUtils.onSearchTextChanged(selectedParkingName);
//                    // Note: this is inefficient sending a request to the database,
//                    // when we can get the item from the auto suggestion array adapter
//                    // But how? TODO: modify this
//                }
            }
        });

        suggestionsAdapter.setOnSuggestionClickListener(new SuggestionsRecyclerAdapter.OnSuggestionClickListener() {
            @Override
            public void onSuggestionClick(Parking suggestion) {
                searchAutoComplete.setText(suggestion.getAddress());
                List<Parking> fetchedSuggestions = new ArrayList<>();
                fetchedSuggestions.add(suggestion);
                suggestionsAdapter.updateSuggestions(fetchedSuggestions);

                Intent intent = new Intent(getContext(), GPayActivity.class);
                intent.putExtra("parkingObject", suggestion);
                startActivity(intent);
            }
        });
    }
}