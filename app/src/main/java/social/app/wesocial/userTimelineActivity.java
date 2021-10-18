package social.app.wesocial;

import static social.app.wesocial.Functions.fragmentContainerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import timber.log.Timber;

public class userTimelineActivity extends AppCompatActivity {
    Functions functions;
    public LottieAnimationView lottie;
    Data data = new Data();
    RecyclerView recyclerView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    public String timelinePostsJson = "";
    TimelinePostsAdapter timelinepostsAdapter;

    private static String userID = "";
    //public static LottieAnimationView lottie;
    public ImageView imgUserCoverPhoto, imgUserProfilePhoto;
    public TextView txtUserProfileFullname, txtUserProfileUsername;


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

    @Override
    protected void onPause() {
        super.onPause();
        recyclerView.invalidate();
     }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_timeline);
         lottie = findViewById(R.id.userTimelineProfileProgressView);

         ActionBar actionBar = getSupportActionBar();
         actionBar.setDisplayHomeAsUpEnabled(true);
        functions= new Functions(getApplicationContext());
        imgUserProfilePhoto = findViewById(R.id.userProfilePhoto);
        imgUserCoverPhoto = findViewById(R.id.userProfileCoverPhoto);
        txtUserProfileFullname = findViewById(R.id.txtUserProfileFullname);
        txtUserProfileUsername = findViewById(R.id.txtUserProfileUsername);

        Intent i = getIntent();
        userID = i.getStringExtra("userID");



        recyclerView = findViewById(R.id.userTimelinepostsRecyclerView);
        mSwipeRefreshLayout = findViewById(R.id.userTimelineSwipeToRefresh);
        mSwipeRefreshLayout.bringToFront();
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            Toast.makeText(getApplicationContext(), getString(R.string.refreshing), Toast.LENGTH_SHORT).show();
            try {
                loadTimelinePosts();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mSwipeRefreshLayout.setRefreshing(false);
        });
        loadUserProfile(userID);



    }

    public void loadUserProfile(String userID) {
        functions.showProgress(lottie);
        HashMap<String, String> postData = new HashMap<>();
        postData.put("userID", userID);
        NetworkController networkController = new NetworkController(getApplicationContext(), new NetworkController.IResult() {
            @Override
            public void notifySuccess(String response) throws JSONException {
                //load profile picture thumb after profile loads.
                functions.hideProgress(lottie);
                if (functions.isJsonObject(response)) {
                    Log.i("Json", response);
                    JSONObject jsonObject = new JSONObject(response);
                    String responseCode = jsonObject.get("responseCode").toString();

                    if (responseCode.equals("1")) {

                        try {
                            loadTimelinePosts();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        String username = jsonObject.getString("username");
                        String email = jsonObject.getString("email");
                        String avatarLink = jsonObject.getString("avatar");
                        String firstName = jsonObject.get("fname").toString();
                        String lastName = jsonObject.get("lname").toString();
                        String nickName = jsonObject.get("nickname").toString();
                        String middleName = jsonObject.get("mname").toString();
                        String userRank = jsonObject.get("rank").toString();
                        String userTitle = jsonObject.get("title").toString();
                        String deactivated = jsonObject.get("deactivated").toString();

                        txtUserProfileUsername.setText(username);

                        getSupportActionBar().setTitle(username.concat("'s").concat(" ").concat(getString(R.string.timeline)));

                        if (firstName.equals("") || lastName.equals("")) {
                            txtUserProfileFullname.setText("------");
                        } else {
                            txtUserProfileFullname.setText(firstName.concat(" ").concat(lastName));
                        }

                        functions.loadProfilePictureDrawableThumb(avatarLink, imgUserProfilePhoto);
                        functions.loadProfilePictureDrawableThumb(avatarLink, imgUserCoverPhoto);
                        imgUserCoverPhoto.setColorFilter(R.color.dark_gray, PorterDuff.Mode.DARKEN);
                        imgUserProfilePhoto.bringToFront();


                    }
                }
            }


            @Override
            public void notifyError(VolleyError error) {

            }
        });
        networkController.PostMethod(data.profile_Api, postData);
    }

    private void displayTimelinePosts(String response) throws JSONException {
        String timelinePostContent;
        String timelinePostUsername;
        String timelinePostDate;
        String timelineUserID;
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

         timelinepostsAdapter = new TimelinePostsAdapter(timelinePost, true, userID, userTimelineActivity.this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(timelinepostsAdapter);
        timelinepostsAdapter.notifyDataSetChanged();

    }

    private void loadTimelinePosts() throws JSONException {
        if (functions.isJsonArray(timelinePostsJson)) {
            displayTimelinePosts(timelinePostsJson);
            Timber.i("True");
        } else {
            functions.showProgress(lottie);
            Timber.i("false");
        }

        HashMap<String, String> postData = new HashMap<>();
        postData.put("userID", userID);

        NetworkController networkController = new NetworkController(getApplicationContext(), new NetworkController.IResult() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void notifySuccess(String response) throws JSONException {
                Timber.i(response);

                if (!functions.isJsonArray(response)) {
                    functions.showSnackBarError(getString(R.string.no_timeline), findViewById(android.R.id.content), getApplicationContext());
                    return;
                }

                if (functions.isJsonArray(response)) {
                    timelinePostsJson = response;
                    displayTimelinePosts(response);

                }
                  functions.hideProgress(lottie);

            }

            @Override
            public void notifyError(VolleyError error) {
                functions.hideProgress(lottie);
                Toast.makeText(getApplicationContext(), getString(R.string.network_something_wrong), Toast.LENGTH_SHORT).show();
            }
        });

        networkController.PostMethod(data.user_timeline_Api, postData);
    }

}