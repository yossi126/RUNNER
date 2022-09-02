package com.example.runner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.runner.data.User;
import com.example.runner.databinding.ActivityRunTogetherBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RunTogether extends AppCompatActivity {


    private FirebaseUser currentFirebaseUser;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference documentReference;


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


    //Chronometer vars
    private boolean stopStart;
    private long pauseOffset;


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
        firebaseFirestore = FirebaseFirestore.getInstance();
        //documentReference = firebaseFirestore.collection("share").document();

        //Chronometer vars
        pauseOffset = 0;
        distanceFar = 0;
        startChronometer();

        String startTime = getCurrentDateTime();




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
                        if((boolean)snapshot.child("hasFinished").getValue()==true)
                        {
                            binding.imageViewFinish.setVisibility(View.VISIBLE);
                        }
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
        //stopButton();

        binding.finishRunTogether.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                databaseReference.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String partnerUid = String.valueOf(snapshot.child("letsRun").getValue());
                        String myname = String.valueOf(snapshot.child("name").getValue());
                        String mykm = String.valueOf(snapshot.child("currentKm").getValue());
                        databaseReference.child(firebaseUser.getUid()).child("myChrono").setValue(binding.timeChronometer.getText());
                        String mychrono = String.valueOf(snapshot.child("myChrono").getValue());
                        databaseReference.child(firebaseUser.getUid()).child("hasFinished").setValue(true);
                        //binding.imageViewFinish.setVisibility(View.VISIBLE);


                        databaseReference.child(partnerUid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String partnerKm = String.valueOf(snapshot.child("currentKm").getValue());
                                String partnerChrono = String.valueOf(snapshot.child("myChrono").getValue());
                                String partnerName = String.valueOf(snapshot.child("name").getValue());

                                if((boolean)snapshot.child("hasFinished").getValue() == false){
                                    fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                                    Snackbar snackbar = Snackbar.make(view, "waiting for partner to end run",
                                            Snackbar.LENGTH_LONG);
                                    snackbar.setDuration(20000);
                                    snackbar.show();
                                    pauseChronometer();


                                }else{
                                    Map<String, Object> runTogether = new HashMap<>();
                                    runTogether.put(firebaseUser.getUid(),myname + " " + mykm+" "+mychrono);
                                    runTogether.put(partnerUid,partnerName + " " + partnerKm+" "+partnerChrono);
                                    runTogether.put("runners", myname + " & " + partnerName);
                                    runTogether.put("startTime",startTime);
                                    runTogether.put("timestamp", FieldValue.serverTimestamp());

                                    documentReference = firebaseFirestore.collection("share").document( startTime + " " +
                                            partnerName+ " vs " + myname );

                                    documentReference.set(runTogether).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {

                                        }
                                    });
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        databaseReference.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String partnerUid = String.valueOf(snapshot.child("letsRun").getValue());
                                databaseReference.child(partnerUid).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if((boolean)snapshot.child("hasFinished").getValue() == true){
                                            finish();
                                        }
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
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



                String partnerid = String.valueOf(databaseReference.child(currentFirebaseUser.getUid()).child("letsRun"));
                Log.d("TAG", "onClick: "+partnerid);
            }
        });

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }


    // CHRONOMETER
    private void startChronometer() {
        binding.timeChronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
        binding.timeChronometer.start();
    }

    private void pauseChronometer() {
        binding.timeChronometer.stop();
        pauseOffset = SystemClock.elapsedRealtime() - binding.timeChronometer.getBase();
    }

    private void resetChronometer() {
        binding.timeChronometer.setBase(SystemClock.elapsedRealtime());
        pauseOffset = 0;
    }

    private static String getCurrentDateTime() {
        // creating an id by time and date to the "SavedRuns" children
        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        //SimpleDateFormat ft = new SimpleDateFormat("EEEE, d MMMM yyyy HH:mm:ss");
        String datetime = ft.format(dNow);
        return datetime;
    }


}