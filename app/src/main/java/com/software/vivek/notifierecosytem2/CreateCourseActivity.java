// <header>
// Module: CreateCourseActivity
// Date of creation: 14-04-17
// Author : Rohit Pant
// Modification history:
// 	14-04-17: Created module with initialization functions
// 	20-04-17: Documented code.
// Synopsis:
// 	    This activity serves the professor's use case of creating a new course.
//      The professor will enter the course details, namely the course name and course
//      code into a create course form. The contents of the form will be validated and
//      a new course will be written into the database.

// Global variables:
//      private static final String TAG = "CreateCourseActivity";
//      private DatabaseReference mDatabase;
//      private FirebaseAuth mAuthentication;
//      private EditText mCourseNameField;
//      private EditText mCourseCodeField;
//      private Button mCreateCourseButton;
//      private String professorID;
// Functions:
// 	    void onCreate(Bundle savedInstanceState)
// 	    void createCourse()
// 	    void showViewButton()
// 	    void writeNewCourse(String professorID, String courseName, String courseCode)
// 	    String getString(String uid)
//  	Boolean validateForm()
//  	void onClick(View view)
// </header>

// Package declaration of our application
package com.software.vivek.notifierecosytem2;

//Files that need to be imported for smooth functioning of the module
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.software.vivek.notifierecosytem2.models.Course;

import java.util.ArrayList;

public class CreateCourseActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "CreateCourseActivity";
    //Declarations of objects to interface with Firebase Database
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuthentication;
    //Variables to store responses of users to User Interface elements
    private EditText mCourseNameField;
    private EditText mCourseCodeField;
    //Create Course Button
    private Button mCreateCourseButton;
    private String professorID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Setting layout for activity
        setContentView(R.layout.activity_create_course);
        //Initializing Firebase Database objects
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuthentication = FirebaseAuth.getInstance();

        // Views
        mCourseNameField = findViewById(R.id.field_course_name);
        mCourseCodeField = findViewById(R.id.field_course_code);
        mCreateCourseButton = findViewById(R.id.button_create_course_fin);
        //Setting on click listener for create course button
        mCreateCourseButton.setOnClickListener(this);
    }

    //Function to create course
    private void createCourse() {
        Log.d(TAG, "createCourse");
        //If form contents are invalid
        if (!validateForm()) {
            return;
        }
        //Storing user responses in variables
        String courseName = mCourseNameField.getText().toString();
        String courseCode = mCourseCodeField.getText().toString();
        //Getting current professor's UID from database
        professorID = getString(mDatabase.child("users").child("professors")
                .child(mAuthentication.getCurrentUser().getUid()).toString());

        //Writing a new course into the database
        writeNewCourse(professorID, courseName, courseCode);
    }

    //Function to create a new course in accordance to the details entered by the user
    private void writeNewCourse(String professorID, String courseName, String courseCode) {
        //Creating new course object
        Course course = new Course(professorID, courseName, courseCode);

        //Updating database values
        mDatabase.child("courses").push().setValue(course);
        mDatabase.child("enrolled").child(courseName).child("profID").setValue(professorID);
        mDatabase.child("professors").child("profcourses").child(courseName).setValue(courseCode);
        mDatabase.child("enrolled").child(courseName).child("session").setValue("inactive");
        mDatabase.child("profCourses").child(courseName).child("profID")
                .setValue(mAuthentication.getCurrentUser().getUid());

        //Redirecting professor from current activity to his Main Activity
        startActivity(new Intent(CreateCourseActivity.this, MainActivityProfessor.class));
        finish();
    }

    //Extracts UID of a professor from a composite string using regular expressions
    private String getString(String uid){
        if (uid.contains("professors/")) {
            return uid.split("professors/")[1];
        } else {
            return uid;
        }
    }

    //Function to validate form contents
    private boolean validateForm() {
        //Boolean variable which denotes whether the form contents are valid or not
        boolean isValid = true;
        if (TextUtils.isEmpty(mCourseNameField.getText().toString())) {
            //If course name field is empty, invalidate form
            mCourseNameField.setError("Required");
            isValid = false;
        }
        else{
            //Else, no error
            mCourseNameField.setError(null);
        }

        if(TextUtils.isEmpty(mCourseCodeField.getText().toString())) {
            //If course code field is empty, invalidate form
            mCourseCodeField.setError("Required");
            isValid = false;
        }
        else{
            //Else, no error
            mCourseCodeField.setError(null);
        }
        return isValid;
    }

    //Function which calls createCourse function when create course button is clicked
    @Override
    public void onClick(View view) {
        //Display message if course creation task is successful
        if(validateForm()){    //If form contents are valid
            Toast.makeText(CreateCourseActivity.this, "Course successfully created!",
                    Toast.LENGTH_LONG).show();
            //Calling create course function on Create Course button click
            createCourse();
        }
        else{   //If form content is invalid
            Toast.makeText(CreateCourseActivity.this, "Please enter all required details",
                    Toast.LENGTH_LONG).show();
        }
    }
}

