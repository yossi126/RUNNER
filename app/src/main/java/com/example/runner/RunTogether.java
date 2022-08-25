package com.example.runner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import com.example.runner.data.User;
import com.example.runner.databinding.ActivityNewRunBinding;
import com.example.runner.databinding.ActivityRunTogetherBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class RunTogether extends AppCompatActivity {


    private FirebaseUser currentFirebaseUser;
    private DatabaseReference databaseReference;
    private User currentUser;
    private FirebaseUser firebaseUser;


    private String km;
    private float distanceFar;
    private float lat1, lng1, lat2, lng2;
    private int length;
    private ActivityRunTogetherBinding binding;
    private LocationCallback locationCallback;
    private Location currentLocation;
    private PolylineOptions polylineOptions;


    private FusedLocationProviderClient fusedLocationProviderClient;
    private Looper looper;


    //public static String partnerUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //binding
        binding = ActivityRunTogetherBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        polylineOptions = new PolylineOptions();

        //GET PATH OF DB NAMED "USERS"
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //GET PARTNER AND USER PARAMETERS
        databaseReference.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                binding.myName.setText(String.valueOf(snapshot.child("name").getValue()));

                //get partner
                String partnerUid = String.valueOf(snapshot.child("letsRun").getValue());
                databaseReference.child(partnerUid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        binding.partnerName.setText(String.valueOf(snapshot.child("name").getValue()));
                        binding.partnerKm.setText(String.valueOf(snapshot.child("currentKm").getValue()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        distanceFar = 0;

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                currentLocation = locationResult.getLastLocation();
                final LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                polylineOptions.add(latLng);
                getFarAway(polylineOptions);
                double track = distanceFar / 1000;
                km = String.valueOf(track);
                try {
                    int dot = km.indexOf(".");
                    km = km.substring(0, dot + 3);
                } catch (Exception e) {
                    System.out.println(km);
                }
                if (km.equals("0.0")) {
                    km = "0.00";
                }
                binding.myKm.setText(km);
                databaseReference.child(currentFirebaseUser.getUid()).child("currentKm").setValue(km);
            }
        };

//        databaseReference.child(partnerUid).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
//            @Override
//            public void onSuccess(DataSnapshot dataSnapshot) {
//                binding.partnerKm.setText(String.valueOf(dataSnapshot.child("currentKm").getValue()));
//            }
//        });

//        databaseReference.child(partnerUid).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                binding.partnerName.setText(String.valueOf(snapshot.child("currentKm").getValue()));
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        requestLocation();


    }

    private void getCurrentLocation() {
        // init the first location to avoid mistakes
        @SuppressLint("MissingPermission") Task<Location> task = fusedLocationProviderClient.getLastLocation();
    }

    @SuppressLint("MissingPermission")
    void requestLocation() {
        // get the client location every second
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, looper.myLooper());
    }


    //DISTANCE IN KM
    public float distance(float lat_a, float lng_a, float lat_b, float lng_b) {
        // calculate the distance between 2 points
        double earthRadius = 3958.75;
        double latDiff = Math.toRadians(lat_b - lat_a);
        double lngDiff = Math.toRadians(lng_b - lng_a);
        double a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2) +
                Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) *
                        Math.sin(lngDiff / 2) * Math.sin(lngDiff / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthRadius * c;
        int meterConversion = 1609;
        return new Float(distance * meterConversion).floatValue();
    }

    //POLYLINE TRACK
    public void getFarAway(PolylineOptions road) {
        // calculating user's run by list of points
        List<LatLng> locations = road.getPoints();
        Object[] points = locations.toArray();
        length = points.length - 1;
        if (length > 0 && points[length - 1] instanceof LatLng && points[length] instanceof LatLng) {
            LatLng latlng1, latLng2;
            latlng1 = (LatLng) points[length - 1];
            latLng2 = (LatLng) points[length];
            lat1 = (float) latlng1.latitude;
            lng1 = (float) latlng1.longitude;
            lat2 = (float) latLng2.latitude;
            lng2 = (float) latLng2.longitude;
            distanceFar += distance(lat1, lng1, lat2, lng2);
        }
    }
}