package com.software.vivek.notifierecosytem2.viewholder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.software.vivek.notifierecosytem2.R;
import com.software.vivek.notifierecosytem2.models.Notification;


public class NotificationHolder extends RecyclerView.ViewHolder {

    public TextView messageView;
    public TextView studentView;
    public TextView timeView;

    public NotificationHolder(View itemView) {
        super(itemView);

        messageView = itemView.findViewById(R.id.notification_message);
        studentView = itemView.findViewById(R.id.notification_student);
        timeView = itemView.findViewById(R.id.notification_time);
    }

    public void bindToNotification(Notification notification) {
        messageView.setText(notification.message);
        studentView.setText(notification.student);
        timeView.setText(String.valueOf(notification.time));

    }
}
//
