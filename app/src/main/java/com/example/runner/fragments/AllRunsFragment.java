package com.example.runner.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import android.os.Parcelable;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AllRunsFragment extends Fragment{

    private View view;
    private ListView allRunsListView;
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
        //itemsAdapter = new ArrayAdapter(getContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, allRunArrayList);
        //allRunsListView.setAdapter(itemsAdapter);
        allRunsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                //Toast.makeText(getContext(),"click",Toast.LENGTH_SHORT).show();

                //Navigation.findNavController(view).navigate(R.id.action_allRunsFragment_to_showRunFragment);
                ShowRunFragment showRunFragment = new ShowRunFragment();
//                getParentFragmentManager().beginTransaction()
//                        .setCustomAnimations(
//                                R.anim.slide_in,  // enter
//                                R.anim.fade_out,  // exit
//                                R.anim.fade_in,   // popEnter
//                                R.anim.slide_out  // popExit
//                        )
//                        .replace(((ViewGroup)getView().getParent()).getId(), showRunFragment)
//                        .addToBackStack(null)
//                        .commit();
//                Log.d("ViewGroup", "onItemClick: "+(ViewGroup)getView().getParent());



//                FragmentManager fragmentManager = getParentFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.setReorderingAllowed(true);
//                fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left,
//                        android.R.anim.slide_out_right);
//                fragmentTransaction.replace(((ViewGroup)getView().getParent()).getId(), showRunFragment, null);
//                fragmentTransaction.commit();

//                fragmentManager.beginTransaction()
//                        .replace(((ViewGroup)getView().getParent()).getId(),showRunFragment,null )
//                        .setReorderingAllowed(true)
//                        .addToBackStack("name").commit();

//                getActivity().getSupportFragmentManager().beginTransaction()
//                        .replace(((ViewGroup)getView().getParent()).getId(), showRunFragment, "findThisFragment")
//                        .addToBackStack(null)
//                        .commit();

                //getActivity().overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                documentReference = collectionReference.document(allRunArrayList.get(position));
                documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Toast.makeText(getContext(), "Error!", Toast.LENGTH_SHORT).show();
                            //Log.d("AllRunsFragment", "onItemClick: "+ error.toString());
                            return;
                        }
                        if (value.exists()) {
//                            Log.d("TAG", "onEvent: "+value.get("points"));
//                            points = (List<LatLng>) value.get("points");
//                            Log.d("TAG", "onEvent: "+points);
                            Intent intent = new Intent(getContext(), ShowRunActivity.class);
//                            intent.putExtra("distance",value.get("distance").toString());
//                            intent.putExtra("timestamp",value.get("timestamp").toString());
//                            intent.putExtra("chronometer",value.get("chronometer").toString());
//                            intent.putExtra("points", (Parcelable) points);
                            //intent.putExtra("position",position);
                            intent.putExtra("position",allRunArrayList.get(position));
                            startActivity(intent);

                        }
                    }
                });
                //startActivity(new Intent(getContext(), ShowRunActivity.class));

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



    }

        private void getAllRunList() {
        collectionReference.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //GET FIRESTORE DOCUMENT TITLE
                                String runString = document.getId();
                                //ADD TO RUN LIST
                                allRunArrayList.add(runString);
                            }
                            itemsAdapter = new ArrayAdapter(getContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, allRunArrayList);
                            allRunsListView.setAdapter(itemsAdapter);
                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

}