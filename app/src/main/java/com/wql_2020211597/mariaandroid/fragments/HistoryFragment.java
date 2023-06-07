package com.wql_2020211597.mariaandroid.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.wql_2020211597.mariaandroid.R;
import com.wql_2020211597.mariaandroid.history.HistoryStorage;
import com.wql_2020211597.mariaandroid.models.HistoryEntry;

import java.util.ArrayList;

public class HistoryFragment extends Fragment {
    private static final String TAG = "HistoryFragment";
    private HistoryStorage historyStorage;

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


        // History list
        ListView historyListView = view.findViewById(R.id.historyListView);
        historyStorage = HistoryStorage.getInstance(getContext());
        ArrayList<HistoryEntry> history = historyStorage.loadHistory();

        ArrayAdapter<HistoryEntry> adapter = new ArrayAdapter<HistoryEntry>(
                getContext(), android.R.layout.simple_list_item_1, history);

        historyListView.setAdapter(adapter);

        return view;
    }
}
