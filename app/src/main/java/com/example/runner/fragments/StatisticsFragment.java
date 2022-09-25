package com.example.runner.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.runner.R;
import com.example.runner.ShowRunActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;


public class StatisticsFragment extends Fragment {

    private View view;
    //Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference documentReference;
    private CollectionReference collectionReference;

    // widgets
    private TextView totalRunsTv, totalKmTv, longestRunKmTv, longestRunDateTv,totalTimeTv, longestRunSearchTv;

    //var
    private String longestRunID;

    //bar chart
    BarChart barChart;// variable for our bar chart
    BarData barData;// variable for our bar data.
    BarDataSet barDataSet; // variable for our bar data set.
    ArrayList barEntriesArrayList;// array list for storing entries.

    public StatisticsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_statistics, container, false);
        totalRunsTv = view.findViewById(R.id.totalRunsTextView);
        totalKmTv = view.findViewById(R.id.totalKmTextView);
        longestRunKmTv = view.findViewById(R.id.longestRunTextView);
        longestRunDateTv = view.findViewById(R.id.longestRunDateTextView);
        totalTimeTv = view.findViewById(R.id.totalTimeTv);
        // this on click work the same as in the AllRunsFragment. we pass the ID of the document to the next activity and open the run.
        longestRunSearchTv = view.findViewById(R.id.longestRunSearchTv);
        longestRunSearchTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),ShowRunActivity.class);
                intent.putExtra("position", longestRunID);
                startActivity(intent);
            }
        });

        getTotalRuns();
        getTotalRunsKm();
        getLongestRun();
        getTotalRunningTime();

        // initializing variable for bar chart.
        barChart = view.findViewById(R.id.idBarChart);

        return view;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // init firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection(firebaseUser.getUid());

        getBarEntries();
    }

    private void getBarEntries() {
        float f[] = new float[5];
        // creating a new array list
        firebaseFirestore.collection(firebaseUser.getUid()).orderBy("timestamp", Query.Direction.DESCENDING).limit(5).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                barEntriesArrayList = new ArrayList<>();
                if (task.isSuccessful()) {
                    int count = 0;
                    for (QueryDocumentSnapshot document : task.getResult()){
                        f[count] = Float.valueOf((String)document.get("distance"));
                        count++;
                    }
                    barEntriesArrayList.add(new BarEntry(1, f[4]));
                    barEntriesArrayList.add(new BarEntry(2, f[3]));
                    barEntriesArrayList.add(new BarEntry(3, f[2]));
                    barEntriesArrayList.add(new BarEntry(4, f[1]));
                    barEntriesArrayList.add(new BarEntry(5, f[0]));


                    // creating a new bar data set.
                    barDataSet = new BarDataSet(barEntriesArrayList, "Last 5 Runs");
                    // creating a new bar data and
                    // passing our bar data set.
                    barData = new BarData(barDataSet);
                    // below line is to set data
                    // to our bar chart.
                    barChart.setData(barData);
                    barChart.setFitBars(true);
                    // adding color to our bar data set.
                    barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                    // setting text color.
                    barDataSet.setValueTextColor(Color.BLACK);
                    //set y axis gone
                    YAxis leftAxis = barChart.getAxisLeft();
                    YAxis rightAxis = barChart.getAxisRight();
                    leftAxis.setEnabled(false);
                    rightAxis.setEnabled(false);
                    // setting text size
                    barDataSet.setValueTextSize(16f);
                    barChart.getDescription().setEnabled(false);
                }
            }
        });
        Log.d("TAG", "out: "+ Arrays.toString(f));

    }

    private void getTotalRunningTime() {
        firebaseFirestore.collection(firebaseUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                ArrayList<String> totalTimeArrayList = new ArrayList<>();
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        totalTimeArrayList.add((String)document.get("chronometer"));
                    }
                    long seconds = 0;
                    long minutesToHours = 0;
                    for(String userTime : totalTimeArrayList) {
                        String[] hhmmss = userTime.split(":");
                        seconds += Integer.valueOf(hhmmss[0]) * 60;
                        seconds += Integer.valueOf(hhmmss[1]);
                        minutesToHours+=Integer.valueOf(hhmmss[0]);
                    }
                    long ss = seconds % 60;
                    long mm = (seconds / 60) % 60;
                    long hh = minutesToHours/60;
                    totalTimeTv.setText(String.format("%02d:%02d:%02d",hh,mm,ss));
                }

            }
        });
    }

    private void getLongestRun() {
        firebaseFirestore.collection(firebaseUser.getUid())
                .orderBy("distance", Query.Direction.DESCENDING)
                .limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                longestRunKmTv.setText((String)document.get("distance"));
                                longestRunDateTv.setText((String)document.getId().split(" ")[0]);
                                longestRunDateTv.setVisibility(View.GONE);
                                //longestRunDateTv.setText((String)document.getId());
                                //longestRunDateTv.setVisibility(View.GONE);
                                longestRunID = (String)document.getId();
                            }
                        } else {
                            longestRunKmTv.setText("err");
                            longestRunDateTv.setText("err");
                            Log.w("StatisticsFragment", "StatisticsFragment - Error getting QuerySnapshot.", task.getException());
                        }
                    }
                });
    }

    private void getTotalRuns() {
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    totalRunsTv.setText(String.valueOf(task.getResult().size()));
                }else{
                    totalRunsTv.setText("err");
                }
            }
        });
    }


    public void getTotalRunsKm() {
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<String> distances = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        distances.add((String) document.get("distance"));
                    }
                    /*
                    for example:
                     distances - [2.01, 4.00, 4.00, 2.00, 3.00, 5.00, 2.00, 6.02]
                     first after calc - 28.0
                     second after calc - 0.03
                     together total - 28.03
                    */
                    double first = 0; // 2
                    double second = 0; // 0.1
                    // first+second  = 2.01
                    for (String distance : distances) {
                        String[] strings = distance.split("[.]");
                        first += Double.valueOf(strings[0]);
                        second += Double.valueOf(strings[1]) / 100;
                    }
                    totalKmTv.setText(String.valueOf(first + second));
                }
            }
        });
    }
}