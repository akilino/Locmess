package pt.ulisboa.tecnico.meic.cmu.locmess;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.view.View;

import pt.ulisboa.tecnico.meic.cmu.locmess.inbox.InboxActivity;
import pt.ulisboa.tecnico.meic.cmu.locmess.location.Location;
import pt.ulisboa.tecnico.meic.cmu.locmess.location.LocationActivity;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Akilino on 12/05/2017.
 */

public class NotificationActivity{

    static int numMessages = 0;

    public static void showNotification(Context context){

        int notificationID = 186318;
        int color = 0xff1B2845;

        Uri notificationSound = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_NOTIFICATION);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_freepik)
                        .setContentTitle("New Post")
                        .setContentText("You have a new Post to view")
                        .setAutoCancel(true)
                        .setColor(color)
                        .setSound(notificationSound);

        mBuilder.setContentText("You have a new Post to view").setNumber(++numMessages);

        Intent intent = new Intent(context, InboxActivity.class);

        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(pendingIntent);

        notificationManager.notify(notificationID, mBuilder.build());

    }

}
