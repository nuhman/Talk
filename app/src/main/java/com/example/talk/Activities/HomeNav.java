package com.example.talk.Activities;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.talk.Fragments.HomeFragment;
import com.example.talk.Fragments.ProfileFragment;
import com.example.talk.Fragments.SettingsFragment;
import com.example.talk.Modals.Post;
import com.example.talk.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class HomeNav extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private static final int PermissionReqCode = 2;
    private static final int INTENTREQUESTCODE = 2;
    FirebaseUser currentUser;
    FirebaseAuth firebaseAuth;
    private DrawerLayout mDrawerLayout;
    Dialog popupAddPost;
    ImageView popupUserImage, popupPostImage, PopupAddBtnImage;
    TextView popupTitle, popupDesc;
    ProgressBar popupProgressBar;
    private Uri pickedImageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_nav);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupAddPost.show();
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // initialize popup for creating new posts
        initializePopup();

        handlePopupImageUploadClick();

        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        updateHeaderInfo();
    }

    private void handlePopupImageUploadClick() {
        popupPostImage.setOnClickListener(new View.OnClickListener() {
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

    private void checkAndRequestForPermission() {

        if(ContextCompat.checkSelfPermission(HomeNav.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if(ActivityCompat.shouldShowRequestPermissionRationale(HomeNav.this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                Toast.makeText(HomeNav.this, "Kindly accept the permission request", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(HomeNav.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PermissionReqCode);
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
            popupPostImage.setImageURI(pickedImageUri);

        }

    }

    private void initializePopup() {
        popupAddPost = new Dialog(this);
        popupAddPost.setContentView(R.layout.popup_add_post);
        popupAddPost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupAddPost.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.WRAP_CONTENT);
        popupAddPost.getWindow().getAttributes().gravity = Gravity.TOP;


        // initialize popup widgets
        popupTitle = popupAddPost.findViewById(R.id.popup_title);
        popupDesc = popupAddPost.findViewById(R.id.popup_desc);
        popupUserImage = popupAddPost.findViewById(R.id.popup_profile_photo);
        popupPostImage = popupAddPost.findViewById(R.id.popup_bg);
        PopupAddBtnImage = popupAddPost.findViewById(R.id.popup_create_icon);
        popupProgressBar = popupAddPost.findViewById(R.id.popup_progressBar);

        Glide.with(HomeNav.this)
                .load(currentUser.getPhotoUrl())
                .into(popupUserImage);

        PopupAddBtnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupProgressBar.setVisibility(View.VISIBLE);
                PopupAddBtnImage.setVisibility(View.INVISIBLE);
                if(validateFields()) {

                    if(pickedImageUri == null) {
                        pickedImageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                                "://" + getResources().getResourcePackageName(R.drawable.post_default)
                                + '/' + getResources().getResourceTypeName(R.drawable.post_default) + '/' + getResources().getResourceEntryName(R.drawable.post_default) );
                        // = Uri.parse("android.resource://com.example.talk/drawable/R.drawable.userphoto");
                        popupPostImage.setImageURI(pickedImageUri);
                    }

                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("blog_images");
                    final StorageReference imageFilePath = storageReference.child(pickedImageUri.getLastPathSegment());
                    imageFilePath.putFile(pickedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageDownloadLink = uri.toString();
                                    Post post = new Post(currentUser.getUid(), popupTitle.getText().toString(), popupDesc.getText().toString(), imageDownloadLink, currentUser.getPhotoUrl().toString());
                                    addPost(post);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    showMessage(e.getMessage());
                                    popupProgressBar.setVisibility(View.INVISIBLE);
                                    PopupAddBtnImage.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    });

                } else {
                    showMessage("Please fill out all fields!");
                    popupProgressBar.setVisibility(View.INVISIBLE);
                    PopupAddBtnImage.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    private void addPost(Post post) {

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = db.getReference("posts").push();

        String key = dbRef.getKey();
        post.setPostKey(key);

        dbRef.setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                showMessage("Post added successfully!");
                popupProgressBar.setVisibility(View.INVISIBLE);
                PopupAddBtnImage.setVisibility(View.VISIBLE);
                popupAddPost.dismiss();
            }
        });

    }

    private boolean validateFields() {

        if(!(popupTitle.getText().toString().isEmpty()) &&  !(popupDesc.getText().toString().isEmpty())) {
            return true;
        }
        return false;
    }

    private void showMessage(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_nav, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            getSupportActionBar().setTitle("Home");
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        } else if (id == R.id.nav_profile) {
            getSupportActionBar().setTitle("My Profile");
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
        } else if (id == R.id.nav_manage) {
            getSupportActionBar().setTitle("Settings");
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment()).commit();
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_logout) {
            firebaseAuth.signOut();
            Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(loginActivity);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void updateHeaderInfo(){
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView username = headerView.findViewById(R.id.nav_username);
        TextView usermail = headerView.findViewById(R.id.nav_usermail);
        ImageView userphoto = headerView.findViewById(R.id.nav_userphoto);

        username.setText(currentUser.getDisplayName());
        usermail.setText(currentUser.getEmail());

        Uri a = currentUser.getPhotoUrl();
        Glide.with(userphoto)
                .load(currentUser.getPhotoUrl())
                .placeholder(R.drawable.userphoto)
                .dontAnimate()
                .into(userphoto);

        userphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                mDrawerLayout.closeDrawers();
            }
        });

    }


}
