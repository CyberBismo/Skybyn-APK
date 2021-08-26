package social.app.wesocial;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link notification#newInstance} factory method to
 * create an instance of this fragment.
 */
public class notification extends Fragment {

    Functions functions = new Functions();
    Data data = new Data();
    RecyclerView recyclerView;
    TextView lblNotificationsTitle;
    String userID;
    LottieAnimationView lottie;


    public notification() {
        // Required empty public constructor
    }

    public static notification newInstance(String param1, String param2) {

        notification fragment = new notification();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification, container,false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = getView().findViewById(R.id.notificationsRecyclerView);
        lblNotificationsTitle = getView().findViewById(R.id.lblNotificationsTitle);
        lottie = getActivity().findViewById(R.id.frontpageProgressView);
        userID = Frontpage.userID;
        loadNotification();
    }




    private void loadNotification() {
        functions.showProgress(lottie);
        HashMap<String, String> postData = new HashMap<String, String>();
        postData.put("user", userID);

        NetworkController networkController = new NetworkController(getActivity().getApplicationContext(), new NetworkController.IResult() {
            @Override
            public void notifySuccess(String response) throws JSONException {
                functions.hideProgress(lottie);
                if (response.equals("You've got no new notifications.")){
                    functions.showSnackBarError(response,getView().findViewById(android.R.id.content),getActivity().getApplicationContext());
                    return;
                }

                if (functions.isJsonArray(response)) {
                    String notificationContent;
                    String notificationTitle;
                    String notificationDate;
                    String notificationID;
                    Long unixNotificationDate;
                    String notificationAvatarLink;
                    String notificationPost = "";
                    String notificationPage = "";
                    String notificationGroup = "";
                    String notificationType = "";

                    JSONArray jsonArray = new JSONArray(response);

                    ArrayList<NotificationDataClass> notifications = new ArrayList();
                    JSONObject jsonObject = null;


                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        notificationContent = (String) jsonObject.get("content");
                        notificationAvatarLink = "";//(String) jsonObject.get("avatar");
                        notificationDate = (String) jsonObject.get("created").toString();
                        notificationDate = functions.convertUnixToDateAndTime(Long.valueOf(notificationDate));
                        notificationTitle = (String) jsonObject.get("title");
                        notificationID = (String) jsonObject.get("id");

                        notificationPost = (String) jsonObject.get("post").toString();
                        notificationPage = (String) jsonObject.get("page").toString();
                        notificationGroup = (String) jsonObject.get("group").toString();


                        //notificationType = getString(R.string.notifications);

                        if (!notificationPost.equals("null")) {
                            notificationType = "Post";
                        }
                        if (!notificationGroup.equals("null")) {
                            notificationType = "Group";
                        }
                        if (!notificationPage.equals("null")){
                            notificationType = "Page";
                        }

                        notifications.add(new NotificationDataClass(notificationContent, notificationAvatarLink, notificationDate, notificationTitle,notificationType,notificationID));
                        Log.i("JSON OBJECT",jsonObject.toString());
                    }

                    NotificationsAdapter notificationsAdapter = new NotificationsAdapter(notifications);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setAdapter(notificationsAdapter);
                    notificationsAdapter.notifyDataSetChanged();
                    lblNotificationsTitle.setText(getString(R.string.notifications)+" ("+recyclerView.getAdapter().getItemCount()+")");
                }
                if (!functions.isJsonArray(response.toString())) {
                    Toast.makeText(getActivity().getApplicationContext(), getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void notifyError(VolleyError error) {
                functions.hideProgress(lottie);
                Toast.makeText(getActivity().getApplicationContext(), getString(R.string.network_something_wrong), Toast.LENGTH_SHORT).show();
            }
        });

        networkController.PostMethod(data.notification_Api, postData);
    }


}