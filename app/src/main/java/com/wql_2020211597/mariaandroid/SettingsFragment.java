package com.wql_2020211597.mariaandroid;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.wql_2020211597.mariaandroid.history.HistoryStorage;
import com.wql_2020211597.mariaandroid.models.HistoryEntry;

import java.util.ArrayList;

public class SettingsFragment extends Fragment {
    private static final String TAG = "SettingsFragment";
    private HistoryStorage historyStorage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container,
                false);

        // History list
        ListView historyListView = view.findViewById(R.id.historyListView);
        historyStorage = HistoryStorage.getInstance(getContext());
        ArrayList<HistoryEntry> history = historyStorage.loadHistory();

        ArrayAdapter<HistoryEntry> adapter = new ArrayAdapter<HistoryEntry>(
                getContext(),
                android.R.layout.simple_list_item_1, history);

        historyListView.setAdapter(adapter);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
        return view;
    }
}
