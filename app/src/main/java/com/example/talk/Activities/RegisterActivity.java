package com.example.talk.Activities;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.talk.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class RegisterActivity extends AppCompatActivity {


    ImageView imguserPhoto;
    static int PermissionReqCode = 1;
    static int INTENTREQUESTCODE = 1;
    Uri pickedImageUri;

    private EditText userName, userEmail, userPass, userPassConfirm;
    private ProgressBar loadingProgressBar;
    private Button regButton;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        userEmail = findViewById(R.id.regEmailTextField);
        userName = findViewById(R.id.regNameTextField);
        userPass = findViewById(R.id.regPassTextField);
        userPassConfirm = findViewById(R.id.regPassConfirmTextField);
        loadingProgressBar = findViewById(R.id.regProgressBar);
        regButton = findViewById(R.id.regButton);

        // in the beginning loadingProgressBar is hidden
        loadingProgressBar.setVisibility(View.INVISIBLE);


        // handle what happens when regButton is clicked
        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regButton.setVisibility(View.INVISIBLE);
                loadingProgressBar.setVisibility(View.VISIBLE);
                final String name = userName.getText().toString();
                final String email = userEmail.getText().toString();
                final String pass = userPass.getText().toString();
                final String passConfirm = userPassConfirm.getText().toString();
                firebaseAuth = FirebaseAuth.getInstance();

                if(formValidated(name, email, pass, passConfirm)) {
                    createUserAccount(email, name, pass);
                }

            }
        });


        // add your own image for profile
        imguserPhoto = findViewById(R.id.regUserPhoto);
        imguserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= 22){
                    checkAndRequestForPermission();
                } else {
                    openGallery();
                }
            }
        });

    }

    private boolean formValidated(String name, String email, String pass, String passConfirm) {
        if(name.isEmpty() || email.isEmpty() || pass.isEmpty() || passConfirm.isEmpty()) {
            showMessage("Please fill out all fields!");
        } else if(pass.length() < 6) {
            showMessage("Password should be at least 6 characters long!");
        } else if(!(pass.equals(passConfirm))) {
            showMessage("Passwords don't match!");
        } else {
            return true;
        }
        regButton.setVisibility(View.VISIBLE);
        loadingProgressBar.setVisibility(View.INVISIBLE);
        return false;
    }

    private void createUserAccount(String email, final String name, String pass) {
        firebaseAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            showMessage("Account created successfully!");
                            updateUserInfo(name, pickedImageUri, firebaseAuth.getCurrentUser());
                        } else {
                            showMessage("Account creation failed. Please try again!");
                            regButton.setVisibility(View.VISIBLE);
                            loadingProgressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }

    private void updateUserInfo(final String name, Uri pickedImageUri, final FirebaseUser currentUser) {

        if(pickedImageUri == null){
            pickedImageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                    "://" + getResources().getResourcePackageName(R.drawable.userphoto)
                    + '/' + getResources().getResourceTypeName(R.drawable.userphoto) + '/' + getResources().getResourceEntryName(R.drawable.userphoto) );
            // = Uri.parse("android.resource://com.example.talk/drawable/R.drawable.userphoto");
            imguserPhoto.setImageURI(pickedImageUri);
        }

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("users_photos");
        final StorageReference imageFilePath = storageReference.child(pickedImageUri.getLastPathSegment());

        final Uri finalPickedImageUri = pickedImageUri;

        imageFilePath.putFile(pickedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // image uploaded successfully, now get reference to image uri

                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name).setPhotoUri(uri).build();

                        currentUser.updateProfile(profileUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            showMessage("Profile updated successfully!");
                                            updateUi();
                                        }
                                    }
                                });
                    }
                });
            }
        });
    }

    private void updateUi() {
        Intent homeActivity = new Intent(getApplicationContext(), HomeNav.class);
        startActivity(homeActivity);
        finish();
    }

    private void showMessage(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    private void checkAndRequestForPermission() {
        
        if(ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {

            if(ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                Toast.makeText(RegisterActivity.this, "Kindly accept the permission request", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PermissionReqCode);
            }
            
        } else {
            openGallery();
        }
        
    }

    private void openGallery() {

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, INTENTREQUESTCODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == INTENTREQUESTCODE && data != null) {

            // this means, user have successfully picked an image/file from the gallery intent
            // in here, we'll save its reference to an Uri variable
            pickedImageUri = data.getData();
            imguserPhoto.setImageURI(pickedImageUri);

        }

    }
}
