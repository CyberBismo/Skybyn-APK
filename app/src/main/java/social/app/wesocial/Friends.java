package social.app.wesocial;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
 * Use the {@link Friends#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Friends extends Fragment {
LottieAnimationView lottie;
RecyclerView friendsRecyclerView;
TextView txtFriendsTitle;
Functions functions = new Functions();
Data data = new Data();

    public Friends() {
        // Required empty public constructor
    }

    public static Friends newInstance(String param1, String param2) {
        Friends fragment = new Friends();
        Bundle args = new Bundle();
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
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lottie = getActivity().findViewById(R.id.frontpageProgressView);
        friendsRecyclerView = getActivity().findViewById(R.id.friendsRecyclervView);
        txtFriendsTitle= getActivity().findViewById(R.id.txtFriendsTitle);
        loadFriends();

    }

    private void loadFriends() {
        functions.showProgress(lottie);
        HashMap<String, String> postData = new HashMap<>();
        postData.put("userID", Frontpage.userID);

        NetworkController networkController = new NetworkController(getActivity().getApplicationContext(), new NetworkController.IResult() {
            @Override
            public void notifySuccess(String response) throws JSONException {
                Log.i("response",response.toString());
                functions.hideProgress(lottie);

                if (!functions.isJsonArray(response)) {
                    functions.showSnackBarError(getActivity().getString(R.string.no_friends),getActivity().findViewById(android.R.id.content),getActivity().getApplicationContext());
                    Fragment timelineFragment = Timeline.newInstance("","");
                    functions.LoadFragment(timelineFragment, "timelinePosts",getActivity(),true);

                    return;
                }

                if (functions.isJsonArray(response)) {
                    String friendUsername;
                    String friendNickname = "";
                    String friendID = "";
                    String friendAvatarLink = "";

                    JSONArray jsonArray = new JSONArray(response);
                    ArrayList<FriendsDataClass> friendsDataClass = new ArrayList();
                    JSONObject jsonObject;


                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        friendID = (String) jsonObject.get("friend_id");
                        friendAvatarLink = jsonObject.get("avatar").toString();
                        friendNickname = (String) jsonObject.get("nickname");
                        friendUsername = (String) jsonObject.get("username");

                        friendsDataClass.add(new FriendsDataClass(friendID,friendAvatarLink,friendNickname,friendUsername));
                    }

                    FriendsAdapter friendsAdapter = new FriendsAdapter(friendsDataClass);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
                    friendsRecyclerView.setLayoutManager(mLayoutManager);
                    friendsRecyclerView.setAdapter(friendsAdapter);
                    friendsAdapter.notifyDataSetChanged();

                }

            }

            @Override
            public void notifyError(VolleyError error) {
                functions.hideProgress(lottie);
                Toast.makeText(getActivity().getApplicationContext(), getString(R.string.network_something_wrong), Toast.LENGTH_SHORT).show();
            }
        });

        networkController.PostMethod(data.friends_Api, postData);
    }



}