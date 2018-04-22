// <header>
// Module: EnrolCourseActivity
// Date of creation: 14-04-17
// Author : Rohit Pant

// Modification history:
// 	14-04-17: Created module with initialization functions
// 	20-04-17: Documented code.

// Synopsis:
//      This activity serves the student's use case of enrolling himself into a course.
//      In this activity the student will be displayed a list of all courses that he can
//      enrol for. The student can select all courses that he wants to enrol for by clicking
//      on the multiple choice selectable list and enrol for then by clicking the enrol course
//      button.

// Global variables:
//      private ArrayList<String> courseList= new ArrayList<>();
//      private ArrayList<String> selectedItems = new ArrayList<>();
//      private FirebaseAuth mAuthentication;
//      private DatabaseReference mDatabase;
//      private DatabaseReference mDatabaseReference;
//      private ArrayAdapter arrayAdapter;
//      private Button mEnrolCourseButton;

// Functions:
// 	    void onCreate(Bundle savedInstanceState)
//      void onItemClick()
// 	    void onDataChange()
// 	    void onChildAdded()
//      void showSelectedItems()
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
import com.google.firebase.database.ValueEventListener;
import com.software.vivek.notifierecosytem2.models.Course;

import java.util.ArrayList;

public class EnrolCourseActivity extends AppCompatActivity implements View.OnClickListener {

    //Declaring an Array List to store course list of courses for which student can enrol and for storing items selected by student
    private ArrayList<String> courseList = new ArrayList<>();
    private ArrayList<String> selectedItems = new ArrayList<>();
    //Declaring objects to interface with Firebase Database
    private FirebaseAuth mAuthentication;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseReference;
    //Declaring array adapter object to combine with list view
    private ArrayAdapter arrayAdapter;
    //Enrol Course Button
    private Button mEnrolCourseButton;
    private String enrolledCourse = "";
    private boolean enrolled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initializing Firebase Database objects
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("courses");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuthentication = FirebaseAuth.getInstance();

        //Combining User interface elements with Java objects
        setContentView(R.layout.activity_enrol_course);
        mEnrolCourseButton = findViewById(R.id.button_enrol_course_fin);
        ListView courseListView = findViewById(R.id.course_list);
        courseListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);  //Enabling multiple choice selections on list

        //Initializing array adapter object with layouts and course list
        arrayAdapter = new ArrayAdapter<>(this, R.layout.row_layout, R.id.txt_lan, courseList);

        //Setting on click listener on enrol course button
        mEnrolCourseButton.setOnClickListener(this);

        //Combing array adapter with list view
        courseListView.setAdapter(arrayAdapter);
        //Setting an item click listener on list
        courseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long length) {
                String selectedItem = ((TextView) view).getText().toString();  //Recording value of just clicked element
                //If value already selected, removing it from selected items list, else adding it to the list
                enrolledCourse = selectedItem;
            }
        });

        //Adding child event listener update course list of courses that student can enrol for whenever a professor creates a course
        mDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot courseSnapshot, String s) {
                Course course = courseSnapshot.getValue(Course.class);
                courseList.add(course.courseName);
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

    //Function to display list of courses student has enrolled for and to update database values accordingly
    public void showSelectedItems(View view) {
        //Updating database values for each course student has enrolled for
        final String course = enrolledCourse;
        mDatabase.child("enrolled").child(course).child("students").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot courseSnapshot) {
                for(DataSnapshot snapshot: courseSnapshot.getChildren()){
                    String courseName = snapshot.getValue(String.class);
                    if(courseName.equals(mAuthentication.getCurrentUser().getUid())){
                        Toast.makeText(EnrolCourseActivity.this, "You have already " +
                                "enrolled for this course", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(EnrolCourseActivity.this, MainActivityStudent.class));
                        enrolled = true;
                        finish();
                    }
                }
                if(enrolled == false){
                //Displays courses student has enrolled for
                Toast.makeText(EnrolCourseActivity.this, "You have been enrolled for\n"
                        + course, Toast.LENGTH_LONG).show();
                    mDatabase.child("enrolled").child(course).child("students")
                            .child(mAuthentication.getCurrentUser().getUid())
                            .setValue(mAuthentication.getCurrentUser().getUid());
                    mDatabase.child("students").child(mAuthentication.getCurrentUser().getUid())
                            .child("courses").child(course).setValue(course);
                                   }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Redirecting student to his Main Activity
        startActivity(new Intent(EnrolCourseActivity.this, MainActivityStudent.class));
    }

    @Override
    public void onClick(View view) {
        if(enrolledCourse.equals("")){   //If no course selected
            Toast.makeText(EnrolCourseActivity.this, "No course selected", Toast.LENGTH_LONG).show();
        }
        //Calling showSelectedItems function on clicking of enrol course button
        else{
            showSelectedItems(view);
        }
    }
}
