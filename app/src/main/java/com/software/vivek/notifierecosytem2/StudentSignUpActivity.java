// <header>
// Module: SignInActivity
// Date of creation: 14-04-17
// Author : Rohit Pant

// Modification history:
// 	14-04-17: Created module with initialization functions
// 	20-04-17: Documented code.

// Synopsis:
//      This activity serves the use case of signing up a user for the application (for students).
//      The user (Student) will enter his credentials i.e Email ID, Password, Name and Roll Number.
//      If the user is an existing user of the application, he will be redirected to his
//      MainActivity. Else, these credentials will be logged into the Firebase Authentication
//      Database.

// Global variables:
//      private static final String TAG = "SignInActivity";
//      private DatabaseReference mDatabase;
//      private FirebaseAuth mAuthentication;
//      private DatabaseReference mDatabaseReference;
//      private EditText mEmailField;
//      private EditText mPasswordField;
//      private Button mSignInButton;
//      private Button mSignUpButtonProf;
//      private Button mSignUpButtonStud;

// Functions:
// 	    void onCreate(Bundle savedInstanceState)
//      void onSignUpProfClicked(View view)
//      void onSignUpStudClicked(View view)
//      void onStart()
//      void signIn()
//      void onAuthSuccess(FirebaseUser user)
//      void writeNewStudent()
//      void writeNewStudentToken()
//      Boolean validateForm()
//      String usernameFromEmail(String email)
// 	    void onClick(View view)
// </header>

// Package declaration of our application
package com.software.vivek.notifierecosytem2;

//Files that need to be imported for smooth functioning of the module
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.software.vivek.notifierecosytem2.models.Student;

public class StudentSignUpActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "StudentSignUpActivity";

    //Declaring objects to interface with Firebase Database
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuthentication;
    private DatabaseReference mDatabaseReference;
    //Variables to store user responses
    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mNameField;
    private EditText mRollNoField;
    //Strings to store student attributes
    private String name;
    private String rollNumber;
    //Sign up button
    private Button mSignUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            //Redirect user to student or professor Main Activity based on their designation
            if(mDatabase.child("users").child("professors").child(mAuthentication.getCurrentUser().getUid()) != null)
                startActivity(new Intent(StudentSignUpActivity.this, MainActivityProfessor.class));
            else if(mDatabase.child("users").child("students").child(mAuthentication.getCurrentUser().getUid()) != null) {
                startActivity(new Intent(StudentSignUpActivity.this, MainActivityStudent.class));
            }
        }

        //Setting layout for activity
        setContentView(R.layout.signup_layout_stud);

        //Initializing Firebase Database objects
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuthentication = FirebaseAuth.getInstance();

        // Views
        mEmailField = findViewById(R.id.field_email_stud);
        mPasswordField = findViewById(R.id.field_password_stud);
        mNameField = findViewById(R.id.field_name_stud);
        mSignUpButton = findViewById(R.id.button_sign_up_stud_fin);
        mRollNoField = findViewById(R.id.field_roll_number);

        //Setting a Click listener on sign up button
        mSignUpButton.setOnClickListener(this);
    }

    //Function to sign up a new student using Firebase Authentication
    private void signUp() {
        if (!validateForm()) {  //If form contents are invalid
            return;
        }

        //Activate progress dialog
        showProgressDialog();

        //Store user input into variables
        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();
        name = mNameField.getText().toString();
        rollNumber = mRollNoField.getText().toString();

        //Sign up a new user with his email and password
        mAuthentication.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUser:onComplete:" + task.isSuccessful());
                        hideProgressDialog();

                        if (task.isSuccessful()) {                     //If sign up task is successful, create new user
                            onAuthSuccess(task.getResult().getUser());
                        } else {                                       //If sign up task is successful display error message
                            Toast.makeText(StudentSignUpActivity.this, "Sign Up Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void onAuthSuccess(FirebaseUser user) {
        //Extract username from email ID
        String username = usernameFromEmail(user.getEmail());

        // Write new user
        writeNewStudent(user.getUid(), name, username, user.getEmail(), rollNumber);

        // Go to MainActivityStudent
        startActivity(new Intent(StudentSignUpActivity.this, MainActivityStudent.class));
        finish();
    }

    //Create a new student
    private void writeNewStudent(String userId, String name, String username, String email, String rollNumber) {
        // New Student object
        Student student = new Student(name, username, email, rollNumber,10,"None");

        //Updating database values
        mDatabase.child("users").child("students").child(userId).setValue(student);

        //Creating token for student's device
        final String token = FirebaseInstanceId.getInstance().getToken();
        mDatabaseReference = mDatabase.child("token");
        DatabaseReference currentStudentRollNo = mDatabase.child("users").child("students").
                child(mAuthentication.getCurrentUser().getUid()).child("rollNumber");

        //Using value event listeners to get data snapshots of database and update database values accordingly
        currentStudentRollNo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mDatabaseReference.child(dataSnapshot.getValue().toString()).child("tokenID").setValue(token);
                mDatabaseReference.child(dataSnapshot.getValue().toString()).child("userID").setValue(mAuthentication.getCurrentUser().getUid());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //Function to extract username from email ID using regular expressions
    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];      //Split string at first occurrence of '@' and taking the initial part
        } else {
            return email;
        }
    }

    //Function to validate form responses
    private boolean validateForm() {
        //Boolean variable denoting whether the form is valid or not
        boolean isValid = true;
        if(TextUtils.isEmpty(mEmailField.getText().toString())) {
            mEmailField.setError("Required");      //If email field is empty, invalidate the form
            isValid = false;
        }
        else{
            mEmailField.setError(null);            //Else no error
        }

        if(TextUtils.isEmpty(mPasswordField.getText().toString())) {
            mPasswordField.setError("Required");   //If password field is empty, invalidate the form
            isValid = false;
        }
        else{
            mPasswordField.setError(null);         //Else no error
        }
        if(!isValid){   //If form contents are invalid, display error message
            Toast.makeText(StudentSignUpActivity.this, "Please enter all required details",
                    Toast.LENGTH_SHORT).show();
        }
        String password = mPasswordField.getText().toString();
        if(password.length() < 6){
            Toast.makeText(StudentSignUpActivity.this, "Password length should be at least 6",
                    Toast.LENGTH_LONG).show();
            isValid = false;
        }
        return isValid;
    }

    @Override
    public void onClick(View v) {
        //Calling signUp function when Sign Up button is clicked
        signUp();
    }
}
