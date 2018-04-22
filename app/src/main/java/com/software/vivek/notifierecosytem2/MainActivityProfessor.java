// <header>
// Module: MainActivityProfessor
// Date of creation: 14-04-17
// Author : Rohit Pant

// Modification history:
// 	14-04-17: Created module with initialization functions
// 	20-04-17: Documented code.

// Synopsis:
//      This activity is basically the home activity of a professor. The professor is redirected
//      here after signing in/signing up. In this activity the professor can fulfill his/her use
//      cases of creating a session in a course and creating a course by clicking on their
//      respective buttons. The professor may also logout from here.

// Global variables:
//      private Button mCreateCourseButton;
//      private Button mCreateSessionButton;

// Functions:
// 	    void onCreate(Bundle savedInstanceState)
//      void onCreateOptionsMenu(Menu menu)
// 	    void onOptionsItemSelected(MenuItem item)
// 	    void onClick(View view)
// </header>

// Package declaration of our application
package com.software.vivek.notifierecosytem2;

//Files that need to be imported for smooth functioning of the module
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivityProfessor extends BaseActivity implements View.OnClickListener{

    //Buttons
    private Button mCreateCourseButton;
    private Button mCreateSessionButton;
//    private Button mPastRecords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Linking Java objects to User Interface elements
        setContentView(R.layout.activity_main_prof);
        mCreateCourseButton = findViewById(R.id.button_create_course);
        mCreateSessionButton = findViewById(R.id.button_create_session);
//        mPastRecords = findViewById(R.id.button_past_records_prof);
        //Setting on click listeners on buttons
        mCreateCourseButton.setOnClickListener(this);
        mCreateSessionButton.setOnClickListener(this);
//        mPastRecords.setOnClickListener(this);
    }

    //Creating navigation menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    //Used to logout user
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.action_logout) {                 //If logout option is selected
//
            FirebaseAuth.getInstance().signOut();      //Logout using Firebase Authentication
            startActivity(new Intent(this, com.software.vivek.notifierecosytem2
                    .SignInActivity.class));           //Redirect user to SignInActivity
            finish();
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v){
        int i = v.getId();
        if (i == R.id.button_create_course) {         //If create course button is clicked, redirect professor to CreateCourseActivity
            startActivity(new Intent(MainActivityProfessor.this, CreateCourseActivity.class));
        }
        else if (i == R.id.button_create_session) {    //If create session button is clicked, redirect professor to CreateSessionActivity
            startActivity(new Intent(MainActivityProfessor.this, CreateSessionActivity.class));
        }
//        else if(i == R.id.button_past_records_prof){
//            startActivity(new Intent(MainActivityProfessor.this, PastRecordsActivityProf.class));
//        }
    }

}
