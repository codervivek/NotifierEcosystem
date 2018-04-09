package com.software.vivek.notifierecosytem2.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

// [START post_class]
@IgnoreExtraProperties
public class Notification {

    public String uid;
    public String student;
    public String message;
    public String time;
//    public String body;
//    public int starCount = 0;
//    public Map<String, Boolean> stars = new HashMap<>();

    public Notification() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Notification(String uid, String student, String message, String time) {
        this.uid = uid;
        this.student = student;
        this.message = message;
        this.time = time;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("student", student);
        result.put("message", message);
        result.put("time", time);
//        result.put("starCount", starCount);
//        result.put("stars", stars);

        return result;
    }

}
// [END post_class]
