package com.example.runner.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.runner.R;
import com.example.runner.ShowRunActivity;
import com.example.runner.ShowRunTogether;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.protobuf.StringValue;

import java.util.ArrayList;

public class RunTogetherListFragment extends Fragment {
    private View view;

    private ArrayList<String> allRunTogetherArrayList;
    private ArrayAdapter<String> itemsAdapter;
    private ListView allRunTogetherListView;


    //Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference documentReference;
    private CollectionReference collectionReference;

    public RunTogetherListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_run_together_list, container, false);
        allRunTogetherListView = view.findViewById(R.id.allRunTogetherList);
        allRunTogetherListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                documentReference = collectionReference.document(allRunTogetherArrayList.get(position));
                documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Toast.makeText(getContext(), "Error from RunTogetherListFragment!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (value.exists()) {
                            Intent intent = new Intent(getContext(), ShowRunTogether.class);
                            // here we pass the ID of the document that we press and move it to the next activity
                            intent.putExtra("position", allRunTogetherArrayList.get(position));
                            startActivity(intent);
                        }
                    }
                });
            }
        });
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // init firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection("share");

        allRunTogetherArrayList = new ArrayList<>();

        getAllRunList();



    }

    private void getAllRunList() {
        collectionReference.orderBy(firebaseUser.getUid(), Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("TAG", "onCreate: "+document.getId());
                        String aa = document.getId();
                        allRunTogetherArrayList.add(aa);
                    }
                    Log.d("TAG", "onCreate: "+allRunTogetherArrayList.toString());

                    itemsAdapter = new ArrayAdapter(getContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, allRunTogetherArrayList);
                    allRunTogetherListView.setAdapter(itemsAdapter);
                }
            }
        });
    }
}