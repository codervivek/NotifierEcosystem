package com.software.vivek.notifierecosytem2.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Record {
    public String message;
    public String responded;

    public Record() {
        // Default constructor required for calls to DataSnapshot.getValue(Student.class)
    }

    public Record(String username, String status) {
        this.message = username;
        this.responded = status;
    }
}