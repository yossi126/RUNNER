package com.example.runner.fragments;

import android.content.Intent;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class StatisticsFragment extends Fragment {

    private View view;
    //Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference documentReference;
    private CollectionReference collectionReference;

    // widgets
    private TextView totalRunsTv, totalKmTv, longestRunKmTv, longestRunDateTv;
    private ImageView longestRunSearchIv;
    //var
    private String longestRunID;

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
        // this on click work the same as in the AllRunsFragment. we pass the ID of the document to the next activity and open the run.
//        longestRunDateTv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //Toast.makeText(getContext(),longestRunDateTv.getText(),Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(getContext(),ShowRunActivity.class);
//                intent.putExtra("position", longestRunDateTv.getText());
//                startActivity(intent);
//            }
//        });

        longestRunSearchIv = view.findViewById(R.id.longestRunSearchIv);
        longestRunSearchIv.setOnClickListener(new View.OnClickListener() {
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

        return view;
    }

    private void getTotalRunningTime() {
        firebaseFirestore.collection(firebaseUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("StatisticsFragment", "getTotalRunningTime: "+(String)document.get("chronometer"));
                    }
                }
            }
        });
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // init firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection(firebaseUser.getUid());


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