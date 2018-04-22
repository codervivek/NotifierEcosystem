// <header>
// Module: SignInActivity
// Date of creation: 14-04-17
// Author : Rohit Pant

// Modification history:
// 	14-04-17: Created module with initialization functions
// 	20-04-17: Documented code.

// Synopsis:
//      This activity serves the use case of signing up a user for the application (for professors).
//      The user (Professor) will enter his credentials i.e Email ID, Password and Name. If the user
//      is an existing user of the application, he will be redirected to his MainActivity. Else,
//      these credentials will be logged into the Firebase Authentication Database.

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
//      void writeNewProf()
//      void writeNewProfessorToken()
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.software.vivek.notifierecosytem2.models.Professor;

public class ProfessorSignUpActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "ProfessorSignUpActivity";

    //Declaring objects to interface with Firebase Database
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuthentication;
    private DatabaseReference mDatabaseReference;
    //Variables to store user responses
    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mNameField;
    //Strings to store professor name attribute
    private String name;
    //Sign up button
    private Button mSignUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            //Redirect user to student or professor Main Activity based on their designation
            if(mDatabase.child("users").child("professors").child(mAuthentication.getCurrentUser().getUid()) != null)
                startActivity(new Intent(ProfessorSignUpActivity.this, MainActivityProfessor.class));
            else if(mDatabase.child("users").child("students").child(mAuthentication.getCurrentUser().getUid()) != null) {
                startActivity(new Intent(ProfessorSignUpActivity.this, MainActivityStudent.class));
            }
        }

        //Setting layout for activity
        setContentView(R.layout.signup_layout_prof);

        //Initializing Firebase Database objects
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuthentication = FirebaseAuth.getInstance();

        //Combining Java objects with User Interface elements
        mEmailField = findViewById(R.id.field_email_prof);
        mPasswordField = findViewById(R.id.field_password_prof);
        mNameField = findViewById(R.id.field_name_prof);
        mSignUpButton = findViewById(R.id.button_sign_up_prof_fin);

        //Setting on click listener on Sign Up Button
        mSignUpButton.setOnClickListener(this);
    }

    //Function to sign up a new user
    private void signUp() {
        Log.d(TAG, "signUp");
        if (!validateForm()) {
            return;
        }

        showProgressDialog();        //Displaying progress dialog

        //Storing user responses in variables
        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();
        name = mNameField.getText().toString();

        //Signing up new users using Firebase Authentication
        mAuthentication.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUser:onComplete:" + task.isSuccessful());
                        hideProgressDialog();

                        if (task.isSuccessful()) {                            //If sign up is successful, creating a new user
                            onAuthSuccess(task.getResult().getUser());
                        } else {                                              //Else, displaying error message
                            Toast.makeText(ProfessorSignUpActivity.this, "Sign Up Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //Function to create new professor if sign up is successful
    private void onAuthSuccess(FirebaseUser user) {
        String username = usernameFromEmail(user.getEmail());

        // Write new user
        writeNewProf(user.getUid(), name, username, user.getEmail());
        writeNewProfessorToken();

        //Redirecting professor to his Main Activity
        startActivity(new Intent(ProfessorSignUpActivity.this, MainActivityProfessor.class));
        finish();
    }

    //Funtion to write a new user in database
    private void writeNewProf(String userId, String name, String username, String email) {
        //New Professor object
        Professor professor = new Professor(name, username, email, "None");
        //Updating database values
        mDatabase.child("users").child("professors").child(userId).setValue(professor);
    }

    //Creating new token for professor device
    private void writeNewProfessorToken() {
        //Getting token and updating database values
        final String token = FirebaseInstanceId.getInstance().getToken();
        mDatabaseReference = mDatabase.child("token");
        mDatabaseReference.child(mAuthentication.getCurrentUser().getUid()).child("tokenID").setValue(token);
    }

    //Extracting username from email using regular expressions
    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];    //Split string at first occurrence of '@' and taking the initial part
        } else {
            return email;
        }
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
            Toast.makeText(ProfessorSignUpActivity.this, "Please enter all required details",
                    Toast.LENGTH_SHORT).show();
        }
        String password = mPasswordField.getText().toString();
        if(password.length() < 6){
            Toast.makeText(ProfessorSignUpActivity.this, "Password length should be at least 6",
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
