// <header>
// Module: MessagingInstance
// Date of creation: 14-04-17
// Author : Vivek Raj
// Modification history:
// 	14-04-17: Created module with initialization functions
// 	20-04-17: Documented code.
// Synopsis:
// 	    This class extends Message Module of Firebase. It is called whenever a
//      new device is registered. On calling this module, a new token is assigned
//      to the device.

// Global variables:
//      private static final String TAG = "MessagingInstance";

// Functions:
// 	    void onTokenRefresh()
// </header>

// Package declaration of our application
package com.software.vivek.notifierecosytem2;

//Files that need to be imported for smooth functioning of the module
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import static android.content.ContentValues.TAG;

public class MessagingInstance extends FirebaseInstanceIdService {

    private static final String TAG = "MessagingInstance";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
    }
}