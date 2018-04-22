// <header>
// Module: StudentPastRecordsActivity
// Date of creation: 14-04-17
// Author : Rohit Pant

// Modification history:
// 	14-04-17: Created module with initialization functions
// 	20-04-17: Documented code.

// Synopsis:
//      This activity serves the student's use case of viewing his past records. In this
//      activity a list of records of the student not paying attention in sessions of various
//      courses will be displayed.

// Global variables:
//      private FirebaseAuth mAuthentication;
//      private DatabaseReference mDatabase;
//      private DatabaseReference mDatabaseReference;
//      private ArrayAdapter arrayAdapter;
//      private ArrayList<String> recordsList = new ArrayList<>();

// Functions:
// 	    void onCreate(Bundle savedInstanceState)
//      void onDataChange()
// 	    void onChildAdded()
// </header>

// Package declaration of our application
package com.software.vivek.notifierecosytem2;

//Files that need to be imported for smooth functioning of the module
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.software.vivek.notifierecosytem2.models.Course;

import java.util.ArrayList;

public class StudentPastRecordsActivity extends AppCompatActivity {

    //Declaring objects to interface with Firebase Database
    private FirebaseAuth mAuthentication;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseReference;
    //Declaring an array adapter object to combine with list view
    private ArrayAdapter arrayAdapter;
    //Creating an array list to store past records of students
    private ArrayList<String> recordsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initializing Firebase Database objects
        mAuthentication = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("pastRecords").
                child(mAuthentication.getCurrentUser().getUid());

        //Linking Java objects with user interface elements
        setContentView(R.layout.activity_past_records_stud);
        ListView courseListView = findViewById(R.id.records_list_stud);

        //Initializing an array adapter with various list layouts and records list
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, recordsList);

        //Linking array adapter object with list view
        courseListView.setAdapter(arrayAdapter);

        //Adding a value event listener to database reference
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String value = snapshot.getValue(String.class);
                    if(!recordsList.contains(value)){
                        recordsList.add(value);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

        //Adding a child event listener to database reference to dynamically add new records to the list
        mDatabaseReference.addChildEventListener(new ChildEventListener() {
        @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String record = dataSnapshot.getValue(String.class);
                if(recordsList.contains(record)){
                    recordsList.add(record);
                    arrayAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
