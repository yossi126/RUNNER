package com.example.runner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.example.runner.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.signMainActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //LONG  NEW INTENT
               Intent intent = new Intent(MainActivity.this,Register.class);
                startActivity(intent);
            }
        });

        binding.loginMainActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // SHORT  NEW INTENT
                startActivity(new Intent(MainActivity.this,Login.class));
            }
        });
    }
}