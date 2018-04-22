// <header>
// Module: RespondNotification
// Date of creation: 14-04-17
// Author : Vivek Raj
// Modification history:
// 	14-04-17: Created module with initialization functions
// 	20-04-17: Documented code.
// Synopsis:
// 	    This activity is called whenever a student opens a notification.
//      The student can respond to notification using this activity.

// Global variables:
//      private static final String TAG = "RespondNotification";
//      private Button respondNotification;
//      private DatabaseReference mDatabase;
//      private FirebaseAuth mAuthentication;

// Functions:
//      void onCreate(Bundle savedInstanceState)
// 	    void onClick(View v)
//      void onDataChange(DataSnapshot dataSnapshot)
// </header>

// Package declaration of our application
package com.software.vivek.notifierecosytem2;

//Files that need to be imported for smooth functioning of the module
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RespondNotification extends AppCompatActivity {

    private static final String TAG = "RespondNotification";
    //Respond to notification Button
    private Button respondNotification;
    //Declaring object to interface with Firebase Database
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuthentication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Linking Java objects to user interface elements
        setContentView(R.layout.activity_respond_notification);
        respondNotification = findViewById(R.id.respond_notification);

        //Initializing Firebase Database object
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuthentication = FirebaseAuth.getInstance();

        //Setting an on click listener on respond to notification button
        respondNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // get value to path users/students/{currentStudentID}/currentSession
                mDatabase.child("users").child("students").child(mAuthentication.getCurrentUser().getUid())
                        .child("currentSession").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        // get current session
                        String currentSession = dataSnapshot.getValue(String.class);

                        //check if current session exist else display appropriate error
                        if(!currentSession.equals("None")){
                            if ((getIntent().getExtras() != null)&&(getIntent().getExtras().getString("uid")!=null)) {
                                //Logging event for student responding in database
                                mDatabase.child("notification").child(getIntent().getExtras().getString("uid"))
                                        .child("response").setValue("Responded");
                                mDatabase.child("profRecords").child(getIntent().getExtras().getString("profID"))
                                        .child(getIntent().getExtras().getString("currentSession"))
                                        .child(getIntent().getExtras().getString("uid"))
                                        .child("responded").setValue("Responded");

                                //close activity if the student responds
                                finish();
                            }
                        }
                        else{
                            //if the current session does not exist then display error
                            Toast.makeText(RespondNotification.this, "Session has ended."
                            + " You cannot respond to notification.", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(RespondNotification.this, MainActivityStudent.class));
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) { }
                });
            }
        });
    }
}
