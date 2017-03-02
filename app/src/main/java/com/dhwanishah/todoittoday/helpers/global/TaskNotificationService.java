package com.dhwanishah.todoittoday.helpers.global;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.dhwanishah.todoittoday.MainActivity;
import com.dhwanishah.todoittoday.SplashActivity;

/**
 * Created by DhwaniShah on 3/2/17.
 */

public class TaskNotificationService {

    private Context mCurrentContext;
    private int mId = 12334;

    public TaskNotificationService(Context context) {
        this.mCurrentContext = context;
    }

    public void createSimpleNotification(int icon, String notificationTitle, String notificationBody) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mCurrentContext)
                        .setSmallIcon(icon)
                        .setContentTitle(notificationTitle)
                        .setContentText(notificationBody);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(mCurrentContext, SplashActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(mCurrentContext);
            // Adds the back stack for the Intent (but not the Intent itself)
            stackBuilder.addParentStack(MainActivity.class);
            // Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(resultPendingIntent);
        }
        NotificationManager mNotificationManager = (NotificationManager) mCurrentContext.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(this.mId, mBuilder.build());
    }

    public int getmId() { return mId; }

    public void setmId(int mId) { this.mId = mId; }
}
