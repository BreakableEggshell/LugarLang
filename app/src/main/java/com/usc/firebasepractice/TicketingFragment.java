package com.usc.firebasepractice;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TicketingFragment extends Fragment {

    private Spinner spinnerOrig, spinnerDest, spinnerPassType;
    private Button btnSubmit;

    private DatabaseReference fareRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_ticketing, container, false);

        spinnerOrig = view.findViewById(R.id.spinnerOrig);
        spinnerDest = view.findViewById(R.id.spinnerDest);
        spinnerPassType = view.findViewById(R.id.spinnerPassType);
        btnSubmit = view.findViewById(R.id.btnSubmit);

        FirebaseDatabase database = FirebaseDatabase.getInstance(
                "https://lugarlangfinal-default-rtdb.asia-southeast1.firebasedatabase.app/"
        );

        fareRef = database.getReference("Fare");

        setupSpinners();
        setupSubmit();

        return view;
    }

    private void setupSpinners() {

        List<String> locations = new ArrayList<>();
        locations.add("Cebu North");
        locations.add("Cebu South");
        locations.add("Mandaue");
        locations.add("Lapu-Lapu");

        ArrayAdapter<String> locAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                locations
        );
        locAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerOrig.setAdapter(locAdapter);
        spinnerDest.setAdapter(locAdapter);

        List<String> types = new ArrayList<>();
        types.add("Regular");
        types.add("Student");
        types.add("PWD");
        types.add("Senior");

        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                types
        );
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerPassType.setAdapter(typeAdapter);
    }

    private void setupSubmit() {

        btnSubmit.setOnClickListener(v -> {

            String origin = spinnerOrig.getSelectedItem().toString();
            String destination = spinnerDest.getSelectedItem().toString();
            String type = spinnerPassType.getSelectedItem().toString();

            int fare = computeFare(origin, destination, type);

            String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(new Date());

            DatabaseReference typeRef = fareRef.child(today).child(type.toLowerCase());

            typeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {

                    int passengerTotal = 0;
                    int fareTotal = 0;

                    if (snapshot.exists()) {
                        if (snapshot.child("passengerTotal").getValue() != null) {
                            passengerTotal = snapshot.child("passengerTotal").getValue(Integer.class);
                        }
                        if (snapshot.child("fareTotal").getValue() != null) {
                            fareTotal = snapshot.child("fareTotal").getValue(Integer.class);
                        }
                    }

                    passengerTotal += 1;
                    fareTotal += fare;

                    typeRef.child("passengerTotal").setValue(passengerTotal);
                    typeRef.child("fareTotal").setValue(fareTotal);

                    Toast.makeText(getContext(),
                            "Saved. Fare: " + fare,
                            Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Toast.makeText(getContext(),
                            "Error: " + error.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private int computeFare(String origin, String destination, String type) {

        boolean sameRoute = origin.equals(destination);

        if (sameRoute) {
            if (type.equalsIgnoreCase("Regular")) {
                return 15;
            } else {
                return 10;
            }
        }

        return 15;
    }
}