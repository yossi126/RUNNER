package com.example.runner;

import android.content.Intent;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.runner.databinding.ActivityHomePageBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HomePage extends AppCompatActivity {

    ActivityHomePageBinding binding;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private String lastLogin;
    private List<Calendar> calendars;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_home_page);
        binding = ActivityHomePageBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //FIREBASE - GET CURRENT USER ID +GET PATH OF DB NAMED "USERS"
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        //SET USER STATUS ONLINE
        databaseReference.child(firebaseUser.getUid()).child("isConnected").setValue(true);

        //CHANGE LAST LOGIN PARAMETER FOR USER MANAGEMENT
        lastLogin = getCurrentDateTime();
        databaseReference.child(firebaseUser.getUid()).child("lastLogin").setValue(lastLogin);

        //CREATE CALENDAR LIST
        calendars = new ArrayList<>();

        //HIGHLIGHT RUNNING DATES IN CALENDAR
        db.collection(firebaseUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //GET FIRESTORE DOCUMENT TITLE
                                String dateString = document.getId();
                                //HIGHLIGHT DATES CALENDAR
                                DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                Calendar cal = Calendar.getInstance();
                                try {
                                    cal.setTime(df.parse(dateString));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                calendars.add(cal);
                               binding.calendarView.setHighlightedDays(calendars);
                            }
                        } else {
                            Log.w("logw", "Error getting documents.", task.getException());
                        }
                    }
                });


        //GET USER LAST RUN + ASSIGN IN CARDVIEW
        db.collection(firebaseUser.getUid()).orderBy("timestamp", Query.Direction.DESCENDING).limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("logd", document.getId() + " => " + document.getData());
                                String t = document.getData().get("timestamp").toString();
                                String d = document.getData().get("distance").toString();
                                String c = document.getData().get("chronometer").toString();
                                binding.timestamp.setText("Time : " + t);
                                binding.distance.setText("Distance in " + d);
                                binding.chronometer.setText("Running Time (in min) :" + c);
                            }
                        } else {
                            Log.w("logw", "Error getting documents.", task.getException());
                        }
                    }
                });


        //SETTING TOP NAV BAR
        topNavBar();

        //SETTING BOTTOM NAV BAR
        bottomNavBar();

    }

    //SETTING BOTTOM NAV BAR
    private void bottomNavBar() {
        binding.bottomNavBar.setSelectedItemId(R.id.home_page); // to keep the icon on
        binding.bottomNavBar.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home_page:
                        // start new activity....
                        //overridePendingTransition(0, 0);
                        break;
                    case R.id.club:
                        Intent intentC = new Intent(HomePage.this, Club.class);
                        startActivity(intentC);
                        overridePendingTransition(0, 0);
                        break;
                    case R.id.run:
                        Intent intentR = new Intent(HomePage.this, Run.class);
                        startActivity(intentR);
                        overridePendingTransition(0, 0);
                        break;
                    case R.id.activity:
                        Intent intentS = new Intent(HomePage.this, Activity.class);
                        startActivity(intentS);
                        overridePendingTransition(0, 0);
                        break;
                    case R.id.profile:
                        Intent intent = new Intent(HomePage.this, Profile.class);
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
                        Intent intent = new Intent(HomePage.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        FirebaseAuth.getInstance().signOut();
                        startActivity(intent);
                        break;
                }
                return true;
            }
        });
    }

    // for smooth transitions between activity & for the bottom-nav button to be pressed
    @Override
    protected void onRestart() {
        super.onRestart();
        binding.bottomNavBar.setSelectedItemId(R.id.home_page);
        overridePendingTransition(0, 0);
    }

    //GET PARAMETER FOR LAST LOGIN
    public static String getCurrentDateTime() {
        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String datetime = ft.format(dNow);
        return datetime;
    }


}
