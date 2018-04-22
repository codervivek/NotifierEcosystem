// <header>
// Module: CreateCourseActivity
// Date of creation: 14-04-17
// Author : Rohit Pant
// Modification history:
// 	14-04-17: Created module with initialization functions
// 	20-04-17: Documented code.
// Synopsis:
// 	    This activity serves the professor's use case of creating a session in a course.
//      A single choice selectable list of courses offered by the professor will be
//      displayed on the enter screen. The professor may select one of these courses and
//      click the Create Session Button thus successfully creating a live session in the
//      selected course. Now the students enrolled for this course can join this session.

// Global variables:
//        private ArrayList<String> courseList= new ArrayList<>();
//        private ArrayList<String> selectedItems= new ArrayList<>();
//        private FirebaseAuth mAuthentication;
//        private DatabaseReference mDatabase;
//        private DatabaseReference mDatabaseReference;
//        private ArrayAdapter arrayAdapter;
//        private Button mCreateSessionButton;
// Functions:
// 	    void onCreate(Bundle savedInstanceState)
// 	    void onItemClick()
// 	    void onChildAdded()
//      void createSession()
// 	    void onClick(View view)
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
import com.software.vivek.notifierecosytem2.models.Course;

import java.util.ArrayList;

public class CreateSessionActivity extends AppCompatActivity implements View.OnClickListener {
    //Array List declarations to present Professor with course options to activate session in and to store selected course names
    private ArrayList<String> courseList= new ArrayList<>();
    //Declaring objects to interface with Firebase Database
    private FirebaseAuth mAuthentication;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseReference;
    //Declaring an Array Adapter to interact with the List View
    private ArrayAdapter arrayAdapter;
    //Create Session Button
    private Button mCreateSessionButton;
    private String course = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initializing Firebase Database objects
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("courses");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuthentication = FirebaseAuth.getInstance();

        //Linking User Interface elements with Java objects
        setContentView(R.layout.activity_create_session);
        mCreateSessionButton = findViewById(R.id.button_create_session_fin);
        ListView courseListView = findViewById(R.id.prof_course_list);
        //Enabling single choice selection in List View
        courseListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        //Initializing Array Adapter object with various layout elements and course list
        arrayAdapter = new ArrayAdapter<>(this, R.layout.row_layout, R.id.txt_lan, courseList);

        //Setting on click listener on Create Session Button
        mCreateSessionButton.setOnClickListener(this);

        //Linking Course List View with Array Adapter to output list of courses
        courseListView.setAdapter(arrayAdapter);

        //Setting an item click listener on the list to save selections of users
        courseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long length) {
                //Stores value of item just clicked
                String selectedItem = ((TextView) view).getText().toString();
                course = selectedItem;
            }
        });
        //Adding a child event listener to courses in database so as to update course list of professor whenever he creates a new course
        mDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot courseSnapshot, String s) {
                Course course = courseSnapshot.getValue(Course.class);               //Data snapshot of database of Course data type
                if(!courseList.contains(course.courseName)){                       //If item is not already in list
                    if(course.profUid.equals(mAuthentication.getCurrentUser().getUid())){    //Checking if course is offered by currently logged in user
                        courseList.add(course.courseName);                         //Adding course to course list
                    }
                }
                arrayAdapter.notifyDataSetChanged();
            }
            @Override
            public void onChildChanged(DataSnapshot courseSnapshot, String s) {

            }
            @Override
            public void onChildRemoved(DataSnapshot courseSnapshot) {

            }
            @Override
            public void onChildMoved(DataSnapshot courseSnapshot, String s) {

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void createSession(){
        //Updating database values
        mDatabase.child("enrolled").child(course).child("session").setValue("active");
        mDatabase.child("users").child("professors").child(mAuthentication.getCurrentUser().getUid())
                .child("activeSessions").setValue(course);
    }

    //Updating database entries when create session button is clicked
    @Override
    public void onClick(View view) {
        if(course.equals("")){
            Toast.makeText(CreateSessionActivity.this, "No course selected", Toast.LENGTH_LONG).show();
        }
        //Calling the create session function on button click
        else{
            createSession();
            //Redirecting professor from CreateSessionActivity to the CurrentSessionActivity
            startActivity(new Intent(CreateSessionActivity.this, CurrentSessionActivity.class));
            finish();
        }
    }
}