package com.wql_2020211597.mariaandroid.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.wql_2020211597.mariaandroid.R;
import com.wql_2020211597.mariaandroid.history.HistoryStorage;
import com.wql_2020211597.mariaandroid.models.HistoryEntry;
import com.wql_2020211597.mariaandroid.viewmodels.HomeViewModel;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {
    private static final String TAG = "HistoryFragment";
    private HomeViewModel homeViewModel;
    private HistoryStorage historyStorage;
    private HistoryAdapter adapter;
    private RecyclerView rvHistory;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container,
                false);

        Toolbar toolbar = view.findViewById(R.id.historyToolbar);
        rvHistory = view.findViewById(R.id.rvHistory);
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

        historyStorage = HistoryStorage.getInstance(getContext());

        ArrayList<HistoryEntry> history = historyStorage.loadHistory();
        HistoryAdapter adapter = new HistoryAdapter(
                history);
        rvHistory.setAdapter(adapter);

        return view;
    }

    private class HistoryAdapter extends RecyclerView.Adapter<HistoryViewHolder> {
        private List<HistoryEntry> history;

        HistoryAdapter(List<HistoryEntry> history) {
            this.history = history;
        }

        @NonNull
        @Override
        public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent
                , int viewType) {
            View view =
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
            return  new HistoryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull HistoryViewHolder holder,
                                     int position) {
            holder.bind(history.get(position));
        }

        @Override
        public int getItemCount() {
            return history.size();
        }
    }

    private class HistoryViewHolder extends RecyclerView.ViewHolder{
        TextView tvQuery;

        HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuery = itemView.findViewById(R.id.tvQuery);
        }

        void bind(HistoryEntry entry) {
            tvQuery.setText(entry.getQuery());;
        }
    }
}
