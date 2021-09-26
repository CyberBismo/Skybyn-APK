package social.app.wesocial;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


import static android.content.ContentValues.TAG;

import androidx.core.app.NotificationCompat;

import timber.log.Timber;

public class PushNotificationService extends FirebaseMessagingService {
    Boolean gottenToken = false;
    public static String  userNotificationtoken;
    public PushNotificationService() {

        FirebaseInstallations.getInstance().getId().addOnCompleteListener(
                    task -> {
                        if (task.isSuccessful()) {
                            String token = task.getResult();
                            Timber.i("token ---->>" + token);
                            Frontpage.gottenToken = true;
                            userNotificationtoken = token;

                        }
                    }
            );

    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(remoteMessage.getFrom())
                .setContentText(remoteMessage.getData().toString())
                .setAutoCancel(true);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(9999999, notificationBuilder.build());




    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

    }
}