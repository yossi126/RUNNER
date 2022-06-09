package com.example.runner;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.runner.databinding.ActivityLoginBinding;
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

    //CREATE HASH PASSWORD
    private static String hashPassword(String password) {
        String hashPass = Register.encryptSha256(password);
        return hashPass;
    }

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
                binding.progressBar.setVisibility(View.VISIBLE);
                userLogin();
            }
        });

        binding.forgotPassTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRecoverPasswordDialog();
            }
        });

        binding.toSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d("TAG", "onBackPressed: ");
    }

    private void showRecoverPasswordDialog() {
        AlertDialog dialogRecover = new AlertDialog.Builder(Login.this)
                .setTitle("Recover password")
                .setPositiveButton("Send", null)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();

        final EditText recoverEmailEt = new EditText(this);
        recoverEmailEt.setHint("Email");
        dialogRecover.setView(recoverEmailEt);
        recoverEmailEt.requestFocus();
        dialogRecover.show();

        Button positiveButtonR = dialogRecover.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButtonR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = recoverEmailEt.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(Login.this, "Enter Valid Email", Toast.LENGTH_SHORT).show();
                    recoverEmailEt.setError("error");
                } else {
                    sendEmail(email);
                }
            }
        });


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
        String email = binding.etEmail.getText().toString().trim();
        //String email2 =  binding.
        String password = binding.etPassword.getText().toString().trim();

        //CHECK PASS HASH SHA256
        password = hashPassword(password);

        if (email.isEmpty()) {
            binding.loginEnterEmailEt.setError("Email is requierd");
            binding.loginEnterEmailEt.requestFocus();
            binding.progressBar.setVisibility(View.GONE);
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.loginEnterEmailEt.setError("please fill valid Email");
            binding.loginEnterEmailEt.requestFocus();
            binding.progressBar.setVisibility(View.GONE);
            return;
        }
        if (password.isEmpty()) {
            binding.loginEnterPassEt.setError("Password is requierd");
            binding.loginEnterPassEt.requestFocus();
            binding.progressBar.setVisibility(View.GONE);
            return;
        }
        if (password.length() < 6) {
            binding.loginEnterPassEt.setError("Min password length is 6 characters");
            binding.loginEnterPassEt.requestFocus();
            binding.progressBar.setVisibility(View.GONE);
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
                    binding.progressBar.setVisibility(View.GONE);
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

}