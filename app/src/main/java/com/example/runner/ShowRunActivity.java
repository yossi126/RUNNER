package com.example.runner;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.runner.Adapter.SplitsRvAdapter;
import com.example.runner.data.Splits;
import com.example.runner.databinding.ActivityShowRunBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ShowRunActivity extends AppCompatActivity {

    private static final String TAG = "ShowRunActivity";
    //binding
    private ActivityShowRunBinding binding;
    //var
    private String position, chronometer, distance, timesOfDay, avgPace;
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
    //splits table
    private SplitsRvAdapter splitsRvAdapter;

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

        //setting up top nav bar
        topNavBar();


        extras = getIntent().getExtras();
        if (extras != null) {
            // for the position of the run in the fire-store
            position = extras.getString("position");
            documentReference = collectionReference.document(position);

        }


        // in this snapshot we getting all in info about the run an putting it in the ui
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                try {
                    if (value.exists()) {
                        //points = (List<LatLng>) value.get("points");

                        // getting the points for drawing the map -- references for the points in firebase
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
                                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLatLng, 14));
                                    //googleMap.addMarker(options);
                                }
                            });
                        } catch (Exception e) {
                            Toast.makeText(ShowRunActivity.this, "there is no road for this run", Toast.LENGTH_LONG).show();
                        }

                        // getting the splits
                        final List<HashMap<String, Object>> splits = (List<HashMap<String, Object>>) value.get("splits");
                        final ArrayList<Splits> splitsArrayList = new ArrayList<>();
                        for (HashMap<String, Object> split : splits) {
                            //create split object and store it in the array list
                            String time = (String) split.get("time");
                            int km = ((Long) split.get("km")).intValue();
                            splitsArrayList.add(new Splits(time, km));

                        }
                        Log.d(TAG, "onEvent: " + splitsArrayList);
                        // set up the RecyclerView and the Adapter to organize the table
                        setUpRecyclerView(splitsArrayList);


                        // set up al the values from fire base into the UI
                        chronometer = (String) value.get("chronometer");
                        binding.timeTextView.setText(chronometer);
                        distance = (String) value.get("distance");
                        binding.kmTextView.setText(distance);

                        // value.get("timestamp") is Timestamp so we need to use getTimeStamp() to get it back to string
                        binding.timeStampTextView.setText(getTimeStamp((Timestamp) value.get("timestamp")));

                        timesOfDay = (String) value.get("timesOfDay");
                        binding.topAppBar.setTitle(timesOfDay);
                        avgPace = (String) value.get("avgPace");
                        binding.avgPaceTextView.setText(avgPace);
                    }
                } catch (Exception e) {
                    Log.d("yossiErr", "ShowRunActivity - onCreate -  onEvent: " + e.getMessage());
                }

            }
        });

    }

    // method that write back Timestamp to string
    public String getTimeStamp(Timestamp timestamp) {
        Date date = new Date(2000, 11, 21);
        try {
            date = timestamp.toDate();
        } catch (Exception e) {

        }
        long timestampl = date.getTime();
        java.text.SimpleDateFormat simpleDateFormat = new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String dateStr = simpleDateFormat.format(timestampl);
        return dateStr;
    }

    private void setUpRecyclerView(ArrayList<Splits> splitsArrayList) {
        binding.tableRecyclerView.hasFixedSize();
        binding.tableRecyclerView.setLayoutManager(new LinearLayoutManager(ShowRunActivity.this));
        splitsRvAdapter = new SplitsRvAdapter(ShowRunActivity.this, splitsArrayList);
        binding.tableRecyclerView.setAdapter(splitsRvAdapter);
    }

    private void topNavBar() {
        binding.topAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.delete:
                        //when pressing delete alert dialog will show up
                        AlertDialog.Builder builder = new AlertDialog.Builder(ShowRunActivity.this);
                        builder.setTitle("Delete this run?");
                        //builder.setMessage("bla bla");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteRun();
                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });

                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                        break;
                }
                return true;
            }
        });
    }

    //delete run method
    private void deleteRun() {
        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ShowRunActivity.this, "run is deleted", Toast.LENGTH_LONG).show();
                    finish();
                    startActivity(new Intent(ShowRunActivity.this, Activity.class));
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        //overridePendingTransition(0, 0);
        super.onBackPressed();
    }

}