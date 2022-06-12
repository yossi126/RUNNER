package com.example.runner;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.runner.data.User;
import com.example.runner.databinding.ActivityProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.ParseException;
import java.util.Date;

public class Profile extends AppCompatActivity {

    private ActivityProfileBinding binding;

    //CAMERA AND STORAGE PERMISSION AND HANDLES
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 101;
    String[] cameraPermissions;
    String[] storagePermissions;
    private String profileOrCoverPhoto;
    private String cameraOrGalley;

    // INSTANCE FOR FIREBASE STORAGE AND STORAGE REF
    FirebaseStorage storage;
    StorageReference storageReference;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private String userID;
    private FloatingActionButton floatingEditButton;


    // Uri INDICATES WHERE THE IMAGE WILL BE PICKED FROM
    Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_profile);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //GET THE FIREBASE STORAGE REF
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //INIT ARRAYS OF PERMISSIONS CAMERA & STORAGE
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //GET USER PROFILE PREFERENCES
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        userID = firebaseUser.getUid();

        databaseReference.child(userID).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User user = snapshot.getValue(User.class);
                if (user != null) {
                    binding.userName.setText(user.getName());
                    binding.userEmail.setText(user.getEmail());
                    binding.userDate.setText(user.getDate());
                    binding.userHeight.setText(user.getHeight() + " cm");
                    binding.userWeight.setText(user.getWeight() + " kg");
                    binding.userGender.setText(user.getGender());
                    if (user.getCoverPhoto().equals("1") || user.getCoverPhoto() == null)
                        //DEFAULT COVER PHOTO
                        binding.coverPhoto.setImageResource(R.drawable.background);
                    else {
                        getCoverImage(user.getUid());
                    }
                    if (user.getProfilePhoto().equals("1") || user.getCoverPhoto() == null)
                        //DEFAULT PROFILE PHOTO
                        binding.profilePhoto.setImageResource(R.drawable.profile);
                    else
                        //GET STORAGE IMAGE USING GLIDE
                        getProfileImage(user.getUid());
                }
                //PROGRESS BAR DELAYED BY 2.5 SEC
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run()
                    {
                        binding.progressBar.setVisibility(View.GONE);
                    }
                }, 2500);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        //SETTING FLOATING BUTTON- TO OPEN DIALOG EDIT MENU
        String[] editValues = {"Edit Name", "Edit Birthdate", "Edit Height", "Edit Weight", "Edit Gender",
                "Edit Profile Photo", "Edit Cover Photo"};
        //INIT DIALOG MENU
        binding.floatingEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            //MAIN EDIT MENU
            public void onClick(View view) {
                MaterialAlertDialogBuilder mainBuilder = new MaterialAlertDialogBuilder(Profile.this);
                mainBuilder.setTitle("Edit User Profile");
                mainBuilder.setItems(editValues, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            //EDIT NAME
                            case 0:
                                AlertDialog dialogN = new AlertDialog.Builder(Profile.this)
                                        .setTitle("Edit Name")
                                        .setPositiveButton("Update", null)
                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                mainBuilder.show();
                                            }
                                        })
                                        .create();

                                final EditText editName = new EditText(Profile.this);
                                editName.setInputType(InputType.TYPE_CLASS_TEXT);
                                dialogN.setView(editName);
                                editName.requestFocus();
                                dialogN.show();

                                Button positiveButton = dialogN.getButton(AlertDialog.BUTTON_POSITIVE);
                                positiveButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        final String value = editName.getText().toString().trim();
                                        if (TextUtils.isEmpty(value)) {
                                            Toast.makeText(Profile.this, "Enter Name", Toast.LENGTH_SHORT).show();
                                            editName.setError("error");
                                        } else {
                                            //UPDATE USER VALUE
                                            databaseReference.getRef().child(userID).child("name").setValue(value).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    databaseReference = FirebaseDatabase.getInstance().getReference("users");
                                                    Toast.makeText(Profile.this, "Name Update Successful " + value, Toast.LENGTH_SHORT).show();
                                                    dialogN.dismiss();
                                                }
                                            });
                                        }
                                    }
                                });
                                break;

                            //EDIT HEIGHT
                            case 2:
                                AlertDialog dialogH = new AlertDialog.Builder(Profile.this)
                                        .setTitle("Edit Height")
                                        .setPositiveButton("Update", null)
                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                mainBuilder.show();
                                            }
                                        })
                                        .create();

                                final EditText editHeight = new EditText(Profile.this);
                                editHeight.setHint("In Centimeters (cm)");
                                editHeight.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                                dialogH.setView(editHeight);
                                editHeight.requestFocus();
                                dialogH.show();

                                //KEEP DIALOG OPEN WHEN BUTTON IS PRESSED
                                Button positiveButtonH = dialogH.getButton(AlertDialog.BUTTON_POSITIVE);
                                positiveButtonH.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        final String value = editHeight.getText().toString().trim();
                                        if (TextUtils.isEmpty(value)) {
                                            Toast.makeText(Profile.this, "Enter Height", Toast.LENGTH_SHORT).show();
                                            editHeight.setError("error");
                                        } else {
                                            //UPDATE USER VALUE
                                            databaseReference.getRef().child(userID).child("height").setValue(value).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    databaseReference = FirebaseDatabase.getInstance().getReference("users");
                                                    Toast.makeText(Profile.this, "Height Update Successful " + value + " cm", Toast.LENGTH_SHORT).show();
                                                    dialogH.dismiss();
                                                }
                                            });
                                        }
                                    }
                                });
                                break;

                            //EDIT WEIGHT
                            case 3:
                                AlertDialog dialogW = new AlertDialog.Builder(Profile.this)
                                        .setTitle("Edit Weight")
                                        .setPositiveButton("Update", null)
                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                mainBuilder.show();
                                            }
                                        })
                                        .create();

                                final EditText editWeight = new EditText(Profile.this);
                                editWeight.setHint("In Kilograms (kg)");
                                editWeight.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                                dialogW.setView(editWeight);
                                editWeight.requestFocus();
                                dialogW.show();

                                Button positiveButtonW = dialogW.getButton(AlertDialog.BUTTON_POSITIVE);
                                positiveButtonW.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        final String value = editWeight.getText().toString().trim();
                                        if (TextUtils.isEmpty(value)) {
                                            Toast.makeText(Profile.this, "Enter Weight", Toast.LENGTH_SHORT).show();
                                            editWeight.setError("error");
                                        } else {
                                            //UPDATE USER VALUE
                                            databaseReference.getRef().child(userID).child("weight").setValue(value).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    databaseReference = FirebaseDatabase.getInstance().getReference("users");
                                                    Toast.makeText(Profile.this, "Weight Update Successful " + value + " kg", Toast.LENGTH_SHORT).show();
                                                    dialogW.dismiss();
                                                }
                                            });
                                        }
                                    }
                                });
                                break;

                            //EDIT BIRTHDATE
                            case 1:
                                //CREATE DATE PICKER DIALOG
                                MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();
                                materialDateBuilder.setTitleText("Select  Birthdate");

                                // CONSTRAINT LIMITS TO PICK FUTURE DATES
                                CalendarConstraints.Builder calendarConstraintBuilder = new CalendarConstraints.Builder();
                                calendarConstraintBuilder.setValidator(DateValidatorPointBackward.now());
                                materialDateBuilder.setCalendarConstraints(calendarConstraintBuilder.build());

                                final MaterialDatePicker materialDatePicker = materialDateBuilder.build();
                                materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");

                                materialDatePicker.addOnPositiveButtonClickListener(
                                        new MaterialPickerOnPositiveButtonClickListener() {
                                            public void onPositiveButtonClick(Object selection) {
                                                String value = materialDatePicker.getHeaderText();
                                                //CHANGE DATE FORMAT
                                                value = parseDate(value);
                                                //UPDATE USER VALUE
                                                databaseReference.getRef().child(userID).child("date").setValue(value).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        Toast.makeText(Profile.this, "Date Update Successful ", Toast.LENGTH_SHORT).show();
                                                        materialDatePicker.dismiss();
                                                    }
                                                });
                                            }
                                        });
                                materialDatePicker.addOnNegativeButtonClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        mainBuilder.show();
                                        materialDatePicker.dismiss();
                                    }
                                });
                                break;

                            //EDIT GENDER
                            case 4:
                                //CREATE RADIO BUTTON DIALOG
                                String[] genders = {"Male", "Female"};
                                String[] selectedGender = new String[1];
                                selectedGender[0] = "Male";
                                AlertDialog dialogG = new AlertDialog.Builder(Profile.this)
                                        .setTitle("Choose Gender")
                                        .setPositiveButton("Update", null)
                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                mainBuilder.show();
                                            }
                                        })
                                        .setSingleChoiceItems(genders, 0, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                selectedGender[0] = genders[i].trim();
                                            }
                                        })
                                        .show();

                                Button positiveButtonG = dialogG.getButton(AlertDialog.BUTTON_POSITIVE);
                                positiveButtonG.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        final String value = selectedGender[0].trim();
                                        //UPDATE USER VALUE
                                        databaseReference.getRef().child(userID).child("gender").setValue(value).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                databaseReference = FirebaseDatabase.getInstance().getReference("users");
                                                Toast.makeText(Profile.this, "Gender Update Successful " + value, Toast.LENGTH_SHORT).show();
                                                dialogG.dismiss();
                                            }
                                        });
                                    }
                                });
                                break;

                            case 5:
                                profileOrCoverPhoto = "profile";
                                showImagePicDialog();
                                break;
                            case 6:
                                profileOrCoverPhoto = "cover";
                                showImagePicDialog();
                                break;


                        }
                    }
                });
                mainBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                mainBuilder.show();

            }
        });

        //SETTING TOP NAV BAR
        topNavBar();

        //SETTING BOTTOM NAV BAR
        bottomNavBar();

    }


    //START CAMERA ACTIVITY FOR RESULT PIC
    ActivityResultLauncher<Intent> startCamera = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result != null) {
                        //PROFILE OR COVER
                        if (profileOrCoverPhoto.equals("profile"))
                            binding.profilePhoto.setImageURI(imageUri);
                        else
                            binding.coverPhoto.setImageURI(imageUri);

                        uploadImage();
                    } else
                        Toast.makeText(Profile.this, "Error Loading Camera Photo ", Toast.LENGTH_SHORT).show();

                }
            }
    );

    //START GALLERY ACTIVITY FOR RESULT PIC
    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    if (result != null) {
                        //PROFILE OR COVER
                        if (profileOrCoverPhoto.equals("profile"))
                            binding.profilePhoto.setImageURI(result);
                        else
                            binding.coverPhoto.setImageURI(result);

                        //RESULT IS SET IN THE URI
                        imageUri = result;
                        uploadImage();
                    } else
                        Toast.makeText(Profile.this, "Error Loading Gallery Photo", Toast.LENGTH_SHORT).show();

                }
            });


    //IMAGE UPLOAD ALERT DIALOG MENU
    private void showImagePicDialog() {
        String[] options = {"Camera", "Gallery"};
        MaterialAlertDialogBuilder picBuilder = new MaterialAlertDialogBuilder(Profile.this);
        picBuilder.setTitle("Upload Photo From :");
        picBuilder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //String uploadOption = options[i];
                if (i == 0) {
                    cameraOrGalley = options[i];
                    //CAMERA CLICKED
                    checkPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE);
                } else if (i == 1) {
                    cameraOrGalley = options[i];
                    //GALLERY CLICKED
                    checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
                } else {
                    Toast.makeText(Profile.this, "Try Again....", Toast.LENGTH_SHORT).show();
                    dialogInterface.dismiss();
                }
            }
        });
        picBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        picBuilder.show();
    }

    //FUNCTION TO CHECK AND REQUEST PERMISSION
    private void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(Profile.this, permission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(Profile.this, new String[]{permission}, requestCode);
        } else {
            Toast.makeText(Profile.this, "Permission already granted", Toast.LENGTH_SHORT).show();
            if (cameraOrGalley.equals("Camera"))
                pickFromCamera();
            else
                pickFromGallery();

        }
    }

    //CHECK CAMERA AND STORAGE PERMISSION RESULT-USER PRESS ALLOW OR DENY
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (requestCode == CAMERA_PERMISSION_CODE) {
                // Checking whether user granted the permission or not.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickFromCamera();
                } else {
                    Toast.makeText(Profile.this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
                }

            } else if (requestCode == STORAGE_PERMISSION_CODE) {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickFromGallery();
                } else {
                    Toast.makeText(Profile.this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //CHOOSE PIC FROM GALLERY
    private void pickFromGallery() {
        mGetContent.launch("image/*");
    }

    //CHOOSE PIC FROM CAMERA
    private void pickFromCamera() {

        String fileName = "temp.jpg";
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, fileName);
        imageUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startCamera.launch(cameraIntent);
    }


    //UPLOAD TO STORAGE AND REALTIME
    private void uploadImage() {
        if (imageUri != null) {
            if (profileOrCoverPhoto.equals("profile")) {
                storageReference = storage.getReference().child("profile_images/" + firebaseUser.getUid());
                storageReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            //UPLOAD STRING IMAGE VALUE TO REALTIME
                            databaseReference.getRef().child(userID).child("profilePhoto").setValue(firebaseUser.getUid());
                            Snackbar snackbar = Snackbar.make(findViewById(R.id.bottomNavBar), "Image Upload Successfully!",
                                    Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        } else {
                            Toast.makeText(Profile.this, "Error Uploading Image", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                storageReference = storage.getReference().child("cover_images/" + firebaseUser.getUid());
                storageReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            //UPLOAD STRING IMAGE VALUE TO REALTIME
                            databaseReference.getRef().child(userID).child("coverPhoto").setValue(firebaseUser.getUid());
                            Snackbar snackbar = Snackbar.make(findViewById(R.id.bottomNavBar), "Image Upload Successfully!",
                                    Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        } else {
                            Toast.makeText(Profile.this, "Error Uploading Image", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }


    //GET PROFILE IMAGE
    private void getProfileImage(String uid) {
        storageReference = storage.getReference().child("profile_images/").child(uid);
        //Glide.with(Profile.this).load("https://firebasestorage.googleapis.com/v0/b/runner-3fa14.appspot.com/o/profile_images%2FpnZK4vYApdOeP51gLxHAfhwKoln2?alt=media&token=80c2c20b-0e5d-4eac-b049-af74808a92cf").into(binding.profilePhoto);
        storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Glide.with(Profile.this)
                            .load(task.getResult())
                            .apply(RequestOptions.centerCropTransform())
                            .into(binding.profilePhoto);
                } else {
                    binding.profilePhoto.setImageResource(R.drawable.profile);
                    Toast.makeText(Profile.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    //GET COVER IMAGE
    private void getCoverImage(String uid) {
        storageReference = storage.getReference().child("cover_images/").child(uid);
        //Glide.with(Profile.this).load("https://firebasestorage.googleapis.com/v0/b/runner-3fa14.appspot.com/o/profile_images%2FpnZK4vYApdOeP51gLxHAfhwKoln2?alt=media&token=80c2c20b-0e5d-4eac-b049-af74808a92cf").into(binding.profilePhoto);
        storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Glide.with(Profile.this)
                            .load(task.getResult())
                            .apply(RequestOptions.centerCropTransform())
                            .into(binding.coverPhoto);
                } else {
                    binding.coverPhoto.setImageResource(R.drawable.background);
                    Toast.makeText(Profile.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    //CHANGE DATE FORMAT EXAMPLE MAY 02,2022 --> 02/05/2022
    private String parseDate(String dateStr) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("MMM dd, yyyy");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");

        Date date = new Date(dateStr);
        String str = null;

        try {
            date = inputFormat.parse(dateStr);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return (str);
    }

    //SETTING BOTTOM NAV BAR
    private void bottomNavBar() {
        binding.bottomNavBar.setSelectedItemId(R.id.profile); // to keep the icon on
        binding.bottomNavBar.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home_page:
                        Intent intent = new Intent(Profile.this, HomePage.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        //finish();
                        break;
                    case R.id.club:
                        Intent intentC = new Intent(Profile.this, Club.class);
                        startActivity(intentC);
                        overridePendingTransition(0, 0);
                        break;
                    case R.id.run:
                        Intent intentR = new Intent(Profile.this, Run.class);
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

    //SETTING TOP NAV BAR
    private void topNavBar() {
        //SETTING TOP NAV BAR
        binding.topAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.logOut:
                        Intent intent = new Intent(Profile.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
        binding.bottomNavBar.setSelectedItemId(R.id.profile);
        overridePendingTransition(0, 0);
    }

}