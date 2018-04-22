// <header>
// Module: JoinSessionActivity
// Date of creation: 14-04-17
// Author : Rohit Pant

// Modification history:
// 	14-04-17: Created module with initialization functions
// 	20-04-17: Documented code.

// Synopsis:
//      This activity serves the student's use case of joining an active session in a particular
//      course. In this activity a list of all courses that the student has enrolled for will
//      be displayed. The student can select a course whose live session he wants to join by
//      by clicking a single choice selectable list. If a session is active for the selected
//      course then the student will successfully join the session else an error message will be
//      displayed.

// Global variables:
//      private ArrayList<String> courseList= new ArrayList<>();
//      private ArrayList<String> selectedItems = new ArrayList<>();
//      private FirebaseAuth mAuthentication;
//      private DatabaseReference mDatabase;
//      private DatabaseReference mDatabaseReference;
//      private ArrayAdapter arrayAdapter;
//      private Button mJoinSessionButton;

// Functions:
// 	    void onCreate(Bundle savedInstanceState)
//      void onItemClick()
// 	    void onDataChange()
// 	    void onChildAdded()
// 	    void onClick(View view)
//      void joinSession()
// </header>

// Package declaration of our application
package com.software.vivek.notifierecosytem2;

//Files that need to be imported for smooth functioning of the module
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class JoinSessionActivity extends AppCompatActivity implements View.OnClickListener {

    //Declaring an Array List for displaying courses that a student has enrolled for and to store value of session which student wants to join
    private ArrayList<String> courseList = new ArrayList<>();
    //Declaring objects to interface with Firebase Database
    private FirebaseAuth mAuthentication;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseReference;
    //Declaring an Array Adapter for combining with List View
    private ArrayAdapter arrayAdapter;
    //Join Session Button
    private Button mJoinSessionButton;
    //Course name of selected course
    private String courseName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initializing various Firebase Database objects
        mAuthentication = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabaseReference = mDatabase.child("students").child(mAuthentication.getCurrentUser().getUid()).child("courses");

        //Interlinking Java objects with User Interface elements
        setContentView(R.layout.activity_join_session);
        mJoinSessionButton = findViewById(R.id.button_join_session_fin);
        ListView courseListView = findViewById(R.id.join_course_list);
        courseListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);  //Enabling one choice selection on course list

        //Combining array adapter object various list layouts and course list
        arrayAdapter = new ArrayAdapter<>(this, R.layout.row_layout, R.id.txt_lan, courseList);

        //Setting on click listener on Join Session Button
        mJoinSessionButton.setOnClickListener(this);

        //Adding value event listener to get data snapshot of database to return all courses that student has enrolled for
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {     //Iterating over children of the database reference
                            String value = snapshot.getValue(String.class);
                            if(!courseList.contains(value)){                           //If course list does not contain data snapshot value
                                courseList.add(value);                                 //Add value to course list
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

        //Linking array adapter object with course list view
        courseListView.setAdapter(arrayAdapter);

        //Setting an item click listener on the course list to identify item that has been clicked on
        courseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long length) {
                String selectedItem = ((TextView) view).getText().toString();
                courseName = selectedItem;
            }
        });

        //Adding child event listener on Database Reference
        mDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String value = dataSnapshot.getValue(String.class);
                if(!courseList.contains(value)){                        //If data snapshot value not in list
                    courseList.add(value);                              //Add item to the list
                }
                arrayAdapter.notifyDataSetChanged();
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

    public void joinSession(){
        final String course = courseName;
        mDatabase.child("users").child("students").child(mAuthentication.getCurrentUser().getUid())
                .child("currentSession").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                if(!value.equals("None")){
                    Toast.makeText(JoinSessionActivity.this, "You can join only one "+
                            "live session at a time", Toast.LENGTH_SHORT).show();
                }
                else{
                    //Checking if the course requested to be joined by the student is active or not using a value event listener
                    mDatabase.child("enrolled").child(course).child("session")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String value = dataSnapshot.getValue(String.class);
                                    if(value.equals("active")){     //If course session is active
                                        //Change student current session attribute to selected item
                                        mDatabase.child("users").child("students")
                                                .child(mAuthentication.getCurrentUser().getUid())
                                                .child("currentSession").setValue(course);
                                        //Display successful joining message
                                        Toast.makeText(JoinSessionActivity.this,
                                                "Requested session joined successfully",
                                                Toast.LENGTH_LONG).show();
                                    }
                                    else{          //If course is not active, display error message
                                        Toast.makeText(JoinSessionActivity.this, "Session not "+
                                                "active for this course", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) { }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onClick(View view) {
        if(courseName.equals("")){   //If no course selected
            Toast.makeText(JoinSessionActivity.this, "No course selected", Toast.LENGTH_LONG).show();
        }

        else{
            //Updating Database values
            joinSession();

            //Redirect Student to his main activity
            startActivity(new Intent(JoinSessionActivity.this, MainActivityStudent.class));
            finish();
        }
    }
}
