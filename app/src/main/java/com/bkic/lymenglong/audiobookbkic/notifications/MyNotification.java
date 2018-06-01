package com.bkic.lymenglong.audiobookbkic.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.bkic.lymenglong.audiobookbkic.R;
import com.bkic.lymenglong.audiobookbkic.main.MainActivity;

import java.text.DateFormat;
import java.util.Date;

import static android.content.Context.NOTIFICATION_SERVICE;

public class MyNotification {

    private Context context;

    public MyNotification(Context context) {
        this.context = context;
    }

    public void createNotification() {
        // BEGIN_INCLUDE(notificationCompat)
        @SuppressWarnings("deprecation") NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        // END_INCLUDE(notificationCompat)

        // BEGIN_INCLUDE(intent)
        //Create Intent to launch this Activity again if the notification is clicked.
        Intent i = new Intent(context, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(context, 0, i,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(intent);
        // END_INCLUDE(intent)

        // BEGIN_INCLUDE(ticker)
        // Sets the ticker text
//        builder.setTicker(context.getResources().getString(R.string.custom_notification));

        // Sets the small icon for the ticker
        builder.setSmallIcon(R.drawable.ic_audiotrack);
        // END_INCLUDE(ticker)

        // BEGIN_INCLUDE(buildNotification)
        // Cancel the notification when clicked
        builder.setAutoCancel(true);

        // Build the notification
        Notification notification = builder.build();
        // END_INCLUDE(buildNotification)

        // BEGIN_INCLUDE(customLayout)
        // Inflate the notification layout as RemoteViews
        RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.small_notification);

        // Set text on a TextView in the RemoteViews programmatically.
        final String time = DateFormat.getTimeInstance().format(new Date());
//        final String text = context.getResources().getString(R.string.collapsed, time);
//        contentView.setTextViewText(R.id.textView, text);
        contentView.setTextViewText(R.id.textAlbumName, time);
        contentView.setTextViewText(R.id.textSongName, "Title");

        /* Workaround: Need to set the content view here directly on the notification.
         * NotificationCompatBuilder contains a bug that prevents this from working on platform
         * versions HoneyComb.
         * See https://code.google.com/p/android/issues/detail?id=30495
         */

//        contentView.setOnClickPendingIntent(R.id.btnPause, intent);
        notification.contentView = contentView;

        // Add a big content view to the notification if supported.
        // Support for expanded notifications was added in API level 16.
        // (The normal contentView is shown when the notification is collapsed, when expanded the
        // big content view set here is displayed.)
        // Inflate and set the layout for the expanded notification view
        if (Build.VERSION.SDK_INT >= 16) {
            RemoteViews bigContentView = new RemoteViews(context.getPackageName(), R.layout.big_notification);
            bigContentView.setTextViewText(R.id.textAlbumName, time);
            notification.bigContentView = bigContentView;
        }
        // END_INCLUDE(customLayout)

        // START_INCLUDE(notify)
        // Use the NotificationManager to show the notification
        NotificationManager nm = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        assert nm != null;
        nm.notify(0, notification);
        // END_INCLUDE(notify)
    }
}
