package com.software.vivek.notifierecosytem2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.software.vivek.notifierecosytem2.models.Course;

public class  PastRecordsActivityProf extends AppCompatActivity {

    private FirebaseAuth mAuthentication;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseReference;
    private ArrayAdapter arrayAdapter;

    private ArrayAdapter<String> recordsList;
    private String record;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_past_records_prof);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuthentication = FirebaseAuth.getInstance();
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

//                    if(!recordsList.contains(course.courseName)){
//                        if(course.profUid.equals(mAuthentication.getCurrentUser().getUid())){
//                            courseList.add(course.courseName);
//                        }
//                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
//        setContentView(R.layout.activity_enrol_course);
//        mEnrolCourseButton = findViewById(R.id.button_enrol_course_fin);
//        mEnrolCourseButton.setOnClickListener(this);
        ListView courseListView = findViewById(R.id.records_list_prof);
//        courseListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
//        arrayAdapter = new ArrayAdapter<String>(this, R.layout.row_layout, R.id.txt_lan, recordsList);
//        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, courseList);
        courseListView.setAdapter(arrayAdapter);

        mDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Course course = dataSnapshot.getValue(Course.class);
                Log.e("HElloWorld",course.toString());
                recordsList.add(course.courseName);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
