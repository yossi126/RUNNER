package com.example.runner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TableLayout;
import android.widget.Toast;

import com.example.runner.Adapter.ViewPagerAdapter;
import com.example.runner.databinding.ActivityBinding;
import com.example.runner.databinding.ActivityHomePageBinding;
import com.example.runner.fragments.AllRunsFragment;
import com.example.runner.fragments.RunTogetherListFragment;
import com.example.runner.fragments.StatisticsFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Activity extends AppCompatActivity {

    private static final String TAG = "shukim";
    ActivityBinding binding;
    //Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference documentReference;
    private CollectionReference collectionReference;

    //SET ALL RUNS LIST VIEW
    private ArrayList<String> allRunArrayList;
    private ArrayAdapter<String> itemsAdapter;

    //tabs
    private TableLayout tableLayout;
    private ViewPager2 viewPager2;
    private ViewPagerAdapter viewPagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_);
        binding = ActivityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // init firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection(firebaseUser.getUid());

        //GET ALL RUNS FB FIRESTORE
        //allRunArrayList = new ArrayList<>();
        //getAllRunList();
        //getAllRunList();


        //tabs
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),getLifecycle());
        viewPagerAdapter.addFragment(new AllRunsFragment(), "Runs List",R.drawable.ic_baseline_run_list_24);
        viewPagerAdapter.addFragment(new RunTogetherListFragment(), "Run Together",R.drawable.ic_baseline_query_stats_24);
        viewPagerAdapter.addFragment(new StatisticsFragment(), "Statistics",R.drawable.ic_baseline_query_stats_24);
        binding.viewPager2.setAdapter(viewPagerAdapter);

        new TabLayoutMediator(binding.tabLayout, binding.viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(viewPagerAdapter.getPageTitle(position));
                tab.setIcon(viewPagerAdapter.getDrawables(position));
            }

        }).attach();

//        binding.allRunsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                //Log.d(TAG, "onItemClick: "+ allRunArrayList.get(position));
//                documentReference = firebaseFirestore.collection(firebaseUser.getUid()).document(allRunArrayList.get(position));
//                documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
//                    @Override
//                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
//                        if (error != null) {
//                            Toast.makeText(Activity.this, "Error!", Toast.LENGTH_SHORT).show();
//                            //Log.d(TAG, "onItemClick: "+ error.toString());
//                            return;
//                        }
//                        if (value.exists()) {
//                            //Log.d(TAG, "onItemClick: "+ value.get("distance"));
//                            //String d = value.get("distance");
//                            Intent intent = new Intent(Activity.this, ShowRunActivity.class);
//                            //Log.d(TAG, "onItemClick: "+ value.get("distance"));
//                            //Log.d(TAG, "onItemClick: "+ value.get("timestamp"));
//                            intent.putExtra("distance",value.get("distance").toString());
//                            intent.putExtra("timestamp",value.get("timestamp").toString());
//                            startActivity(intent);
//
//                        }
//                    }
//                });
//
//
//            }
//        });


        //SETTING TOP NAV BAR
        topNavBar();

        //SETTING BOTTOM NAV BAR
        bottomNavBar();
    }

//    private void getAllRunList() {
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
//                                //Log.d(TAG, document.getId());
//                            }
//                            itemsAdapter = new ArrayAdapter<String>(Activity.this, android.R.layout.simple_list_item_1, allRunArrayList);
//                            binding.allRunsListView.setAdapter(itemsAdapter);
//                        } else {
//                            Log.w(TAG, "Error getting documents.", task.getException());
//                        }
//                    }
//                });
//    }

    //SETTING BOTTOM NAV BAR
    private void bottomNavBar() {
        binding.bottomNavBar.setSelectedItemId(R.id.activity); // to keep the icon on
        binding.bottomNavBar.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.home_page:
                        Intent intentH = new Intent(Activity.this,HomePage.class);
                        startActivity(intentH);
                        overridePendingTransition(0, 0);
                        break;
                    case R.id.club:
                        Intent intentC = new Intent(Activity.this,Club.class);
                        startActivity(intentC);
                        overridePendingTransition(0, 0);
                        break;
                    case R.id.run:
                        Intent intentR = new Intent(Activity.this,Run.class);
                        startActivity(intentR);
                        overridePendingTransition(0, 0);
                        break;
                    case R.id.activity:
                        break;
                    case R.id.profile:
                        Intent intent = new Intent(Activity.this, Profile.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        break;
                }
                return true;
            }
        });
    }

    //SETTING BOTTOM NAV BAR
    private void topNavBar() {
        //SETTING TOP NAV BAR
        binding.topAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.logOut:
                        String logOut = HomePage.getCurrentDateTime();
                        databaseReference = FirebaseDatabase.getInstance().getReference("users");
                        databaseReference.child(firebaseUser.getUid()).child("logOut").setValue(logOut);
                        Intent intent = new Intent(Activity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        FirebaseAuth.getInstance().signOut();
                        startActivity(intent);
                        break;
                }
                return true;
            }
        });
    }


    // for smooth transitions between activity & for the botton-nav button to be pressed
    @Override
    protected void onRestart() {
        super.onRestart();
        binding.bottomNavBar.setSelectedItemId(R.id.activity);
        overridePendingTransition(0, 0);
    }


    ///???????????????????????????
    @Override
    public void onBackPressed() {
        if (binding.viewPager2.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            binding.viewPager2.setCurrentItem(binding.viewPager2.getCurrentItem() - 1);
        }
    }
}

