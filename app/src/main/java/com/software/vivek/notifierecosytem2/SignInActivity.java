// <header>
// Module: SignInActivity
// Date of creation: 14-04-17
// Author : Rohit Pant

// Modification history:
// 	14-04-17: Created module with initialization functions
// 	20-04-17: Documented code

// Synopsis:
//      This activity serves the use case of both professors and students, that of signing into
//      the application. The user(Professor/Student) will enter his credentials i.e Email ID
//      and Password. These credentials will be verified using Firebase Authentication. If the user
//      is an existing user of the application, he will be redirected to his MainActivity. Else,
//      an error message will be displayed. This activity can further be used by users (Professors/
//      Students) to be redirected to their respective Sign Up activities.

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
//      void writeNewStudentToken()
//      void writeNewProfessorToken()
//      Boolean validateForm()
//      String usernameFromEmail(String email)
// 	    void onOptionsItemSelected(MenuItem item)
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

public class SignInActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "SignInActivity";

    //Declaring objects to interface with Firebase Database
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuthentication;
    private DatabaseReference mDatabaseReference;
    //Variables to store user responses
    private EditText mEmailField;
    private EditText mPasswordField;
    //Buttons
    private Button mSignInButton;
    private Button mSignUpButtonProf;
    private Button mSignUpButtonStud;

    //Function which runs at start of activity and is used to initialize it
    //This function is used repeatedly, so commented just once
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initializing Firebase Database objects
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuthentication = FirebaseAuth.getInstance();

        //If user is currently logged in
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            //Redirect user to student or professor Main Activity based on their designation
            if(mDatabase.child("users").child("professors").child(mAuthentication.getCurrentUser().getUid()) != null)
                startActivity(new Intent(SignInActivity.this, MainActivityProfessor.class));
            else if(mDatabase.child("users").child("students").child(mAuthentication.getCurrentUser().getUid()) != null) {
                startActivity(new Intent(SignInActivity.this, MainActivityStudent.class));
            }
        }

        //Setting layout for Activity
        setContentView(R.layout.login_layout);

        //Storing user responses in variables and linking Java objects with user interface elements
        mEmailField = findViewById(R.id.field_email);
        mPasswordField = findViewById(R.id.field_password);
        mSignInButton = findViewById(R.id.button_sign_in);
        mSignUpButtonProf = findViewById(R.id.button_sign_up_prof);
        mSignUpButtonStud = findViewById(R.id.button_sign_up_stud);

        //Setting button on click listeners
        mSignInButton.setOnClickListener(this);
        mSignUpButtonProf.setOnClickListener(this);
        mSignUpButtonStud.setOnClickListener(this);
    }

    //If Sign Up for Professor button is clicked redirect user to ProfessorSignUpActivity
    public void onSignUpProfClicked(View view){
        startActivity(new Intent(SignInActivity.this, ProfessorSignUpActivity.class));
    }

    //If Sign Up for Student  button is clicked redirect user to StudentSignUpActivity
    public void onSignUpStudClicked(View view){
        startActivity(new Intent(SignInActivity.this, StudentSignUpActivity.class));
    }

    //Function to sign in an existing user
    private void signIn() {
        Log.d(TAG, "signIn");
        if (!validateForm()) {             //If form responses are invalid
            return;
        }
        showProgressDialog();              //Displaying progress dialog

        //Storing user responses in variables
        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();

        //Signing in user using Firebase Authentication
        mAuthentication.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signIn:onComplete:" + task.isSuccessful());
                        hideProgressDialog();

                        if (task.isSuccessful()) {                       //If sign in is successful, log in user
                            onAuthSuccess(task.getResult().getUser());
                        } else {                                         //Else display error message
                            Toast.makeText(SignInActivity.this, "Sign In Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //Function which runs when sign in task is successful
    private void onAuthSuccess(FirebaseUser user) {
        //Adding a value event listener and taking a snapshot of  database values
        mDatabase.child("users").child("professors").child(mAuthentication.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){      //If user is a professor, write his token and redirect him to his main activity
                    writeNewProfessorToken();
                    startActivity(new Intent(SignInActivity.this, MainActivityProfessor.class));
                }
                else{                           //If user is a student, write his token and redirect him to his main activity
                    writeNewStudentToken();
                    startActivity(new Intent(SignInActivity.this, MainActivityStudent.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        finish();
    }

    //Function to write token for student's device into the database
    private void writeNewStudentToken() {

        final String token = FirebaseInstanceId.getInstance().getToken();
        mDatabaseReference = mDatabase.child("token");

        //Adding a value event listener to a database reference to get snapshots of database values
        DatabaseReference currentStudentRollNo = mDatabase.child("users").child("students")
                .child(mAuthentication.getCurrentUser().getUid()).child("rollNumber");
        currentStudentRollNo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Updating database values
                mDatabaseReference.child(dataSnapshot.getValue().toString()).child("tokenID").setValue(token);
                mDatabaseReference.child(dataSnapshot.getValue().toString()).child("userID").setValue(mAuthentication.getCurrentUser().getUid());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //Function to write token for professor's device into the database
    private void writeNewProfessorToken() {

        final String token = FirebaseInstanceId.getInstance().getToken();
        //Updating database values
        mDatabaseReference = mDatabase.child("token");
        mDatabaseReference.child(mAuthentication.getCurrentUser().getUid()).child("tokenID").setValue(token);
    }

    //Function to validate form responses
    private boolean validateForm() {
        //Boolean variable denoting whether the form is valid or not
        boolean isValid = true;
        if(TextUtils.isEmpty(mEmailField.getText().toString())) {
            mEmailField.setError("Required");       //If email field is empty, invalidating form
            isValid = false;
        }
        else{
            mEmailField.setError(null);             //Else no error
        }

        if(TextUtils.isEmpty(mPasswordField.getText().toString())) {
            mPasswordField.setError("Required");     //If password field is empty, invalidating form
            isValid = false;
        }
        else{
            mPasswordField.setError(null);           //Else no error
        }
        if(!isValid){   //If form contents are invalid, display error message
            Toast.makeText(SignInActivity.this, "Please enter all required details",
                    Toast.LENGTH_SHORT).show();
        }
        return isValid;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.button_sign_in) {             //If sign in button is clicked call sign in function
            signIn();
        }
        else if (i == R.id.button_sign_up_prof) {   //If sign up for professor button is clicked, calling appropriate sign up function
            onSignUpProfClicked(v);
        }
        else if (i == R.id.button_sign_up_stud) {   //If sign up for student button is clicked, calling appropriate sign up function
            onSignUpStudClicked(v);
        }
    }
}
