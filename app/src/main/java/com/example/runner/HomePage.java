package com.example.runner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.runner.Profile;
import com.example.runner.databinding.ActivityHomePageBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HomePage extends AppCompatActivity {

    ActivityHomePageBinding binding;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main_profile);
        binding = ActivityHomePageBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //firebase
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        //setting up top nav bar
        binding.topAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()){
                    case R.id.logOut:
                        Intent intent = new Intent(HomePage.this,MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        databaseReference.child(firebaseUser.getUid()).child("isConnected").setValue(false).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(HomePage.this,"user is logout",Toast.LENGTH_SHORT).show();
                            }
                        });
                        FirebaseAuth.getInstance().signOut();
                        startActivity(intent);
                        break;
                }
                return true;
            }
        });

        //setting up the bottom nav
        binding.bottomNavBar.setSelectedItemId(R.id.home_page); // to keep the icon on
        binding.bottomNavBar.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.home_page:
                        // start new activity....
                        //overridePendingTransition(0, 0);
                        break;
                    case R.id.club:
                        Intent intentC = new Intent(HomePage.this,Club.class);
                        startActivity(intentC);
                        overridePendingTransition(0, 0);
                        break;
                    case R.id.run:
                        Intent intentR = new Intent(HomePage.this,Run.class);
                        startActivity(intentR);
                        overridePendingTransition(0, 0);
                        break;
                    case R.id.status:
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

        databaseReference.child(firebaseUser.getUid()).child("isConnected").setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
    }

    // for smooth transitions between activity & for the botton-nav button to be pressed
    @Override
    protected void onRestart() {
        super.onRestart();
        binding.bottomNavBar.setSelectedItemId(R.id.home_page);
        overridePendingTransition(0, 0);
    }
}