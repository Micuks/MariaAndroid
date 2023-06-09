package com.wql_2020211597.mariaandroid.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.wql_2020211597.mariaandroid.MainActivity;
import com.wql_2020211597.mariaandroid.R;
import com.wql_2020211597.mariaandroid.config.Config;
import com.wql_2020211597.mariaandroid.history.HistoryStorage;
import com.wql_2020211597.mariaandroid.models.HistoryEntry;
import com.wql_2020211597.mariaandroid.services.SearchService;
import com.wql_2020211597.mariaandroid.viewmodels.HomeViewModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HistoryFragment extends Fragment implements OnEntryClickListener {
    private static int default_page = 1;
    private SearchService service;
    private static final String TAG = "HistoryFragment";
    private HomeViewModel homeViewModel;
    private HistoryStorage historyStorage;
    private HistoryAdapter adapter;
    private RecyclerView rvHistory;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeViewModel = new ViewModelProvider(requireActivity()).get(
                HomeViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container,
                false);

        Toolbar toolbar = view.findViewById(R.id.historyToolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        // Hide the back button
        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity())
                    .getSupportActionBar()
                    .setDisplayHomeAsUpEnabled(false);
            ((AppCompatActivity) getActivity())
                    .getSupportActionBar()
                    .setDisplayShowHomeEnabled(false);
            ((AppCompatActivity) getActivity())
                    .getSupportActionBar()
                    .setTitle("Search History");
        }

        // Initialize HistoryStorage
        historyStorage = HistoryStorage.getInstance(getContext());
        ArrayList<HistoryEntry> history = historyStorage.loadHistory();

        // Initialize adapter with this Fragment as the click listener
        adapter = new HistoryAdapter(history, this);

        // Initialize the RecycleView
        rvHistory = view.findViewById(R.id.rvHistory);
        rvHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        rvHistory.setAdapter(adapter);

        // Initialize Search service
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.getBackendUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(SearchService.class);

        return view;
    }

    @Override
    public void onEntryClick(String query, int page) {
        // Update the historyQuery in the ViewModel
        homeViewModel.getHistoryQuery().setValue(query);
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            BottomNavigationView navigationView = mainActivity.findViewById(
                    R.id.bottom_navigation);
            navigationView.setSelectedItemId(R.id.navigation_home);
        }
    }

    private class HistoryAdapter extends RecyclerView.Adapter<HistoryViewHolder> {
        private List<HistoryEntry> history;
        private OnEntryClickListener onEntryClickListener;

        HistoryAdapter(List<HistoryEntry> history,
                       OnEntryClickListener listener) {
            this.history = history;
            this.onEntryClickListener = listener;

            Log.d(TAG, String.format(
                    "HistoryAdapter initialized with %d " + "history entries",
                    history.size()));
        }

        @NonNull
        @Override
        public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent
                , int viewType) {
            View view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.item_history, parent, false);
            return new HistoryViewHolder(view, onEntryClickListener);
        }

        @Override
        public void onBindViewHolder(@NonNull HistoryViewHolder holder,
                                     int position) {
            Log.d(TAG, String.format("onBindViewHolder called, position[%d]",
                    position));
            holder.bind(history.get(position));
        }

        @Override
        public int getItemCount() {
            return history.size();
        }
    }

    private class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuery;
        TextView tvTimeStamp;
        CardView cardView;
        OnEntryClickListener listener;

        HistoryViewHolder(@NonNull View itemView,
                          OnEntryClickListener listener) {
            super(itemView);
            cardView = itemView.findViewById(R.id.history_card_view);
            tvQuery = itemView.findViewById(R.id.tvQuery);
            tvTimeStamp = itemView.findViewById(R.id.tvTimeStamp);
            this.listener = listener;
        }

        void bind(HistoryEntry entry) {
            Log.d(TAG, String.format("Binding query[%s] to ViewHolder",
                    entry.getQuery()));
            tvQuery.setText(entry.getQuery());
            tvTimeStamp.setText(entry.getTimestamp().toString());

            // Alternating background colors
            if (getAdapterPosition() % 2 == 0) {
                itemView.setBackgroundColor(Color.parseColor("#F8F8F8"));//
                // Light gray
            } else {
                itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));//
                // White
            }

            cardView.setOnClickListener(v -> {
                // Perform a search with click, default page 1
                listener.onEntryClick(entry.getQuery(), default_page);
            });
        }
    }
}
