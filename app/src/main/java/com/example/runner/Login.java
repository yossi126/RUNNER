package com.example.runner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.example.runner.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
//ctrl + alt + L
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
        //enterEmail = findViewById(R.id.edit_text_name_register);


        binding.loginActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin();
            }
        });

        binding.forgotPassTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRecoverPasswordDailog();
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d("TAG", "onBackPressed: ");
    }

    private void showRecoverPasswordDailog() {
        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(this);
        final EditText recoverEmailEt = new EditText(this);
        recoverEmailEt.setHint("Email");
        recoverEmailEt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        materialAlertDialogBuilder.setView(recoverEmailEt);


        materialAlertDialogBuilder.setTitle("Recover password").setNeutralButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).setPositiveButton("send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String email = recoverEmailEt.getText().toString().trim();
                sendEmail(email);
                dialogInterface.dismiss();
            }
        }).show();




//                 1
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Recover password");
//
//        LinearLayout linearLayout = new LinearLayout(this);
//
//        final EditText recoverEmail = new EditText(this);
//        recoverEmail.setHint("Email");
//        recoverEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
//        recoverEmail.setMinEms(16);
//
//        linearLayout.addView(recoverEmail);
//        linearLayout.setPadding(10,10,10,10);
//
//        builder.setView(linearLayout);
//
//        builder.setPositiveButton("send", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                String email = recoverEmail.getText().toString().trim();
//                if(email.isEmpty()){
//                    recoverEmail.setError("Email is requierd");
//                    recoverEmail.requestFocus();
//                    return;
//                }
//                sendEmail(email);
//                dialogInterface.dismiss();
//            }
//        });
//
//        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                dialogInterface.dismiss();
//            }
//        });
//
//        builder.create().show();

    }

    private void sendEmail(String email) {
        if (email.isEmpty()) {
            Toast.makeText(Login.this, "no email provided", Toast.LENGTH_LONG).show();
        } else {
            firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(Login.this, "check your email bitch", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Login.this, "Failed to sent email", Toast.LENGTH_SHORT).show();

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Login.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void userLogin() {
        String email = binding.loginEnterEmailEt.getText().toString().trim();
        //String email2 =  binding.
        String password = binding.loginEnterPassEt.getText().toString().trim();

        //CHECK PASS HASH SHA256
        password = hashPassword(password);

        if (email.isEmpty()) {
            binding.loginEnterEmailEt.setError("Email is requierd");
            binding.loginEnterEmailEt.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.loginEnterEmailEt.setError("please fill valid Email");
            binding.loginEnterEmailEt.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            binding.loginEnterPassEt.setError("Password is requierd");
            binding.loginEnterPassEt.requestFocus();
            return;
        }
        if (password.length() < 6) {
            binding.loginEnterPassEt.setError("Min password length is 6 characters");
            binding.loginEnterPassEt.requestFocus();
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // send verified email to user
                    // to do ---> Verification not working now....
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    startActivity(new Intent(Login.this, HomePage.class));
                    finish();
//                    if(firebaseUser.isEmailVerified()){
//                        startActivity(new Intent(Login.this, HomePage.class));
//                    }else{
//                        firebaseUser.sendEmailVerification();
//                        Toast.makeText(Login.this,"check your email bitch",Toast.LENGTH_SHORT).show();
//                    }
                } else {
                    Toast.makeText(Login.this, "Failed to login", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("onFailure", "onComplete: " + e.getMessage());
            }
        });
    }

    //CREATE HASH PASSWORD
    private static String hashPassword(String password) {

        String hashPass= Register.encryptSha256(password);
        return hashPass;
    }

}