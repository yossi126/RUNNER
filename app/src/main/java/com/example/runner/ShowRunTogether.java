package com.example.runner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.runner.data.User;
import com.example.runner.databinding.ActivityShowRunTogetherBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
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
    private DatabaseReference databaseReference;
    private CollectionReference collectionReference;
    private List<String> keys;
    private ArrayList<String> userArrayList;

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
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        collectionReference = firebaseFirestore.collection("share");
        userArrayList = new ArrayList<>();


        extras = getIntent().getExtras();
        if (extras != null) {
            // for the position of the run in the fire-store
            position = extras.getString("position");
            documentReference = collectionReference.document(position);
        }

        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                try {
                    if (value.exists()) {
                        String startTimer = (String) value.get("startTime");
                        binding.startTimer.setText(startTimer);

                        //GET HASHMAP KEYS TO ARRAY LIST-USE OBJECT MAP KEYS TO WRITE TO VIEW
                        Map<String, Object> runTogether;
                        runTogether = value.getData();
                        keys = new ArrayList<>();
                        for (String key : runTogether.keySet()) {
                            keys.add(key);
                        }

                        //GET FIRESTORE UID USERS-GET DATA FROM REALTIME FB PATH
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                //FOR EACH USER DO
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    User user = ds.getValue(User.class);
                                    for (int i = 0; i < keys.size(); i++) {
                                        //CHECK UID KEY == USER.UID
                                        if (keys.get(i).equals(user.getUid())) {
                                            userArrayList.add(user.getUid());
                                        }
                                    }
                                }
                                //GET AND WRITE TO VIEW USER LEFT
                                String uidLeft = (String) value.get(userArrayList.get(0));
                                String uidLeftPara[] = uidLeft.split(" ");
                                binding.mName.setText(uidLeftPara[0]);
                                binding.mKm.setText(uidLeftPara[1] + " km");
                                binding.mWatch.setText(uidLeftPara[2]);

                                //GET AND WRITE TO VIEW USER RIGHT
                                String uidRight = (String) value.get(userArrayList.get(1));
                                String uidRightPara[] = uidRight.split(" ");
                                binding.pName.setText(uidRightPara[0]);
                                binding.pKm.setText(uidRightPara[1] + " km");
                                binding.pWatch.setText(uidRightPara[2]);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    }
                } catch (Exception e) {
                    Log.d("yossiErr", "ShowRunTogether - onCreate -  onEvent: " + e.getMessage());
                }
            }
        });


    }
}
