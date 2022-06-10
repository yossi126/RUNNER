package com.example.runner;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.runner.Adapter.ClubAdapterRecyclerView;
import com.example.runner.data.User;
import com.example.runner.databinding.ActivityClubBinding;
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

import java.util.ArrayList;

public class Club extends AppCompatActivity {

    ActivityClubBinding binding;
    private FirebaseUser currentFirebaseUser;
    private DatabaseReference databaseReference;
    private String userID;
    private ArrayList<User> userArrayList;
    ClubAdapterRecyclerView clubAdapterRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityClubBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //userID = currentFirebaseUser.getUid();
        userArrayList = new ArrayList<>();

        //SETTING TOP NAV BAR
        topNavBar();

        //SETTING BOTTOM NAV BAR
        bottomNavBar();


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                userArrayList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    //userArrayList.add(user);
                    if (!currentFirebaseUser.getUid().equals(user.getUid())) {
                        userArrayList.add(user);
                    }
                }

                clubAdapterRecyclerView = new ClubAdapterRecyclerView(userArrayList);
                binding.clubRV.setHasFixedSize(true);
                binding.clubRV.setLayoutManager(new LinearLayoutManager(Club.this));
                binding.clubRV.setAdapter(clubAdapterRecyclerView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void bottomNavBar() {
        //setting up the bottom nav
        binding.bottomNavBar.setSelectedItemId(R.id.club); // to keep the icon on
        binding.bottomNavBar.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home_page:
                        Intent intent = new Intent(Club.this, HomePage.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        //finish();
                        break;
                    case R.id.club:
                        break;
                    case R.id.run:
                        Intent intentR = new Intent(Club.this, Run.class);
                        startActivity(intentR);
                        overridePendingTransition(0, 0);
                        break;
                    case R.id.status:
                        break;
                    case R.id.profile:
                        Intent intentP = new Intent(Club.this, Profile.class);
                        startActivity(intentP);
                        overridePendingTransition(0, 0);
                        break;
                }
                return true;
            }
        });
    }

    //setting up top nav bar
    private void topNavBar() {
        binding.topAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.logOut:
                        Intent intent = new Intent(Club.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        databaseReference.child(currentFirebaseUser.getUid()).child("isConnected").setValue(false).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(Club.this, "user is logout", Toast.LENGTH_SHORT).show();
                            }
                        });
                        FirebaseAuth.getInstance().signOut();
                        startActivity(intent);
                        break;
                }
                return true;
            }
        });

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        binding.bottomNavBar.setSelectedItemId(R.id.club);
        overridePendingTransition(0, 0);
    }
}