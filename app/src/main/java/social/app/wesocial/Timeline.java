package social.app.wesocial;

import static social.app.wesocial.Functions.fragmentContainerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.VolleyError;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import timber.log.Timber;

public class Timeline extends Fragment {
    Functions functions = new Functions();
    Data data = new Data();
    LottieAnimationView lottie;
    RecyclerView recyclerView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    public String timelinePostsJson = "";

    private void displayTimelinePosts(String response) throws JSONException {
    String timelinePostContent;
    String timelinePostUsername;
    String timelinePostDate;
    String timelineUserID = null;
    String timelinePostID;
    String timelineILike;

    String timelineAvatarLink;
    String timelinePostLikes;
    String timelinePostCommentsCount;

    JSONArray jsonArray = new JSONArray(response);
    ArrayList<TimelineDataClass> timelinePost = new ArrayList<>();
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

        timelinePost.add(new TimelineDataClass(timelinePostID, timelineUserID, timelinePostUsername, timelineAvatarLink, timelinePostDate, timelinePostContent, timelinePostCommentsCount, timelinePostLikes, timelineILike));
    }

    TimelinePostsAdapter timelinepostsAdapter = new TimelinePostsAdapter(timelinePost,false,timelineUserID,getActivity());
    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(requireActivity().getApplicationContext());
    recyclerView.setLayoutManager(mLayoutManager);
    recyclerView.setAdapter(timelinepostsAdapter);
    timelinepostsAdapter.notifyDataSetChanged();

}

    private void loadTimelinePosts() throws JSONException
    {
        if (functions.isJsonArray(timelinePostsJson)) {
            displayTimelinePosts(timelinePostsJson);
            Timber.i("True");
        }else{
            functions.showProgress(lottie);
            Timber.i("false");
        }

        Frontpage.isTimeline = true;
        CoordinatorLayout bottomLayout = requireActivity().findViewById(R.id.bottomLayout);
        bottomLayout.setVisibility(View.VISIBLE);

        HashMap<String, String> postData = new HashMap<>();
        postData.put("userID", Frontpage.userID);

        NetworkController networkController = new NetworkController(requireActivity().getApplicationContext(), new NetworkController.IResult() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void notifySuccess(String response) throws JSONException {
                functions.hideProgress(lottie);
                Timber.i(response);

                if (!functions.isJsonArray(response)) {
                    functions.showSnackBarError(requireActivity().getString(R.string.no_timeline), requireActivity().findViewById(android.R.id.content), requireActivity().getApplicationContext());
                    return;
                }

                if (functions.isJsonArray(response)) {
                    timelinePostsJson = response;
                    displayTimelinePosts(response);

                }

            }

            @Override
            public void notifyError(VolleyError error) {
                functions.hideProgress(lottie);
                Snackbar snackbar = Snackbar
                        .make(requireActivity().findViewById(android.R.id.content), getString(R.string.network_something_wrong), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.retry), view -> {
                            try {
                                loadTimelinePosts();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        });

                snackbar.show();
            }
        });

        networkController.PostMethod(data.timeline_Api, postData);
    }


    public Timeline() {
        // Required empty public constructor
    }

    public static Timeline newInstance() {
        return new Timeline();
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
        try {
            loadTimelinePosts();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lottie = requireActivity().findViewById(R.id.frontpageProgressView);
        recyclerView = requireActivity().findViewById(R.id.postsRecyclerView);

        try {
            loadTimelinePosts();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSwipeRefreshLayout = requireView().findViewById(R.id.timelineSwipeToRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            Toast.makeText(requireActivity().getApplicationContext(), getString(R.string.refreshing), Toast.LENGTH_SHORT).show();
            try {
                loadTimelinePosts();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mSwipeRefreshLayout.setRefreshing(false);
        });
    }

}



