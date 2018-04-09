package com.software.vivek.notifierecosytem2.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.software.vivek.notifierecosytem2.R;
import com.software.vivek.notifierecosytem2.models.Notification;
import com.software.vivek.notifierecosytem2.viewholder.NotificationHolder;

public abstract class NotificationListFragment extends Fragment {

    private static final String TAG = "NotiListFrag";

    private FirebaseRecyclerAdapter<Notification, NotificationHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;
    private DatabaseReference mDatabase;

    public NotificationListFragment() {}

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_all_notifications, container, false);

        mRecycler = rootView.findViewById(R.id.messages_list);
        mRecycler.setHasFixedSize(true);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set up Layout Manager, reverse layout
        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        // Set up FirebaseRecyclerAdapter with the Query
        Query NotificationsQuery = getQuery(mDatabase);

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Notification>()
                .setQuery(NotificationsQuery, Notification.class)
                .build();

        mAdapter = new FirebaseRecyclerAdapter<Notification, NotificationHolder>(options) {

            @Override
            public NotificationHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new NotificationHolder(inflater.inflate(R.layout.item_notification, viewGroup, false));
            }

            @Override
            protected void onBindViewHolder(NotificationHolder viewHolder, int position, final Notification model) {
//                final DatabaseReference NotificationRef = getRef(position);

                // Set click listener for the whole Notification view
//                final String NotificationKey = NotificationRef.getKey();
//                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        // Launch NotificationDetailActivity
////                        Intent intent = new Intent(getActivity(), NotificationDetailActivity.class);
////                        intent.putExtra(NotificationDetailActivity.EXTRA_Notification_KEY, NotificationKey);
////                        startActivity(intent);
//                    }
//                });

                // Determine if the current user has liked this Notification and set UI accordingly
//                if (model.stars.containsKey(getUid())) {
//                    viewHolder.starView.setImageResource(R.drawable.ic_toggle_star_24);
//                } else {
//                    viewHolder.starView.setImageResource(R.drawable.ic_toggle_star_outline_24);
//                }

                // Bind Notification to ViewHolder, setting OnClickListener for the star button
                viewHolder.bindToNotification(model);
            }
        };
        mRecycler.setAdapter(mAdapter);
    }

    // [START Notification_stars_transaction]
//    private void onStarClicked(DatabaseReference NotificationRef) {
//        NotificationRef.runTransaction(new Transaction.Handler() {
//            @Override
//            public Transaction.Result doTransaction(MutableData mutableData) {
//                Notification p = mutableData.getValue(Notification.class);
//                if (p == null) {
//                    return Transaction.success(mutableData);
//                }
//
//                if (p.stars.containsKey(getUid())) {
//                    // Unstar the Notification and remove self from stars
//                    p.starCount = p.starCount - 1;
//                    p.stars.remove(getUid());
//                } else {
//                    // Star the Notification and add self to stars
//                    p.starCount = p.starCount + 1;
//                    p.stars.put(getUid(), true);
//                }
//
//                // Set value and report transaction success
//                mutableData.setValue(p);
//                return Transaction.success(mutableData);
//            }
//
//            @Override
//            public void onComplete(DatabaseError databaseError, boolean b,
//                                   DataSnapshot dataSnapshot) {
//                // Transaction completed
//                Log.d(TAG, "NotificationTransaction:onComplete:" + databaseError);
//            }
//        });
//    }
//    // [END Notification_stars_transaction]


    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }


    public String getUid() {
        Log.e(TAG,FirebaseAuth.getInstance().getCurrentUser().getUid());
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public abstract Query getQuery(DatabaseReference databaseReference);

}
