// <header>
// Module: MessagingService
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
//      private static final String TAG = "MessagingService";

// Functions:
// 	    void onMessageReceived()
//      void onDataChange()
// </header>

// Package declaration of our application
package com.software.vivek.notifierecosytem2;

//Files that need to be imported for smooth functioning of the module
import android.content.Intent;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MessagingService extends FirebaseMessagingService{

    private static final String TAG = "MessagingService";

    //Declaring object to interface with Firebase Database
    private FirebaseAuth mAuthentication;

    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {

        // initialize firebase auth
        mAuthentication = FirebaseAuth.getInstance();

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {

            // log message payload
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            // initialize database reference
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

            // intent to go to respond notification acrivity
            final Intent respondNotificationintent = new Intent(this, RespondNotification.class);
            DatabaseReference professorID = mDatabase.child("users").child("professors")
                    .child(mAuthentication.getCurrentUser().getUid());
            professorID.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.exists()){

                        // set extra parameters to be passes for intent
                        respondNotificationintent.putExtra("profID",remoteMessage.getData().get("profID"));
                        respondNotificationintent.putExtra("uid",remoteMessage.getData().get("uid"));
                        respondNotificationintent.putExtra("currentSession",remoteMessage.getData()
                                .get("currentSession"));

                        // go to respond notification activity
                        startActivity(respondNotificationintent);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }
}
