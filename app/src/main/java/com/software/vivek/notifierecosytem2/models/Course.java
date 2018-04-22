package com.software.vivek.notifierecosytem2.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;

// [START blog_user_class]
@IgnoreExtraProperties
public class Course {

    public String profUid;
    public String courseName;
    public String courseCode;

    public Course() {
        // Default constructor required for calls to DataSnapshot.getValue(Student.class)
    }

    //Initializing Course object
    public Course(String profUid, String courseName, String courseCode) {
        this.profUid = profUid;
        this.courseName = courseName;
        this.courseCode = courseCode;
    }
}
