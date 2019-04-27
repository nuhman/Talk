package com.example.talk.Activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.talk.Adapters.CommentAdapter;
import com.example.talk.Modals.Comment;
import com.example.talk.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PostDetailActivity extends AppCompatActivity {

    TextView postTitle, postDateName, postDesc;
    EditText postCommentEditText;
    ImageView postImage, postUserImage, postCommentUserImage;
    Button postCommentAddBtn;
    String postKey;
    RecyclerView postCommentsRv;
    CommentAdapter commentAdapter;
    List<Comment> commentList;
    private final static String COMMENT_DB_REF = "comments";

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);


        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getSupportActionBar().hide();

        postTitle = findViewById(R.id.post_detail_title);
        postDateName = findViewById(R.id.post_detail_date_name);
        postDesc = findViewById(R.id.post_detail_desc);
        postCommentEditText = findViewById(R.id.post_detail_add_comment_edittext);
        postImage = findViewById(R.id.post_detail_img);
        postUserImage = findViewById(R.id.post_detail_user_image);
        postCommentUserImage = findViewById(R.id.post_detail_add_comment_user_profile_photo);
        postCommentAddBtn = findViewById(R.id.post_detail_add_comment_btn);
        postCommentsRv = findViewById(R.id.post_detail_comments_rv);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

        /* Populate the fields in activity from the previous intent*/
        String intentStringData;

        intentStringData = getIntent().getExtras().getString("i_postImg");
        Glide.with(this).load(intentStringData).into(postImage);

        intentStringData = getIntent().getExtras().getString("i_userPhoto");
        Glide.with(this).load(intentStringData).into(postUserImage);

        intentStringData = getIntent().getExtras().getString("i_title");
        postTitle.setText(intentStringData);

        intentStringData = getIntent().getExtras().getString("i_desc");
        postDesc.setText(intentStringData);

        Glide.with(this).load(firebaseUser.getPhotoUrl()).into(postCommentUserImage);

        postKey = getIntent().getExtras().getString("i_key");

        String date = timestampToString(getIntent().getExtras().getLong("i_date"));
        intentStringData = getIntent().getExtras().getString("i_author");
        postDateName.setText(date + " by " + intentStringData);

        initializeCommentsRv();

        /* End populating the fields */

        postCommentAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                postCommentAddBtn.setVisibility(View.INVISIBLE);
                DatabaseReference commentReference = firebaseDatabase.getReference(COMMENT_DB_REF).child(postKey).push();
                String commentText = postCommentEditText.getText().toString();
                String userId = firebaseUser.getUid();
                String userName = firebaseUser.getDisplayName();
                String userImageUrl = firebaseUser.getPhotoUrl().toString();
                Comment comment = new Comment(userId, userName, userImageUrl, commentText);

                commentReference.setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        postCommentEditText.setText("");
                        postCommentAddBtn.setVisibility(View.VISIBLE);
                        showMessage("Comment added successfully!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showMessage("Failed to add comment: " + e.getMessage());
                    }
                });
            }
        });

    }

    private void initializeCommentsRv() {

        postCommentsRv.setLayoutManager(new LinearLayoutManager(this));

        DatabaseReference commentsRef = firebaseDatabase.getReference(COMMENT_DB_REF).child(postKey);
        commentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentList = new ArrayList<>();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Comment comment = snapshot.getValue(Comment.class);
                    commentList.add(comment);
                }
                commentAdapter =  new CommentAdapter(getApplicationContext(), commentList);
                postCommentsRv.setAdapter(commentAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showMessage(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    private String timestampToString(long time) {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy", calendar).toString();
        return date;
    }

}
