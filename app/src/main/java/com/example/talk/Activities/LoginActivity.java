package com.example.talk.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText loginEmail, loginPass;
    private ImageView loginPhoto;
    private Button loginButton, loginSignupButton;
    private ProgressBar loginProgressBar;
    private FirebaseAuth firebaseAuth;
    private Intent homeIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEmail = findViewById(R.id.loginEmail);
        loginButton = findViewById(R.id.loginButton);
        loginSignupButton = findViewById(R.id.loginSignupButton);
        loginPass = findViewById(R.id.loginPass);
        loginProgressBar = findViewById(R.id.loginProgressBar);
        loginPhoto = findViewById(R.id.loginImgUser);
        firebaseAuth = FirebaseAuth.getInstance();
        homeIntent = new Intent(this, HomeNav.class);

        loginSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(registerIntent);
                //finish();
            }
        });

        loginProgressBar.setVisibility(View.INVISIBLE);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginProgressBar.setVisibility(View.VISIBLE);
                loginButton.setVisibility(View.INVISIBLE);

                final String mail = loginEmail.getText().toString();
                final String pass = loginPass.getText().toString();

                if(validateForm(mail, pass)){
                    login(mail, pass);
                } else {
                    showMessage("Enter all details!");
                    loginProgressBar.setVisibility(View.INVISIBLE);
                    loginButton.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    private void login(String mail, String pass) {

        firebaseAuth.signInWithEmailAndPassword(mail, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            updateUI();
                        } else {
                            showMessage(task.getException().getMessage());
                            loginProgressBar.setVisibility(View.INVISIBLE);
                            loginButton.setVisibility(View.VISIBLE);
                        }

                    }
                });

    }

    private void updateUI() {
        startActivity(homeIntent);
        finish();
    }

    private void showMessage(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    private boolean validateForm(String mail, String pass) {
        if(mail.isEmpty() || pass.isEmpty())
            return false;
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){
            updateUI();
        }
    }
}
