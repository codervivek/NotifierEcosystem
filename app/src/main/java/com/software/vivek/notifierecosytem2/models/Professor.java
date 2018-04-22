package com.software.vivek.notifierecosytem2.models;

        import com.google.firebase.database.IgnoreExtraProperties;
        import java.util.ArrayList;
// [START blog_user_class]
@IgnoreExtraProperties
public class Professor {

    public String username;
    public String name;
    public String email;
    public String activeSessions;

    public Professor() {
        // Default constructor required for calls to DataSnapshot.getValue(Student.class)
    }

    //Initializing Professor object
    public Professor(String name, String username, String email, String activeSessions) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.activeSessions = activeSessions;
    }

}
