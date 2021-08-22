package social.app.wesocial;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class notifications<mAdapter> extends AppCompatActivity {
    String userID;
    Functions functions = new Functions();
    Data data = new Data();
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView = (RecyclerView) findViewById(R.id.notificationsRecyclerView);
        Intent i = getIntent();
        userID = i.getStringExtra("userID");
        loadNotification();
    }

    public  void loadNotification() {
        LottieAnimationView lottie;
        lottie = findViewById(R.id.notificationsProgressView);
        functions.showProgress(lottie);
        HashMap<String, String> postData = new HashMap<String, String>();
        postData.put("user", userID);

        NetworkController networkController = new NetworkController(getApplicationContext(), new NetworkController.IResult() {
            @Override
            public void notifySuccess(String response) throws JSONException {
                functions.hideProgress(lottie);

                Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                if (functions.isJsonArray(response)) {
                    Log.i("Json", response);
                    String notificationDetails;
                    String notificationTitle;
                    String notificationDate;
                    String notificationAvatarLink;

                    JSONArray jsonArray = new JSONArray(response);
                    ArrayList<NotificationList> notifications = new ArrayList();


                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        notificationDetails = (String) jsonObject.get("content");
                        notificationAvatarLink = (String) jsonObject.get("avatar");
                        notificationDate = (String) jsonObject.get("date");
                        notificationTitle = (String) jsonObject.get("title");
                        notifications.add(new NotificationList(notificationDetails, notificationAvatarLink, notificationDate, notificationTitle));

                    }

                    NotificationsAdapter notificationsAdapter = new NotificationsAdapter(notifications);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerView.setAdapter(notificationsAdapter);
                    notificationsAdapter.notifyDataSetChanged();
                }
                if (!functions.isJsonArray(response.toString())) {
                    Toast.makeText(getApplicationContext(), getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void notifyError(VolleyError error) {
                functions.hideProgress(lottie);
                Toast.makeText(getApplicationContext(), getString(R.string.network_something_wrong), Toast.LENGTH_SHORT).show();
            }
        });

        networkController.PostMethod(data.notification_Api, postData);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            default:
                break;

        }

        return super.onOptionsItemSelected(item);
    }

}