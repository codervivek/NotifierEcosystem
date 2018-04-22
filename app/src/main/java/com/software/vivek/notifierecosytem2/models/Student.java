package com.software.vivek.notifierecosytem2.models;

import com.google.firebase.database.IgnoreExtraProperties;
import java.util.ArrayList;
// [START blog_user_class]
@IgnoreExtraProperties
public class Student {

    public String username;
    public String name;
    public String email;
    public String rollNumber;
    public int state;
    public String currentSession;

    public Student() {
        // Default constructor required for calls to DataSnapshot.getValue(Student.class)
    }

    //Initializing student object
    public Student(String name, String username, String email, String rollNumber, int state, String currentSession) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.rollNumber = rollNumber;
        this.state = state;
        this.currentSession = currentSession;
    }
}
// [END blog_user_class]
