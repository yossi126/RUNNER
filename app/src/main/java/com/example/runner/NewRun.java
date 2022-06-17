package com.example.runner;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.runner.databinding.ActivityNewRunBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

public class NewRun extends AppCompatActivity implements View.OnClickListener{

    /*
    shortcuts
    All Methods Collapse -> CTRL + SHIFT + -
    All Methods Expand -> CTRL + SHIFT + +
    Ctrl+Shift+Alt+L
    */

    //binding
    ActivityNewRunBinding binding;

    // Views
    //private TextView distanceText;
    //private Chronometer chronometer;
    private Button startStop, endBtn, focus;
    // my edit
    private ImageButton startPauseButton;


    //fragment vars
    private GoogleMap map;
    private Polyline polyline;
    private PolylineOptions polylineOptions;

    // current location
    private SupportMapFragment supportMapFragment;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private Location currentLocation;
    private Looper looper;

    // Vars

    //Chronometer vars
    private boolean stopStart;
    private long pauseOffset;

    private float distanceFar;
    private float lat1, lng1, lat2, lng2;
    private int length;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //binding
        binding = ActivityNewRunBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // Initialize:
        supportMapFragment = (SupportMapFragment)  getSupportFragmentManager().findFragmentById(R.id.google_map);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        polylineOptions = new PolylineOptions();
        //my edit
        startPauseButton = findViewById(R.id.imageButtonStartPause);
        //startStop = findViewById(R.id.startStop);
        //focus = findViewById(R.id.focus_btn_nd);
        //startStop.setOnClickListener(this);
        //endBtn = findViewById(R.id.endBtn);
        //endBtn.setOnLongClickListener(this);
        //endBtn.setOnClickListener(this);
        //opened = false;
        //chronometer = findViewById(R.id.timeChronometer);
        //distanceText = findViewById(R.id.distanceText);
        pauseOffset = 0;
        distanceFar = 0;
        resetChronometer();
        getCurrentLocation();

//        focus.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                drawTrack();
//                Toast.makeText(NewRun.this,"focus?",Toast.LENGTH_SHORT).show();
//            }
//        });

        // create a location callback
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                currentLocation = locationResult.getLastLocation();
                final LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                polylineOptions.add(latLng);
                supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        getFarAway(polylineOptions);
                        double track = distanceFar / 1000;
                        String text = String.valueOf(track);
                        try {
                            int dot = text.indexOf(".");
                            text = text.substring(0, dot+3);
                        }catch (Exception e){
                            System.out.println(text);
                        }
                        if (text.equals("0.0")){text = "0.00";}
                        //distanceText.setText("km: " + text);
                        binding.distanceText.setText("km: " + text);

                        drawTrack();
                    }
                });
            }
        };

        stopStart = true;
        startChronometer();
        requestLocation();

        //setting up stop button
        stopButton();

    }


    private void stopButton() {
        binding.imageButtonStop.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent = new Intent(NewRun.this, EndRunSummary.class);
                intent.putExtra("chronometer",  binding.timeChronometer.getText());
                intent.putExtra("distance", binding.distanceText.getText().toString());
                intent.putExtra("polylines", polylineOptions);
                startActivity(intent);
                finish();
                return false;
            }
        });
    }

    @SuppressLint("MissingPermission")
    void requestLocation() {
        // get the client location every second
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, looper.myLooper());
    }

    private void drawTrack() {
        // drawing the user track on the map using a list of coordinates
        @SuppressLint("MissingPermission") Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(final Location location) {
                if (location != null){
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @SuppressLint("MissingPermission")
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            map = googleMap;
                            googleMap.clear();
                            polyline = map.addPolyline(polylineOptions);
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            googleMap.setMyLocationEnabled(true);
                            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                            googleMap.getUiSettings().setRotateGesturesEnabled(false);
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                        }
                    });
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imageButtonStartPause){
            if (!stopStart) {
                stopStart = true;
                startChronometer();
                requestLocation();
                startPauseButton.setImageResource(R.drawable.ic_baseline_pause_24);
            } else {
                stopStart = false;
                pauseChronometer();
                fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                startPauseButton.setImageResource(R.drawable.ic_baseline_play_arrow_24);
            }
        }
        else if (v.getId() == R.id.imageButtonStop){
            Toast.makeText(this, "Ending run with long click", Toast.LENGTH_SHORT).show();
        }
    }



    private void startChronometer(){
        binding.timeChronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
        binding.timeChronometer.start();
    }
    private void pauseChronometer(){
        binding.timeChronometer.stop();
        pauseOffset = SystemClock.elapsedRealtime() - binding.timeChronometer.getBase();
    }
    private void resetChronometer(){
        binding.timeChronometer.setBase(SystemClock.elapsedRealtime());
        pauseOffset = 0;
    }

    private void getCurrentLocation() {
        // init the first location to avoid mistakes
        @SuppressLint("MissingPermission") Task<Location> task = fusedLocationProviderClient.getLastLocation();
    }

    public float distance (float lat_a, float lng_a, float lat_b, float lng_b ) {
        // calculate the distance between 2 points
        double earthRadius = 3958.75;
        double latDiff = Math.toRadians(lat_b-lat_a);
        double lngDiff = Math.toRadians(lng_b-lng_a);
        double a = Math.sin(latDiff /2) * Math.sin(latDiff /2) +
                Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) *
                        Math.sin(lngDiff /2) * Math.sin(lngDiff /2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double distance = earthRadius * c;
        int meterConversion = 1609;
        return new Float(distance * meterConversion).floatValue();
    }

    public void getFarAway(PolylineOptions road){
        // calculating user's run by list of points
        List<LatLng> locations = road.getPoints();
        Object[] points = locations.toArray();
        length = points.length - 1;
        if (length > 0 && points[length-1] instanceof LatLng && points[length] instanceof LatLng){
            LatLng latlng1, latLng2;
            latlng1 = (LatLng) points[length-1];
            latLng2 = (LatLng) points[length];
            lat1 = (float) latlng1.latitude;
            lng1 = (float) latlng1.longitude;
            lat2 = (float) latLng2.latitude;
            lng2 = (float) latLng2.longitude;
            distanceFar += distance(lat1, lng1, lat2, lng2);
        }
    }
}