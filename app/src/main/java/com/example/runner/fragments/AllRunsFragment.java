package com.example.runner.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.runner.R;
import com.example.runner.ShowRunActivity;
import com.google.android.gms.maps.model.LatLng;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AllRunsFragment extends Fragment {

    private View view;
    private ListView allRunsListView;
    private TextView textView;
    //SET ALL RUNS LIST VIEW
    private ArrayList<String> allRunArrayList;
    private ArrayAdapter<String> itemsAdapter;
    //Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference documentReference;
    private CollectionReference collectionReference;
    private List<LatLng> points;

    public AllRunsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_all_runs, container, false);
        allRunsListView = view.findViewById(R.id.allRunsTabListView);
        allRunsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                documentReference = collectionReference.document(allRunArrayList.get(position));
                documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            //Toast.makeText(getContext(), "Error!", Toast.LENGTH_SHORT).show();
                            Log.d("yossiErr", "Error from AllRunsFragment!" + error.getMessage());
                            return;
                        }
                        if (value.exists()) {
                            Intent intent = new Intent(getContext(), ShowRunActivity.class);
                            // here we pass the ID of the document that we press and move it to the next activity
                            intent.putExtra("position", allRunArrayList.get(position));
                            startActivity(intent);
                        }
                    }
                });
            }
        });

        return view;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //GET ALL RUNS FB FIRESTORE
        allRunArrayList = new ArrayList<>();
        // init firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection(firebaseUser.getUid());

        getAllRunList();

//        firebaseFirestore.collection("J0RsZAUuh2groBmPnTOagAv3Dro1")
//                .orderBy("distance", Query.Direction.DESCENDING).limit(1).addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//
//                for (QueryDocumentSnapshot document : value) {
//                    document.get("distance");
//                    Log.d("distance", "onEvent: "+ document.get("distance"));
//                }
//
//            }
//        });
//
//        firebaseFirestore.collection(firebaseUser.getUid())
//                .orderBy("timestamp", Query.Direction.DESCENDING).limit(1).addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//
//                for (QueryDocumentSnapshot document : value) {
//                    Log.d("yoyo2", "onEvent4: "+ getTimeStamp((Timestamp) document.get("timestamp")));
//                }
//            }
//        });

    }

    // method that write back Timestamp to string
    public String getTimeStamp(Timestamp timestamp) {
        Date date = new Date(2000, 11, 21);
        try {
            date = timestamp.toDate();
        } catch (Exception e) {

        }
        long timestampl = date.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String dateStr = simpleDateFormat.format(timestampl);
        return dateStr;
    }

    private void getAllRunList() {
//        collectionReference.get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                //GET FIRESTORE DOCUMENT TITLE
//                                String runString = document.getId();
//                                //ADD TO RUN LIST
//                                allRunArrayList.add(runString);
//                            }
//                            itemsAdapter = new ArrayAdapter(getContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, allRunArrayList);
//                            allRunsListView.setAdapter(itemsAdapter);
//                        } else {
//                            Log.w("TAG", "Error getting documents.", task.getException());
//                        }
//                    }
//                });

        /*
        there are 2 kind of timestamps, both of them in the EndRunSummary Activity
        1. one we use to save the time stamp in the fire store for order - FieldValue.serverTimestamp()
        2. with the second we only mark the ID of the document - getCurrentDateTime()

        In this code here we are ordering the runs by the 2 option.
        after that we take only take the ID by string and put it in array list of strings.

        */
        firebaseFirestore.collection(firebaseUser.getUid())
                .orderBy("timestamp", Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    //allRunArrayList.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        //GET FIRESTORE DOCUMENT TITLE
                        String runString = document.getId();
                        //ADD TO RUN LIST
                        allRunArrayList.add(runString);
                    }
                    try {
                        itemsAdapter = new ArrayAdapter(getContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, allRunArrayList);
                        allRunsListView.setAdapter(itemsAdapter);
                    } catch (Exception e) {
                        Log.d("yossi err", "AllRunsFragment - getAllRunList(): " + e.getMessage());
                    }
                } else {
                    Log.w("TAG", "AllRunsFragment - Error getting documents.", task.getException());
                }
            }
        });


//                    firebaseFirestore.collection(firebaseUser.getUid())
//                .orderBy("timestamp", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
//                @Override
//                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                    allRunArrayList.clear();
//                    if(!value.isEmpty()){
//                        for (QueryDocumentSnapshot document : value) {
//                            //GET FIRESTORE DOCUMENT TITLE
//                            String runString = document.getId();
//                            //ADD TO RUN LIST
//                            allRunArrayList.add(runString);
//                        }
//                        try {
//                            itemsAdapter = new ArrayAdapter(getContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, allRunArrayList);
//                            allRunsListView.setAdapter(itemsAdapter);
//                        }catch (Exception e){
//                            Log.d("yossi err", "AllRunsFragment - getAllRunList(): "+e.getMessage());
////                            Log.d("yossi err", "AllRunsFragment - getAllRunList(): "+error.getMessage());
//                        }
//
//                    }else{
//
//                    }
//                }
//            });
    }

}