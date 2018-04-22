// <header>
// Module: MainActivityStudent
// Date of creation: 14-04-17
// Author : Rohit Pant

// Modification history:
// 	14-04-17: Created module with initialization functions
// 	20-04-17: Documented code.

// Synopsis:
//      This activity is basically the home activity of a student. The student is redirected
//      here after signing in/signing up. In this activity the student can fulfill his/her use
//      cases of joining a session in a course, enrolling for  a course or viewing his past
//      records by clicking on their respective buttons. The student may also logout from here.

// Global variables:
//      private Button mEnrolCourseButton;
//      private Button mJoinSessionButton;
//      private Button mMyPastRecordsButton;

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

public class MainActivityStudent extends BaseActivity implements View.OnClickListener{

    //Buttons
    private Button mEnrolCourseButton;
    private Button mJoinSessionButton;
    private Button mMyPastRecordsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Linking Java objects with User Interface elements
        setContentView(R.layout.activity_main_stud);
        mEnrolCourseButton = findViewById(R.id.button_enrol_course);
        mJoinSessionButton = findViewById(R.id.button_join_session);
        mMyPastRecordsButton = findViewById(R.id.button_past_records_stud);

        //Setting Button on click listeners
        mEnrolCourseButton.setOnClickListener(this);
        mJoinSessionButton.setOnClickListener(this);
        mMyPastRecordsButton.setOnClickListener(this);
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
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.button_enrol_course) {              //If enrol course button is clicked, redirect to EnrolCourseActivity
            startActivity(new Intent(this, com.software.vivek.notifierecosytem2
                    .EnrolCourseActivity.class));
        }
        else if (i == R.id.button_join_session) {        //If join session button is clicked, redirect to JoinSessionActivity
            startActivity(new Intent(this, com.software.vivek.notifierecosytem2
                    .JoinSessionActivity.class));
        }
        else if (i == R.id.button_past_records_stud) {    //If view past records button is clicked, redirect to StudentPastRecordsActivity
            startActivity(new Intent(this, StudentPastRecordsActivity.class));
        }

    }
}
