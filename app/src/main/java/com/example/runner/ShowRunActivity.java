package com.example.runner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.runner.databinding.ActivityBinding;
import com.example.runner.databinding.ActivityShowRunBinding;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

public class ShowRunActivity extends AppCompatActivity {

    private static final String TAG = "onItemClick";
    ActivityShowRunBinding binding;
    private Bundle extras;

    private String timestamp, distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_);
        binding = ActivityShowRunBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        extras = getIntent().getExtras();
        if (extras != null) {
            //time = extras.getString("time");
            distance = extras.getString("distance");
            timestamp = extras.getString("timestamp");
            Log.d(TAG, "SHOW RUN ACTIVITY onItemClick: "+ distance);
            Log.d(TAG, "SHOW RUN ACTIVIT onItemClick: "+ timestamp);
        }

        binding.textView5555.setText(distance + "   " + timestamp);

    }

}