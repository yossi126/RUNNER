package com.example.runner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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
                    String yossi  = (String)value.get("runners");
                    binding.yossi.setText(yossi);

                }
            }
        });

    }
}