package com.wql_2020211597.mariaandroid;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.wql_2020211597.mariaandroid.fragments.HomeFragment;
import com.wql_2020211597.mariaandroid.fragments.HistoryFragment;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Fragment currentFragment;
    private static final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bottom navigation bar
        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnItemSelectedListener(onItemSelectedListener);

        // Load the default fragment when activity is created
        currentFragment = new HomeFragment();
        loadFragment(currentFragment, "HomeFragment");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions,
                grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Permission granted");
            } else {
                Log.e(TAG, "Permission denied");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private NavigationBarView.OnItemSelectedListener onItemSelectedListener =
            this::menuLoadFragmentController;

    public boolean menuLoadFragmentController(@NonNull MenuItem item) {
        return loadFragmentController(item.getItemId());
    }

    public boolean loadFragmentController(int itemId) {
        Fragment fragment;
        FragmentManager fragmentManager = getSupportFragmentManager();
        String homeFragmentTag = "HomeFragment";
        String historyFragmentTag = "HistoryFragment";
        String currTag;

        if (itemId == R.id.navigation_home) {
            currTag = homeFragmentTag;
            fragment = fragmentManager.findFragmentByTag(currTag);
            if (!(fragment instanceof HomeFragment)) {
                fragment = new HomeFragment();
            }
        } else if (itemId == R.id.navigation_history) {
            currTag = historyFragmentTag;
            fragment = fragmentManager.findFragmentByTag(currTag);
            if (!(fragment instanceof HistoryFragment)) {
                fragment = new HistoryFragment();
            }
        } else {
            return false;
        }

        return loadFragment(fragment, currTag);
    }

    private boolean loadFragment(Fragment fragment, String tag) {
        // Switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment, tag)
                    .addToBackStack(null)
                    .commit();
            currentFragment = fragment;
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (menuLoadFragmentController(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}