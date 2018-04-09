package com.software.vivek.notifierecosytem2.fragment;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class MyNotificationsFragment extends NotificationListFragment {

    public MyNotificationsFragment() {}

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // All my notifications
        return databaseReference.child("notifications")
                .child(getUid());
    }
}
