package com.example.runner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.runner.databinding.ActivityRunTogetherBinding;
import com.example.runner.databinding.ActivityShowRunTogetherBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.model.ObjectValue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShowRunTogether extends AppCompatActivity {

    private ActivityShowRunTogetherBinding binding;

    private Bundle extras;
    private String position;

    //Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference documentReference;
    private CollectionReference collectionReference;
    private List<String> keys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_show_run_together);
        //binding
        binding = ActivityShowRunTogetherBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        // init firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection("share");



        extras = getIntent().getExtras();
        if (extras != null) {
            // for the position of the run in the fire-store
            position = extras.getString("position");
            documentReference = collectionReference.document(position);
        }

        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value.exists()){
                    String startTimer = (String) value.get("startTime");
                    binding.startTimer.setText(startTimer);

                    //GET HASHMAP KEYS TO ARRAY LIST
                    Map<String, Object> runTogether;
                    runTogether=value.getData();
                    keys = new ArrayList<>();
                    for(String key : runTogether.keySet())
                    {
                        keys.add(key);
                    }

                    String puid  = (String)value.get(keys.get(1));
                    String partnerPara [] = puid.split(" ");
                    binding.pName.setText(partnerPara[0]);
                    binding.pKm.setText(partnerPara[1]);
                    binding.pWatch.setText(partnerPara[2]);

                    String myuid  = (String)value.get(keys.get(2));
                    String myPara [] = myuid.split(" ");
                    binding.mName.setText(myPara[0]);
                    binding.mKm.setText(myPara[1]);
                    binding.mWatch.setText(myPara[2]);
                }
            }
        });

    }
}
