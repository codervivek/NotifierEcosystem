package com.software.vivek.notifierecosytem2.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class MyPastRecords extends NotificationListFragment {

    public MyPastRecords() {}

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // All my notifications
        return databaseReference.child("notifications")
                .child(getUid());
    }
}
