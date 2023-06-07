package com.wql_2020211597.mariaandroid;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.wql_2020211597.mariaandroid.config.Config;
import com.wql_2020211597.mariaandroid.history.HistoryStorage;
import com.wql_2020211597.mariaandroid.models.Document;
import com.wql_2020211597.mariaandroid.models.HistoryEntry;
import com.wql_2020211597.mariaandroid.models.SearchResult;
import com.wql_2020211597.mariaandroid.searchservice.SearchService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private HistoryStorage historyStorage;
    private EditText etSearch;
    private Button btnSearch;
    private RecyclerView rvResults;
    private SearchResultsAdapter adapter;

    static private final String addr = Config.getBackendAddr();
    static private final String port = Config.getBackendPort();

    private String backendUrl() {
        return Config.getBackendUrl();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Load HistoryStorage
        historyStorage = HistoryStorage.getInstance(this);

        // Bottom navigation bar
        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnItemSelectedListener(onItemSelectedListener);

        // Load the default fragment when activity is created
        loadFragment(new HomeFragment());
    }

    private NavigationBarView.OnItemSelectedListener onItemSelectedListener =
            new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            if (item.getItemId() == R.id.navigation_home) {
                fragment = new HomeFragment();
            } else if (item.getItemId() == R.id.navigation_settings) {
                fragment = new SettingsFragment();
            } else {
                return false;
            }
            return loadFragment(fragment);
        }
    };


    private boolean loadFragment(Fragment fragment) {
        // Switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

}