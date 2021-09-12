package social.app.wesocial;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Timeline extends Fragment {
Functions functions = new Functions();
Data data = new Data();
LottieAnimationView lottie;
RecyclerView recyclerView;
SwipeRefreshLayout mSwipeRefreshLayout;
Integer PostLength = 700;

    private void loadTimelinePosts() {
        functions.showProgress(lottie);
        HashMap<String, String> postData = new HashMap<>();
        postData.put("user", Frontpage.userID);

        NetworkController networkController = new NetworkController(getActivity().getApplicationContext(), new NetworkController.IResult() {
            @Override
            public void notifySuccess(String response) throws JSONException {
                Log.i("response",response.toString());
                functions.hideProgress(lottie);

                if (!functions.isJsonArray(response)) {
                    functions.showSnackBarError(getActivity().getString(R.string.no_timeline),getActivity().findViewById(android.R.id.content),getActivity().getApplicationContext());
                    return;
                }

                if (functions.isJsonArray(response)) {
                    String timelinePostContent;
                    String timelinePostUsername;
                    String timelinePostDate;
                    String timelineUserID;
                    String timelinePostID;
                    String timelineILike;
                    Long unixTimelinePostDate;
                    String timelineAvatarLink;
                    String timelinePostLikes = "";
                    String timelinePostCommentsCount = "";

                    JSONArray jsonArray = new JSONArray(response);
                    ArrayList<TimelineDataClass> timelinePost = new ArrayList();
                    JSONObject jsonObject;


                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        timelineAvatarLink = (String) jsonObject.get("avatar");
                        timelinePostDate = jsonObject.get("date").toString();
                        timelinePostDate = functions.convertUnixToDateAndTime(Long.valueOf(timelinePostDate));
                        timelinePostUsername = (String) jsonObject.get("username");
                        timelineUserID = (String) jsonObject.get("userID");
                        timelinePostID = (String) jsonObject.get("postID");
                        timelinePostLikes = jsonObject.get("likes").toString();
                        timelinePostCommentsCount = jsonObject.get("comments_count").toString();
                        timelinePostContent = (String) jsonObject.get("content");
                        timelineILike = jsonObject.get("ilike").toString();

                        timelinePost.add(new TimelineDataClass(timelinePostID,timelineUserID,timelinePostUsername,timelineAvatarLink,timelinePostDate,timelinePostContent,timelinePostCommentsCount,timelinePostLikes,timelineILike));
                    }

                    TimelinePostsAdapter timelinepostsAdapter = new TimelinePostsAdapter(timelinePost);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setAdapter(timelinepostsAdapter);
                    timelinepostsAdapter.notifyDataSetChanged();

                }

            }

            @Override
            public void notifyError(VolleyError error) {
                functions.hideProgress(lottie);
                Toast.makeText(getActivity().getApplicationContext(), getString(R.string.network_something_wrong), Toast.LENGTH_SHORT).show();
            }
        });

        networkController.PostMethod(data.timeline_Api, postData);
    }


    public Timeline() {
        // Required empty public constructor
    }

    public static Timeline newInstance(String param1, String param2) {
        Timeline fragment = new Timeline();
        Bundle args = new Bundle();

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {// Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_timeline, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTimelinePosts();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lottie = getActivity().findViewById(R.id.frontpageProgressView);
        recyclerView = getActivity().findViewById(R.id.postsRecyclerView);
        loadTimelinePosts();

        mSwipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.timelineSwipeToRefresh);

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            Toast.makeText(getActivity().getApplicationContext(),getString(R.string.refreshing),Toast.LENGTH_SHORT).show();
            loadTimelinePosts();
            mSwipeRefreshLayout.setRefreshing(false);
        });
    }

    }



