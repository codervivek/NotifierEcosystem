package com.software.vivek.notifierecosytem2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class LoggedIn extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);
    }

    public void onLogoutClick(View view){
        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        startActivity(new Intent(LoggedIn.this,MainActivity.class));
    }
}
