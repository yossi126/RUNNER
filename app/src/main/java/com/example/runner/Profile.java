package com.example.runner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.runner.data.User;
import com.example.runner.databinding.ActivityProfileBinding;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Profile extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private String userID;
    private FloatingActionButton floatingEditButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_profile);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);



        //GET USER PREFERENCES
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        userID = firebaseUser.getUid();
        databaseReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if(user !=null){
                    binding.userName.setText(user.getName());
                    binding.userEmail.setText(user.getEmail());
                    binding.userDate.setText(user.getDate());
                    binding.userHeight.setText(user.getHeight());
                    binding.userWeight.setText(user.getWeight());
                    binding.userGender.setText(user.getGender());
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        //SETTING FLOATING BUTTON- TO OPEN DIALOG EDIT MENU
        String editValues [] = {"Edit Name","Edit Birthdate","Edit Height","Edit Weight","Edit Gender"};
        //INIT PROGRESS BAR
        binding.floatingEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(Profile.this);
                builder.setTitle("Edit User Profile");
                builder.setItems(editValues, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i)
                        {
                            case 0:
                            case 2:
                            case 3:
                                editUser(editValues[i].toString());
                                break;
                            case 1:
                                //CREATE DATE PICKER
                                MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();
                                materialDateBuilder.setTitleText("SELECT A DATE");
                                final MaterialDatePicker materialDatePicker = materialDateBuilder.build();
                                materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
                                materialDatePicker.addOnPositiveButtonClickListener(
                                        new MaterialPickerOnPositiveButtonClickListener() {
                                            public void onPositiveButtonClick(Object selection) {
                                                Toast.makeText(Profile.this,materialDatePicker.getHeaderText().toString(),Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                break;
                            case 4:

                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                builder.show();

            }
        });

        //SETTING TOP NAV BAR
        binding.topAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()){
                    case R.id.logOut:
                        Intent intent = new Intent(Profile.this,MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        FirebaseAuth.getInstance().signOut();
                        startActivity(intent);
                        break;
                }
                return true;
            }
        });

        //SETTING BOTTOM NAV
        binding.bottomNavBar.setSelectedItemId(R.id.profile); // to keep the icon on
        binding.bottomNavBar.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.home_page:
                        Intent intent = new Intent(Profile.this,HomePage.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        //finish();
                        break;
                    case R.id.club:
                        Intent intentC = new Intent(Profile.this,Club.class);
                        startActivity(intentC);
                        overridePendingTransition(0, 0);
                        break;
                    case R.id.run:
                        Intent intentR = new Intent(Profile.this,Run.class);
                        startActivity(intentR);
                        overridePendingTransition(0, 0);
                        break;
                    case R.id.status:
                        break;
                    case R.id.profile:
                        break;
                }
                return true;
            }
        });

    }

    private void editUser(String editValue) {
        MaterialAlertDialogBuilder builderEdit = new MaterialAlertDialogBuilder(Profile.this);
        final EditText userEdit = new EditText(Profile.this);
        userEdit.setInputType(InputType.TYPE_CLASS_TEXT);
        builderEdit.setView(userEdit);
        builderEdit.setTitle(editValue);
        builderEdit.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Toast.makeText(Profile.this,userEdit.getText().toString(),Toast.LENGTH_SHORT).show();
            }
        });
        builderEdit.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builderEdit.show();
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        binding.bottomNavBar.setSelectedItemId(R.id.profile);
        overridePendingTransition(0, 0);
    }

    //protected String MaterialAlertDialogBuilder



}


