package com.example.runner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.runner.databinding.ActivityLoginBinding;
import com.example.runner.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    ActivityLoginBinding binding;
    //EditText enterEmail;
    //EditText enterPassword;
    FirebaseAuth firebaseAuth;
    //Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_login);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        firebaseAuth = FirebaseAuth.getInstance();


        binding.loginActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin();
            }
        });

        binding.resetPassBtnLoginActivitiy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this,ForgotPassword.class));
            }
        });




    }

    private void userLogin() {
        String email = binding.loginEnterEmailEt.getText().toString().trim();
        String password = binding.loginEnterPassEt.getText().toString().trim();

        if(email.isEmpty()){
            binding.loginEnterEmailEt.setError("Email is requierd");
            binding.loginEnterEmailEt.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.loginEnterEmailEt.setError("please fill valid Email");
            binding.loginEnterEmailEt.requestFocus();
            return;
        }
        if(password.isEmpty()){
            binding.loginEnterPassEt.setError("Password is requierd");
            binding.loginEnterPassEt.requestFocus();
            return;
        }
        if(password.length()<6){
            binding.loginEnterPassEt.setError("Min password length is 6 characters");
            binding.loginEnterPassEt.requestFocus();
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    // send verified email to user
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    if(firebaseUser.isEmailVerified()){
                        startActivity(new Intent(Login.this, MainProfile.class));
                    }else{
                        firebaseUser.sendEmailVerification();
                        Toast.makeText(Login.this,"check your email bitch",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(Login.this,"Failed to login",Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Login.this,e.getMessage(),Toast.LENGTH_LONG).show();
                Log.d("onFailure", "onComplete: "+e.getMessage());
            }
        });
    }


}