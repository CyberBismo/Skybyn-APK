package social.app.wesocial;

import static social.app.wesocial.Frontpage.userID;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.volley.VolleyError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Locale;

import timber.log.Timber;

public class PushNotificationService extends FirebaseMessagingService {
    private static final String CHANNEL_ID = "PUSH_CHANNEL_ID";
    public static String userNotificationtoken;
    public static String weSocial_Topic = "wesocial";
    public Data data = new Data();
    enum pushNotificationTypes{chat,general};
    int notificationId;
    Boolean gottenToken = false;


    public PushNotificationService() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        //Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();
                    Timber.i("TOKEN"+token);
                    NetworkController networkController = new NetworkController(getApplicationContext(), new NetworkController.IResult() {
                        @Override
                        public void notifySuccess(String response) {
                            Timber.i(response);
                        }

                        @Override
                        public void notifyError(VolleyError error) {

                        }

                    });
                    HashMap<String, String> postData = new HashMap<>();
                    postData.put("token",token);
                    networkController.PostMethod(data.updateToken_Api, postData);





                });


    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notifications_coloured)
                .setContentTitle(remoteMessage.getData().get("title"))
                .setContentText(remoteMessage.getData().get("body"))
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if (remoteMessage.getData().containsKey("type")) {
            switch (remoteMessage.getData().get("type")) {
                case "chat":
                    String fromID = remoteMessage.getData().get("from");
                    notificationId = 1;
            }
        }else{
            notificationId = 999;
        }

      //  if (!Frontpage.isVisible) {
            // notificationId is a unique int for each notification that you must define
            notificationManager.notify(notificationId, builder.build());
        
     //   }


    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

    }
}