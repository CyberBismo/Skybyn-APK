package social.app.wesocial;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.VolleyError;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import timber.log.Timber;


public class Friends extends Fragment {
    static LottieAnimationView lottie;
    static Functions functions;
    static String action;
    RecyclerView friendsRecyclerView;
    TextView txtFriendsTitle;
    Data data = new Data();
    TabLayout friendsTabLayout;
    String reqResponse = "";


    public Friends() {
        // Required empty public constructor
    }

    public static Friends newInstance(String param1) {
        Friends fragment = new Friends();
        action = "";
        action = param1;

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
        lottie = requireActivity().findViewById(R.id.frontpageProgressView);
        friendsRecyclerView = requireActivity().findViewById(R.id.searchRecyclerView);
        txtFriendsTitle = requireActivity().findViewById(R.id.txtFriendsTitle);

        functions = new Functions(requireContext());

        if (action.equals(data.accept_friend_action)) {
            loadFriendsRequests();
        } else {
            loadFriends();
        }

        friendsTabLayout = requireActivity().findViewById(R.id.friendsTabLayout);

        friendsTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {


            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {

                    case 0:
                        loadFriends();
                        break;

                    case 1:
                        loadFriendsRequests();
                        break;

                    case 2:
                        loadBlockedFriends();
                        break;

                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }


        });

    }


    public void loadFriends() {
        if (reqResponse.equals("")) {
            functions.showProgress(lottie);
        }

        HashMap<String, String> postData = new HashMap<>();
        postData.put("userID", Frontpage.userID);

        NetworkController networkController = new NetworkController(requireActivity().getApplicationContext(), new NetworkController.IResult() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void notifySuccess(String response) throws JSONException {
                Timber.i(response);
                functions.hideProgress(lottie);

                if (!functions.isJsonArray(response)) {
                    functions.showSnackBarError(requireActivity().getString(R.string.no_friends), requireActivity().findViewById(android.R.id.content), requireActivity().getApplicationContext());
                    return;
                }

                if (functions.isJsonArray(response)) {
                    reqResponse = response;
                    String friendUsername;
                    String friendNickname;
                    String friendID;
                    String friendAvatarLink;

                    JSONArray jsonArray = new JSONArray(response);
                    ArrayList<FriendsDataClass> friendsDataClass = new ArrayList<>();
                    JSONObject jsonObject;

                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        friendID = (String) jsonObject.get("friend_id");
                        friendAvatarLink = jsonObject.get("avatar").toString();
                        friendNickname = (String) jsonObject.get("nickname");
                        friendUsername = (String) jsonObject.get("username");
                        String friendOnline = (String) jsonObject.get("online");

                        friendsDataClass.add(new FriendsDataClass(friendID, friendAvatarLink, friendNickname, friendUsername, friendOnline));
                    }

                    FriendsAdapter friendsAdapter = new FriendsAdapter(friendsDataClass);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(requireActivity().getApplicationContext());
                    friendsRecyclerView.setLayoutManager(mLayoutManager);
                    friendsRecyclerView.setAdapter(friendsAdapter);
                    friendsAdapter.notifyDataSetChanged();

                }

            }

            @Override
            public void notifyError(VolleyError error) {
                functions.hideProgress(lottie);
                Toast.makeText(requireActivity().getApplicationContext(), getString(R.string.network_something_wrong), Toast.LENGTH_SHORT).show();
            }
        });

        networkController.PostMethod(data.list_friends_API, postData);
    }


    private void loadFriendsRequests() {
        functions.showProgress(lottie);
        HashMap<String, String> postData = new HashMap<>();
        postData.put("userID", Frontpage.userID);

        NetworkController networkController = new NetworkController(requireActivity().getApplicationContext(), new NetworkController.IResult() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void notifySuccess(String response) throws JSONException {
                Timber.i(response);
                functions.hideProgress(lottie);

                if (!functions.isJsonArray(response)) {
                    functions.showSnackBarError(requireActivity().getString(R.string.no_friend_requests), requireActivity().findViewById(android.R.id.content), requireActivity().getApplicationContext());


                    return;
                }

                if (functions.isJsonArray(response)) {
                    String friendUsername;
                    String friendNickname;
                    String friendID;
                    String friendAvatarLink;

                    JSONArray jsonArray = new JSONArray(response);
                    ArrayList<FriendsDataClass> friendsDataClass = new ArrayList<>();
                    JSONObject jsonObject;

                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        friendID = (String) jsonObject.get("friend_id");
                        friendAvatarLink = jsonObject.get("avatar").toString();
                        friendNickname = (String) jsonObject.get("nickname");
                        friendUsername = (String) jsonObject.get("username");

                        friendsDataClass.add(new FriendsDataClass(friendID, friendAvatarLink, friendNickname, friendUsername, ""));
                    }

                    FriendsRequestsAdapter friendsRequestsAdapter = new FriendsRequestsAdapter(friendsDataClass);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(requireActivity().getApplicationContext());
                    friendsRecyclerView.setLayoutManager(mLayoutManager);
                    friendsRecyclerView.setAdapter(friendsRequestsAdapter);
                    friendsRequestsAdapter.notifyDataSetChanged();

                }

            }

            @Override
            public void notifyError(VolleyError error) {
                functions.hideProgress(lottie);
                Toast.makeText(requireActivity().getApplicationContext(), getString(R.string.network_something_wrong), Toast.LENGTH_SHORT).show();
            }
        });

        networkController.PostMethod(data.list_friendRequests_API, postData);
    }


    private void loadBlockedFriends() {
        functions.showProgress(lottie);
        HashMap<String, String> postData = new HashMap<>();
        postData.put("userID", Frontpage.userID);

        NetworkController networkController = new NetworkController(requireActivity().getApplicationContext(), new NetworkController.IResult() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void notifySuccess(String response) throws JSONException {
                Timber.i(response);
                functions.hideProgress(lottie);

                if (!functions.isJsonArray(response)) {
                    functions.showSnackBarError(requireActivity().getString(R.string.no_blocked_friends), requireActivity().findViewById(android.R.id.content), requireActivity().getApplicationContext());
                    return;
                }

                if (functions.isJsonArray(response)) {
                    String friendUsername;
                    String friendNickname;
                    String friendID;
                    String friendAvatarLink;

                    JSONArray jsonArray = new JSONArray(response);
                    ArrayList<FriendsDataClass> friendsDataClass = new ArrayList<>();
                    JSONObject jsonObject;

                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        friendID = (String) jsonObject.get("friend_id");
                        friendAvatarLink = jsonObject.get("avatar").toString();
                        friendNickname = (String) jsonObject.get("nickname");
                        friendUsername = (String) jsonObject.get("username");
                        friendsDataClass.add(new FriendsDataClass(friendID, friendAvatarLink, friendNickname, friendUsername, ""));
                    }

                    BlockedFriendsAdapter blockedFriendsAdapter = new BlockedFriendsAdapter(friendsDataClass);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(requireActivity().getApplicationContext());
                    friendsRecyclerView.setLayoutManager(mLayoutManager);
                    friendsRecyclerView.setAdapter(blockedFriendsAdapter);
                    blockedFriendsAdapter.notifyDataSetChanged();

                }

            }

            @Override
            public void notifyError(VolleyError error) {
                functions.hideProgress(lottie);
                Toast.makeText(requireActivity().getApplicationContext(), getString(R.string.network_something_wrong), Toast.LENGTH_SHORT).show();
            }
        });

        networkController.PostMethod(data.list_blockedFriends_API, postData);
    }


}