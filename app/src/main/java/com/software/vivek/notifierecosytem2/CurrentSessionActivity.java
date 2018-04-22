// <header>
// Module: CurrentSessionActivity
// Date of creation: 14-04-17
// Author : Rohit Pant

// Modification history:
// 	14-04-17: Created module with initialization functions
// 	20-04-17: Documented code.

// Synopsis:
// 	    The professor is redirected to this activity from the create session activity
//      whenever he creates a session in a course. In this activity a list of records
//      of students not paying attention along with the timestamp and response status is
//      displayed. The professor may end session any time by clicking the stop session
//      button.

// Global variables:
//        private ArrayList<String> defaultersList = new ArrayList<>();
//        private FirebaseAuth mAuthentication;
//        private DatabaseReference mDatabase;
//        private DatabaseReference mDatabaseReference;
//        private ArrayAdapter arrayAdapter;
//        private Button mStopSessionButton;
//        private String courseName

// Functions:
// 	    void onCreate(Bundle savedInstanceState)
// 	    void onDataChange()
// 	    void onChildAdded()
// 	    void onClick(View view)
// </header>

// Package declaration of our application
package com.software.vivek.notifierecosytem2;

//Files that need to be imported for smooth functioning of the module
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.software.vivek.notifierecosytem2.models.Record;

import java.util.ArrayList;

public class CurrentSessionActivity extends AppCompatActivity implements View.OnClickListener {

    //Declaring array list to store records of defaulting students
    private ArrayList<String> defaultersList= new ArrayList<>();
    //Declaring objects to interface with Firebase Database
    private FirebaseAuth mAuthentication;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseReference;
    //Declaring an array adapter object to combine with List View
    private ArrayAdapter arrayAdapter;
    //Stop Session Button
    private Button mStopSessionButton;
    private String courseName="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initializing Firebase Database objects
        mAuthentication = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabaseReference = mDatabase.child("profRecords").child(mAuthentication.getCurrentUser().getUid())
                .child(courseName);

        //Linking Java objects to user interface elements
        setContentView(R.layout.activity_current_session);
        mStopSessionButton = findViewById(R.id.button_stop_session);
        ListView defaultersListView = findViewById(R.id.defaulter_list);

        //Initializing array adapter with various layouts and defaulter's list
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, defaultersList);

        //Setting on click listener for Stop Session Button
        mStopSessionButton.setOnClickListener(this);

        //Getting snapshot value currently active session of the user from the database
        FirebaseDatabase.getInstance().getReference().child("users").child("professors")
                .child(mAuthentication.getCurrentUser().getUid()).child("activeSessions")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String studentRecord = dataSnapshot.getValue(String.class);
                        courseName = studentRecord;                                 //Storing snapshot value
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        //Linking array adapter with list view
        defaultersListView.setAdapter(arrayAdapter);

        //Adding child event listener to dynamically update defaulter's list on professor's screen
        mDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot recordSnapshot, String s) {
                for(DataSnapshot snapshot : recordSnapshot.getChildren()){          //Traversing all children of Database reference
                    Record record = snapshot.getValue(Record.class);              //Data snapshot of Record data type
                    if(!defaultersList.contains(record)){                         //If item not in list
                        defaultersList.add(record.message+"\t"+record.responded); //Add item to list
                    }
                }
                arrayAdapter.notifyDataSetChanged();
            }
            @Override
            public void onChildChanged(DataSnapshot recordSnapshot, String s) { }
            @Override
            public void onChildRemoved(DataSnapshot recordSnapshot) { }
            @Override
            public void onChildMoved(DataSnapshot recordSnapshot, String s) { }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

    }

    @Override
    public void onClick(View view) {
        //Updating database values when Stop Session Button is pressed
        mDatabase.child("users").child("professors").child(mAuthentication.getCurrentUser().getUid())
                .child("activeSessions").setValue("None");
        mDatabase.child("enrolled").child(courseName).child("session").setValue("inactive");
        mDatabase.child("enrolled").child(courseName).child("students")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot recordSnapshot) {
                        for(DataSnapshot snapshot: recordSnapshot.getChildren()){
                            String studentRecord = snapshot.getValue(String.class);
                            mDatabase.child("users").child("students").child(studentRecord)
                                    .child("currentSession").setValue("None");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


        //Redirecting professor to his Main Activity
        startActivity(new Intent(CurrentSessionActivity.this, MainActivityProfessor.class));
    }
}