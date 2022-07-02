package com.example.runner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.runner.databinding.ActivityBinding;
import com.example.runner.databinding.ActivityShowRunBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShowRunActivity extends AppCompatActivity {

    private static final String TAG = "ShowRunActivity";
    //binding
    private ActivityShowRunBinding binding;
    //var
    private String position,chronometer,distance,timestamp;
    private Bundle extras;
    //map
    private List<LatLng> points;
    private SupportMapFragment supportMapFragment;
    private GoogleMap map;
    private Polyline polyline;
    private PolylineOptions polylineOptions;

    //Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference documentReference;
    private CollectionReference collectionReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowRunBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // init firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection(firebaseUser.getUid());

        //map
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map_show_run);
        polylineOptions = new PolylineOptions();

        extras = getIntent().getExtras();
        if (extras != null) {
            position = extras.getString("position");
            documentReference = collectionReference.document(position);

        }

        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
//                if (value.exists()) {
//                    points = (List<LatLng>) value.get("points");
//                }
                // references for the points in firebase
                final List<HashMap<String, Double>> points = (List<HashMap<String, Double>>) value.get("points");
                // array list of all the latitude and longitude
                final ArrayList<LatLng> latLngs = new ArrayList<>();

                // looping the points in the firebase into the polylineOptions
                for (HashMap<String, Double> point : points) {
                    // getting the points from firebase into the polylineOptions again
                    polylineOptions.add(new LatLng(point.get("latitude"), point.get("longitude")));
                    // for zooming the map
                    latLngs.add(new LatLng(point.get("latitude"), point.get("longitude")));
                }

                // if the latLngs is empty the app will crash. so if the user didn't start the run and have no points it will show a toast message
                try {
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        final LatLng lastLatLng = latLngs.get(latLngs.size() / 2);

                        @Override
                        public void onMapReady(@NonNull GoogleMap googleMap) {
                            googleMap.clear();
                            polyline = googleMap.addPolyline(polylineOptions);
                            //MarkerOptions options = new MarkerOptions().position(lastLatLng);
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLatLng, 15));
                            //googleMap.addMarker(options);
                        }
                    });
                }
                catch(Exception e) {
                    Toast.makeText(ShowRunActivity.this,"there is no road for this run",Toast.LENGTH_LONG).show();
                }



            }
        });

    }

    @Override
    public void onBackPressed() {
        //overridePendingTransition(0, 0);
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}