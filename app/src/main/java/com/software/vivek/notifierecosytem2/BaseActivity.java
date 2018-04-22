// <header>
// Module: BaseActivity
// Date of creation: 14-04-17
// Author : Rohit Pant
// Modification history:
// 	14-04-17: Created module with initialization functions
// 	20-04-17: Documented code.
// Synopsis:
// 	    This is the base activity. The other main activities extend this activity. This basically
//      provides the UI element of a progress dialog during Sign Up and Sign In.

// Global variables:
//      private ProgressDialog mProgressDialog;

// Functions:
// 	    void showProgressDialog()
// 	    void hideProgressDialog()
// 	    String getUid()
// </header>

// Package declaration of our application
package com.software.vivek.notifierecosytem2;

//Files that need to be imported for smooth functioning of the module
import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class BaseActivity extends AppCompatActivity {
    //Shows progress status
    private ProgressDialog mProgressDialog;

    //Function to display progress dialog on screen
    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);   //Creating new progress dialog if none already exists
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Loading...");            //Displays "Loading....." on screen
        }

        mProgressDialog.show();                //Enables progress dialog
    }

    //Function to hide progress dialog
    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();         //Dismissing the dialog
        }
    }

    //Function to get current user's UID
    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }


}
