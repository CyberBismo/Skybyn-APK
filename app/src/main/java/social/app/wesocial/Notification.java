package social.app.wesocial;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.Objects;

import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Notification#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Notification extends Fragment {

    Functions functions = new Functions();
    Data data = new Data();
    RecyclerView recyclerView;
    TextView lblNotificationsTitle;
    LottieAnimationView lottie;


    public Notification() {
        // Required empty public constructor
    }

    public static Notification newInstance() {
        Notification fragment = new Notification();
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
        recyclerView = requireView().findViewById(R.id.notificationsRecyclerView);
        lblNotificationsTitle = requireView().findViewById(R.id.lblNotificationsTitle);
        lottie = requireActivity().findViewById(R.id.frontpageProgressView);
        loadNotification();
    }


    private void loadNotification() {
        functions.showProgress(lottie);
        HashMap<String, String> postData = new HashMap<>();
        postData.put("userID", Frontpage.userID);

        NetworkController networkController = new NetworkController(requireActivity().getApplicationContext(), new NetworkController.IResult() {
            @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void notifySuccess(String response) throws JSONException {
                functions.hideProgress(lottie);
                if (response.equals("You've got no new notifications.")){
                    //functions.showSnackBarError(response,requireView().findViewById(android.R.id.content),requireActivity().getApplicationContext());
                    return;
                }

                if (functions.isJsonArray(response)) {
                    Timber.i(response);
                    String notificationContent;
                    String notificationTitle;
                    String notificationDate;
                    String notificationID;
                    String notificationAvatarLink;
                    String notificationType;
                    String notificationRead;

                    JSONArray jsonArray = new JSONArray(response);

                    ArrayList<NotificationDataClass> notifications = new ArrayList<>();
                    JSONObject jsonObject;


                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        notificationContent = (String) jsonObject.get("content");
                        notificationAvatarLink = (String) jsonObject.get("avatar");
                        notificationDate = (String) jsonObject.get("date").toString();
                        notificationDate = functions.convertUnixToDateAndTime(Long.valueOf(notificationDate));
                        notificationTitle = (String) jsonObject.get("title");
                        notificationID = (String) jsonObject.get("notiID");
                        notificationRead = (String) jsonObject.get("read");
                        notificationType = (String) jsonObject.get("type");

                        notifications.add(new NotificationDataClass(notificationContent, notificationAvatarLink, notificationDate, notificationTitle,notificationType,notificationID,notificationRead));
                        }
                    NotificationsAdapter notificationsAdapter = new NotificationsAdapter(notifications);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(requireActivity().getApplicationContext());
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setAdapter(notificationsAdapter);
                    lblNotificationsTitle.setText(getString(R.string.notifications)+" ("+ Objects.requireNonNull(recyclerView.getAdapter()).getItemCount()+")");
                    notificationsAdapter.notifyDataSetChanged();
                }
                if (!functions.isJsonArray(response)) {
                    Toast.makeText(requireActivity().getApplicationContext(), getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void notifyError(VolleyError error) {
                functions.hideProgress(lottie);
                Toast.makeText(requireActivity().getApplicationContext(), getString(R.string.network_something_wrong), Toast.LENGTH_SHORT).show();
            }
        });

        networkController.PostMethod(data.list_notification_Api, postData);
    }


}