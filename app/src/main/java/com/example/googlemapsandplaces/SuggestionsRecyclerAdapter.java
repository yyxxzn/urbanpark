package com.example.googlemapsandplaces;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SuggestionsRecyclerAdapter extends RecyclerView.Adapter<SuggestionsRecyclerAdapter.SuggestionViewHolder> {

    final static private String TAG = "SuggestionsRecyclerAdapter";
    private List<Parking> suggestionsList;
    private Context context;
    private OnSuggestionClickListener suggestionClickListener;

    public SuggestionsRecyclerAdapter(Context context, List<Parking> suggestionsList) {
        this.context = context;
        this.suggestionsList = suggestionsList;
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
        holder.placeName.setText(suggestionsList.get(position).getPlaceName());
        holder.address.setText(suggestionsList.get(position).getAddress());
        //holder.distance.setText(suggestionsList.get(position).getDistance());
        //holder.time.setText(suggestionsList.get(position).getTime());

        Log.d(TAG, "onBindViewHolder: Bind the recycler items to their respective views");
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

        private TextView placeName, address, distance, time;

        public SuggestionViewHolder(@NonNull View itemView) {
            super(itemView);

            placeName = itemView.findViewById(R.id.placeName);
            address = itemView.findViewById(R.id.address);
            distance = itemView.findViewById(R.id.distance);
            time = itemView.findViewById(R.id.time);

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
