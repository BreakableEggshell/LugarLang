package com.usc.firebasepractice;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.config.Configuration;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DriverDashboardFragment extends Fragment {

    private boolean tripActive = false;
    private boolean zoomDoneForThisLeg = false;
    private boolean goingForward = true;
    private DatabaseReference driverRef;
    private String driverId = "driver_1"; // replace later with real auth id
    private MapView map;
    private boolean tripStarted = false;
    private Button btnStartTrip;


    // OSRM API, replace with real coordinates later
    private double startLat = 10.3157;
    private double startLon = 123.8854;

    private double endLat = 10.2924;
    private double endLon = 123.9020;


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public DriverDashboardFragment() {
        // Required empty public constructor
    }

    public static DriverDashboardFragment newInstance(String param1, String param2) {
        DriverDashboardFragment fragment = new DriverDashboardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());

        FirebaseDatabase.getInstance(
                "https://lugarlangfinal-default-rtdb.asia-southeast1.firebasedatabase.app/");
        driverRef = FirebaseDatabase.getInstance()
                .getReference("drivers")
                .child(driverId);

        return inflater.inflate(R.layout.fragment_driver_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnStartTrip = view.findViewById(R.id.btnStartTrip);
        map = view.findViewById(R.id.map);

        if (map != null) {
            map.setMultiTouchControls(true);

            GeoPoint startPoint = new GeoPoint(10.3157, 123.8854);
            map.getController().setZoom(14.0);
            map.getController().setCenter(startPoint);
            map.setTileSource(TileSourceFactory.MAPNIK);

            map.post(() -> fetchRoute(startLat, startLon, endLat, endLon));
        }

        btnStartTrip.setOnClickListener(v -> handleTripClick());

        loadTripState();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (map != null) map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (map != null) map.onPause();
    }

    private void swapRoutePoints() {
        double tempLat = startLat;
        double tempLon = startLon;

        startLat = endLat;
        startLon = endLon;

        endLat = tempLat;
        endLon = tempLon;
    }

    private void handleTripClick() {

        driverRef.child("tripStatus").get().addOnSuccessListener(snapshot -> {

            String status = snapshot.getValue(String.class);

            if (!"started".equals(status)) {

                driverRef.child("tripStatus").setValue("started");

                if (!zoomDoneForThisLeg) {
                    GeoPoint startPoint = new GeoPoint(startLat, startLon);
                    map.getController().animateTo(startPoint);
                    map.getController().setZoom(17.0);

                    zoomDoneForThisLeg = true;
                }

                btnStartTrip.setText("Finish Trip");

                map.post(() -> fetchRoute(startLat, startLon, endLat, endLon));

            } else {
                driverRef.child("tripStatus").setValue("finished");

// swap direction A ↔ B
                swapRoutePoints();

                btnStartTrip.setText("Start Trip");
                tripActive = false;

                btnStartTrip.setAlpha(1f);
                btnStartTrip.setEnabled(true);

// clear old route
                map.getOverlays().clear();
                map.invalidate();

                zoomDoneForThisLeg = false;

// preload NEXT route only ONCE (correct version)
                map.post(() -> fetchRoute(startLat, startLon, endLat, endLon));
            }
        });
    }

    private void loadTripState() {
        driverRef.child("tripStatus").get().addOnSuccessListener(snapshot -> {

            String status = snapshot.getValue(String.class);

            if ("started".equals(status)) {
                btnStartTrip.setText("Finish Trip");
            } else {
                btnStartTrip.setText("Start Trip");
            }

            btnStartTrip.setAlpha(1f);
            btnStartTrip.setEnabled(true);
        });
    }

    private void drawRoute(String json) {
        try {
            if (!isAdded()) return;

            JSONObject obj = new JSONObject(json);
            JSONArray routes = obj.getJSONArray("routes");
            JSONObject route = routes.getJSONObject(0);
            JSONObject geometry = route.getJSONObject("geometry");
            JSONArray coords = geometry.getJSONArray("coordinates");

            List<GeoPoint> points = new ArrayList<>();
            for (int i = 0; i < coords.length(); i++) {
                JSONArray point = coords.getJSONArray(i);
                double lon = point.getDouble(0);
                double lat = point.getDouble(1);
                points.add(new GeoPoint(lat, lon));
            }

            requireActivity().runOnUiThread(() -> {
                if (!isAdded() || map == null) return;

                map.getOverlays().clear();

                Polyline line = new Polyline();
                line.setPoints(points);
                line.setWidth(10f);

                Marker startMarker = new Marker(map);
                startMarker.setPosition(points.get(0));
                startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                startMarker.setTitle("Start");

                Marker endMarker = new Marker(map);
                endMarker.setPosition(points.get(points.size() - 1));
                endMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                endMarker.setTitle("End");

                map.getOverlays().add(line);
                map.getOverlays().add(startMarker);
                map.getOverlays().add(endMarker);

                map.getController().setCenter(points.get(0));
                map.invalidate();
            });

        } catch (Exception e) {
            Log.e("DriverDashboard", "Error drawing route", e);
        }
    }

    private void fetchRoute(double startLat, double startLon,
                            double endLat, double endLon) {

        new Thread(() -> {
            String url = "https://router.project-osrm.org/route/v1/driving/"
                    + startLon + "," + startLat + ";"
                    + endLon + "," + endLat
                    + "?overview=full&geometries=geojson";

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String json = response.body().string();
                    Log.d("OSRM_RESPONSE", json);
                    drawRoute(json);
                }
            } catch (Exception e) {
                Log.e("DriverDashboard", "Error fetching route", e);
            }
        }).start();
    }
}