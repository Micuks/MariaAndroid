package com.wql_2020211597.mariaandroid;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.wql_2020211597.mariaandroid.fragments.HomeFragment;
import com.wql_2020211597.mariaandroid.fragments.HistoryFragment;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Fragment currentFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bottom navigation bar
        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnItemSelectedListener(onItemSelectedListener);

        // Load the default fragment when activity is created
        currentFragment = new HomeFragment();
        loadFragment(currentFragment);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private NavigationBarView.OnItemSelectedListener onItemSelectedListener =
            new NavigationBarView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment fragment;
                    if (item.getItemId() == R.id.navigation_home && !(currentFragment instanceof HomeFragment)) {
                        fragment = new HomeFragment();
                    } else if (item.getItemId() == R.id.navigation_settings && !(currentFragment instanceof HistoryFragment)) {
                        fragment = new HistoryFragment();
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
            currentFragment = fragment;
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}