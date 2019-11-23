package com.kutylo.notes.notes;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;

import androidx.core.app.NotificationCompat;

import com.kutylo.notes.R;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationNote {
    private String textNote;
    private Activity activity;

    public NotificationNote(String textNote, Activity activity) {
        this.textNote = textNote;
        this.activity=activity;
        createNotification();

    }

    private void createNotification(){
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(activity)
                        .setSmallIcon(R.drawable.ic_action_notifi)
                        .setContentText(textNote);

        Notification notification = builder.build();

        NotificationManager notificationManager =
                (NotificationManager) activity.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }
}
