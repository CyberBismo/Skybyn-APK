package social.app.wesocial;

import static social.app.wesocial.Frontpage.userID;

import android.app.NotificationManager;
import android.content.Context;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.android.volley.VolleyError;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Locale;

import timber.log.Timber;

public class PushNotificationService extends FirebaseMessagingService {
    Boolean gottenToken = false;
    public Data data = new Data();

    public static String  userNotificationtoken;
    public static String weSocial_Topic ="wesocial";


    public PushNotificationService() {
        FirebaseInstallations.getInstance().getId().addOnCompleteListener(
                task -> {
                    if (task.isSuccessful()) {
                        String token = task.getResult();
                        Timber.i("token ---->>%s", token);
                        Frontpage.gottenToken = true;
                        FirebaseMessaging.getInstance().subscribeToTopic(Frontpage.username.toLowerCase(Locale.ROOT));
                        userNotificationtoken = token;

                        //UPDATE TO DATABASE


                        HashMap<String, String> postData = new HashMap<>();
                        postData.put("userID", userID);
                        postData.put("token", userNotificationtoken);

                        NetworkController networkController = new NetworkController(getApplicationContext(), new NetworkController.IResult() {
                            @Override
                            public void notifySuccess(String response) throws JSONException {
                            Timber.i(response);
                            }

                            @Override
                            public void notifyError(VolleyError error) {

                            }

                        });
                        networkController.PostMethod(data.updateToken_Api,postData); }});


    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Toast.makeText(getApplicationContext(),remoteMessage.getData().toString(),Toast.LENGTH_LONG).show();
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setContentTitle(remoteMessage.getFrom())
                .setContentText(remoteMessage.getData().toString())
                .setAutoCancel(true);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(9999999, notificationBuilder.build());

        Timber.i(remoteMessage.getData().toString());


    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

    }
}