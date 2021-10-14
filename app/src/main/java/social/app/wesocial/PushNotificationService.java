package social.app.wesocial;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.volley.VolleyError;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;

import timber.log.Timber;

public class PushNotificationService extends FirebaseMessagingService {
    private static final String CHANNEL_ID = "PUSH_CHANNEL_ID";
    public static String userNotificationtoken;
    public static String weSocial_Topic = "wesocial";
    public Data data = new Data();
    int notificationId;

    Boolean gottenToken = false;

    public PushNotificationService() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Timber.i("Fetching FCM registration token failed" + " " + task.getException());
                        return;
                    }
                    // Get new FCM registration token
                    String token = task.getResult();
                    Frontpage.notificationToken = token;
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
                    postData.put("token", token);
                    postData.put("userID", Frontpage.userID);

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
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        if (remoteMessage.getData().containsKey("type")) {
            switch (remoteMessage.getData().get("type")) {
                case "chat":
                    String friendID = remoteMessage.getData().get("from");
                    builder.setSmallIcon(R.drawable.chat);
                default:
                    break;
            }

            builder.setContentTitle(remoteMessage.getData().get("title"))
                    .setContentText(remoteMessage.getData().get("body"))
                    .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            //  if (!Frontpage.isVisible) {
            // notificationId is a unique int for each notification that you must define
            notificationId = 9999;
            notificationManager.notify(notificationId, builder.build());

            //   }
        }
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


    enum pushNotificationTypes {chat, general}
}