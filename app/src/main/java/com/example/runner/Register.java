package com.example.runner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.runner.data.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Register extends AppCompatActivity {

    private EditText editName;
    private EditText editEmail;
    private EditText editPass;
    private EditText editConfirmPass;
    private Button registerBtn;
    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBar;
    private TextView toLogin2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editName = findViewById(R.id.etName);
        editEmail = findViewById(R.id.etEmail);
        editPass = findViewById(R.id.etPassword);
        registerBtn = findViewById(R.id.btn_reg_register);
        progressBar = findViewById(R.id.progressBar);
        toLogin2 = findViewById(R.id.toLogin2);
        editConfirmPass = findViewById(R.id.etConfirmPass);

        //DEFAULT USER PREFERENCES
        String date = " ";
        String height = " ";
        String weight= " ";
        String gender=" ";
        String lastLogin = HomePage.getCurrentDateTime();
        String logOut="1";
        String profilePhoto="1";
        String coverPhoto = "1";
        String letsRun = "";
        boolean invitedToRun = false;
        String currentKm = "";
        boolean hasFinished = false;


        progressBar.setVisibility(View.GONE);
        firebaseAuth = FirebaseAuth.getInstance();
        //String uid = firebaseAuth.getCurrentUser().getUid();

        //TO LOGIN
        toLogin2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
                finish();
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                closeKeyBoard();
                progressBar.setVisibility(View.VISIBLE);
                String name=editName.getText().toString().trim();
                String email=editEmail.getText().toString().trim();
                String newPassword=editPass.getText().toString().trim();
                String confirmPass=editConfirmPass.getText().toString().trim();
                String passEncrypt =  encryptSha256(newPassword);

                if(name.isEmpty()){
                    editName.setError("Name is required!");
                    editName.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if(newPassword.length() < 6){
                    editPass.setError("Password length at least 6 characters!");
                    editPass.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if(!confirmPass.equals(newPassword))
                {
                    editConfirmPass.setError("Passwords not the same!");
                    editConfirmPass.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    editEmail.setError("Email is not valid !");
                    editEmail.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                }


                firebaseAuth.createUserWithEmailAndPassword(email,passEncrypt)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()) {
                                    // to do - change the ctor later we have 3 now
                                    //User newUser = new User (name,email,pass);

                                    String uid = firebaseAuth.getCurrentUser().getUid();
                                    User newUser = new User (name,email,passEncrypt,uid,date,height,weight,gender,
                                            profilePhoto,coverPhoto,lastLogin,logOut,letsRun,invitedToRun,currentKm,hasFinished);

                                    FirebaseDatabase.getInstance().getReference("users")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .setValue(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(Register.this,"Register Complete Successful!",Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(Register.this,Login.class));
                                                finish();
                                            }else
                                                Toast.makeText(Register.this,"Register Failed!",Toast.LENGTH_SHORT).show();
                                                progressBar.setVisibility(View.GONE);

                                        }
                                    });
                                }else
                                    Toast.makeText(Register.this,"Error Create User with email!",Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Register.this,e.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });

            }
        });


    }

    private void closeKeyBoard() {
        View view = this.getCurrentFocus();
        if (view != null)
        {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    //STORE PASS WITH HASH SHA256- secure one-way hash-cannot be decrypt
    public static String encryptSha256(String base) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

}