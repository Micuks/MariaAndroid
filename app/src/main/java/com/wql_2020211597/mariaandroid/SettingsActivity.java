package com.wql_2020211597.mariaandroid;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.wql_2020211597.mariaandroid.history.HistoryStorage;
import com.wql_2020211597.mariaandroid.models.HistoryEntry;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG="SettingsActivity";
    private HistoryStorage historyStorage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Bottom navigation bar
        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnItemSelectedListener(
                new NavigationBarView.OnItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Log.d(TAG, "item id: "+item.getItemId());
                        Intent intent = null;
                        if (item.getItemId() == R.id.navigation_home) {
                            intent = new Intent(getApplicationContext(),
                                    MainActivity.class);
                        } else if (item.getItemId() == R.id.navigation_settings) {
                            intent = new Intent(getApplicationContext(),
                                    SettingsActivity.class);
                        }
                        if (intent != null) {
                            intent.setFlags(
                                    Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            startActivity(intent);
                            return true;
                        }
                        return false;
                    }
                });

        // History list
        ListView historyListView = findViewById(R.id.historyListView);
        historyStorage = HistoryStorage.getInstance(this);
        ArrayList<HistoryEntry> history = historyStorage.loadHistory();

        ArrayAdapter<HistoryEntry> adapter = new ArrayAdapter<HistoryEntry>(
                this, android.R.layout.simple_list_item_1, history);
        historyListView.setAdapter(adapter);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();

        // Status bar section
        Toolbar toolbar = findViewById(R.id.settingsToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}