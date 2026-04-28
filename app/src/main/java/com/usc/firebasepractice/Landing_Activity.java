package com.usc.firebasepractice;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapView;

public class Landing_Activity extends AppCompatActivity {

    BottomNavigationView bottomNav;
    MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        // MAP SETUP
        mapView = findViewById(R.id.mapView);
        Configuration.getInstance().setUserAgentValue(getPackageName());
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(15.0);

        // BOTTOM NAV SETUP
        bottomNav = findViewById(R.id.bottomNav);

        // default fragment
        loadFragment(new CommuterHomeFragment());

        bottomNav.setOnItemSelectedListener(item -> {

            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.nav_home) {
                selectedFragment = new CommuterHomeFragment();
            } else if (item.getItemId() == R.id.nav_search) {
                selectedFragment = new CommuterSearchFragment();
            } else if (item.getItemId() == R.id.nav_setting) {
                selectedFragment = new CommuterSettingFragment();
            }

            return loadFragment(selectedFragment);
        });
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}