package social.app.wesocial;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
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
    CardView cardProfileEdit;

    private static String userID = "";
    //public static LottieAnimationView lottie;
    public ImageView imgUserCoverPhoto, imgUserProfilePhoto;
    public TextView txtUserProfileFullname, txtUserProfileUsername, txtEditProfile;

    String currentPassword, newPassword, confirmNewPassword;
    String newEmail, oldEmail;


    TextView txtProfilefirstName, txtProfilelastName, txtProfilemiddleName, txtProfilenickName, txtProfileAboutMe;
    TextView txtProfileCurrentPassword, txtProfileNewPassword, txtProfileConfirmNewPassword, txtProfileEmail;

    Button btnUpdateProfile, btnUpdateProfileEmail, btnUpdateProfilePassword;


    void retrieveIntentNotificationData() {
        Intent i = getIntent();
        userID = i.getStringExtra("userID");
        // loadUserProfile(userID);
        Timber.e("INTENT" + userID);

        if (userID.equalsIgnoreCase(Frontpage.userID)) {
            txtEditProfile.setVisibility(View.VISIBLE);
        } else {
            txtEditProfile.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        //retrieveIntentNotificationData();
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

    @Override
    protected void onPause() {
        super.onPause();
        recyclerView.invalidate();
    }


    public void updateEmailRequest() {
        newEmail = txtProfileEmail.getText().toString();

        if (newEmail.equalsIgnoreCase(Frontpage.email)) {
            txtProfileEmail.setError(getString(R.string.email_the_same));
            return;
        }

        if (newEmail.equals("")) {
            txtProfileEmail.setError(getString(R.string.email_required));
            return;
        }

        if (!functions.validateEmail(newEmail)) {
            txtProfileEmail.setError(getString(R.string.invalid_email));
            return;
        }


        HashMap<String, String> postData = new HashMap<>();
        postData.put("userID", Frontpage.userID);
        postData.put("email", Frontpage.email);
        postData.put("new_email", newEmail);

        functions.showProgress(lottie);

        NetworkController networkController = new NetworkController(getApplicationContext(), new NetworkController.IResult() {
            @Override
            public void notifySuccess(String response) throws JSONException {
                functions.hideProgress(lottie);
                if (functions.isJsonObject(response)) {
                    JSONObject jsonObject = new JSONObject(response);
                    String responseCode = jsonObject.get("responseCode").toString();
                    String message = jsonObject.get("message").toString();

                    if (responseCode.equals("0")) {
                        functions.showSnackBarError(message, findViewById(android.R.id.content), getApplicationContext());
                    }

                    if (responseCode.equals(data.requestSuccessful)) {
                        String token = jsonObject.get("token").toString();
                        AlertDialog.Builder alertName = new AlertDialog.Builder(cardProfileEdit.getContext(), R.style.AlertDialogCustom);
                        final EditText editTextName1 = new EditText(cardProfileEdit.getContext());
                        alertName.setTitle(getString(R.string.enter_the_verification_code));
                        alertName.setView(editTextName1);
                        LinearLayout layoutName = new LinearLayout(cardProfileEdit.getContext());
                        layoutName.setOrientation(LinearLayout.VERTICAL);
                        layoutName.addView(editTextName1); //displays the user input bar
                        alertName.setView(layoutName);

                        alertName.setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                        });

                        alertName.setPositiveButton("Continue", (dialog, whichButton) -> {
                            EditText dialogEditext = editTextName1; // variable to collect user input
                            if (!dialogEditext.getText().toString().equals(token)) {
                                functions.hideSoftKeyboard(userTimelineActivity.this);
                                functions.showSnackBarError(getString(R.string.invalid_verification_code), findViewById(android.R.id.content), getApplicationContext());
                                dialogEditext.setText("");
                            }

                            if (dialogEditext.getText().toString().equals(token)) {
                                functions.hideSoftKeyboard(userTimelineActivity.this);
                                HashMap<String, String> postData = new HashMap<>();
                                postData.put("userID", Frontpage.userID);
                                postData.put("new_email", newEmail);
                                //postData.put("email", Frontpage.email);
                                functions.showProgress(lottie);
                                NetworkController networkController1 = new NetworkController(getApplicationContext(), new NetworkController.IResult() {
                                    @Override
                                    public void notifySuccess(String response) throws JSONException {
                                        functions.hideProgress(lottie);
                                        if (functions.isJsonObject(response)) {
                                            JSONObject jsonObject = new JSONObject(response);
                                            String responseCode = jsonObject.get("responseCode").toString();
                                            String message = jsonObject.get("message").toString();
                                            if (responseCode.equals(data.requestSuccessful)) {
                                                functions.showSnackBar(message, findViewById(android.R.id.content), getApplicationContext());
                                                functions.hideSoftKeyboard(userTimelineActivity.this);
                                            }

                                            if (responseCode.equals("0")) {
                                                functions.hideSoftKeyboard(userTimelineActivity.this);
                                                functions.showSnackBarError(message, findViewById(android.R.id.content), getApplicationContext());
                                            }
                                        }
                                    }

                                    @Override
                                    public void notifyError(VolleyError error) {
                                        functions.hideProgress(lottie);
                                    }
                                });

                                networkController1.PostMethod(data.updateEmail_API, postData);
                            }
                        });
                        alertName.show();
                    }

                }
            }

            @Override
            public void notifyError(VolleyError error) {
                functions.hideProgress(lottie);
                functions.showSnackBarError(getString(R.string.network_something_wrong), findViewById(android.R.id.content), getApplicationContext());
            }
        });

        networkController.PostMethod(data.sendEmailToken_API, postData);
    }


    public void updatePasswordRequest() {
        currentPassword = txtProfileCurrentPassword.getText().toString();
        newPassword = txtProfileNewPassword.getText().toString();
        confirmNewPassword = txtProfileConfirmNewPassword.getText().toString();

        if (currentPassword.equals("")) {
            txtProfileCurrentPassword.setError(getString(R.string.password_required));
            return;
        }

        if (!currentPassword.equals(Frontpage.loginPassword)) {
            txtProfileCurrentPassword.setError(getString(R.string.invalid_currentPassword));
            return;
        }

        if (newPassword.equals("")) {
            txtProfileNewPassword.setError(getString(R.string.password_required));
            return;
        }
        if (confirmNewPassword.equals("")) {
            txtProfileConfirmNewPassword.setError(getString(R.string.password_required));
            return;
        }


        HashMap<String, String> postData = new HashMap<>();
        postData.put("userID", Frontpage.userID);
        postData.put("old_pw", currentPassword);
        postData.put("new_pw", newPassword);
        postData.put("cnew_pw", confirmNewPassword);

        functions.showProgress(lottie);
        btnUpdateProfilePassword.setEnabled(false);

        NetworkController networkController = new NetworkController(getApplicationContext(), new NetworkController.IResult() {
            @Override
            public void notifySuccess(String response) throws JSONException {
                functions.hideProgress(lottie);
                btnUpdateProfilePassword.setEnabled(true);
                Timber.i(response);
                if (functions.isJsonObject(response)) {
                    JSONObject jsonObject = new JSONObject(response);
                    String responseCode = jsonObject.get("responseCode").toString();
                    String message = jsonObject.get("message").toString();
                    switch (responseCode) {
                        case "1":
                            txtProfileCurrentPassword.setText("");
                            txtProfileNewPassword.setText("");
                            txtProfileConfirmNewPassword.setText("");
                            functions.showSnackBar(message, findViewById(android.R.id.content), getApplicationContext());
                            SharedPreferences.Editor sharedPrefEditor = Frontpage.sharedpreferences.edit();
                            sharedPrefEditor.putString("password", newPassword);
                            sharedPrefEditor.apply();

                            //Intent intent = new Intent(getApplicationContext(), LoginRegisterForgot.class);
                            //startActivity(intent);
                            break;

                        default:
                            functions.showSnackBarError(message, findViewById(android.R.id.content), getApplicationContext());
                            break;
                    }
                }
            }

            @Override
            public void notifyError(VolleyError error) {
                btnUpdateProfilePassword.setEnabled(true);
                functions.hideProgress(lottie);
                functions.showSnackBarError(getString(R.string.network_something_wrong), findViewById(android.R.id.content), getApplicationContext());
            }
        });
        networkController.PostMethod(data.updatePassword_API, postData);
    }

    public void updateProfileDetailsRequest() {
        String firstname = txtProfilefirstName.getText().toString();
        String lastname = txtProfilelastName.getText().toString();
        String middlename = txtProfilemiddleName.getText().toString();
        String nickname = txtProfilenickName.getText().toString();
        String bio = txtProfileAboutMe.getText().toString();


        HashMap<String, String> postData = new HashMap<>();
        postData.put("userID", Frontpage.userID);
        postData.put("first_name", firstname);
        postData.put("middle_name", middlename);
        postData.put("last_name", lastname);
        postData.put("nickname", nickname);
        postData.put("bio", bio);

        functions.showProgress(lottie);
        btnUpdateProfile.setEnabled(false);

        NetworkController networkController = new NetworkController(getApplicationContext(), new NetworkController.IResult() {
            @Override
            public void notifySuccess(String response) throws JSONException {
                functions.hideProgress(lottie);
                Timber.i(response);

                if (!functions.isJsonObject(response)) {
                    functions.showSnackBarError(getString(R.string.something_wrong), findViewById(android.R.id.content), getApplicationContext());
                }

                if (functions.isJsonObject(response)) {
                    JSONObject jsonObject = new JSONObject(response);
                    String responseCode = jsonObject.get("responseCode").toString();
                    String message = jsonObject.get("message").toString();
                    switch (responseCode) {
                        case "1":
                            functions.showSnackBar(message, findViewById(android.R.id.content), getApplicationContext());
                            btnUpdateProfile.setText(message);
                            break;

                        default:
                            btnUpdateProfile.setEnabled(true);
                            functions.showSnackBarError(message, findViewById(android.R.id.content), getApplicationContext());
                            break;
                    }
                }
            }

            @Override
            public void notifyError(VolleyError error) {
                btnUpdateProfile.setEnabled(true);
                functions.hideProgress(lottie);
                functions.showSnackBarError(getString(R.string.network_something_wrong), findViewById(android.R.id.content), getApplicationContext());
            }
        });
        networkController.PostMethod(data.update_profile_API, postData);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.bringToFront();
        lottie = findViewById(R.id.userTimelineProfileProgressView);


        functions = new Functions(getApplicationContext());

        imgUserProfilePhoto = findViewById(R.id.userProfilePhoto);
        imgUserCoverPhoto = findViewById(R.id.userProfileCoverPhoto);
        txtUserProfileFullname = findViewById(R.id.txtUserProfileFullname);
        txtUserProfileUsername = findViewById(R.id.txtUserProfileUsername);
        txtEditProfile = findViewById(R.id.txtEditProfile);

        cardProfileEdit = findViewById(R.id.cardprofileEdit);
        View profileEditLayout = LayoutInflater.from(this).inflate(R.layout.edit_profile, null);
        cardProfileEdit.addView(profileEditLayout);
        ImageView imgCloseEditProfile = cardProfileEdit.findViewById(R.id.imgCloseProfileEdit);
        txtProfilefirstName = cardProfileEdit.findViewById(R.id.txtProfileFirstname);
        txtProfilelastName = cardProfileEdit.findViewById(R.id.txtProfileLastname);
        txtProfilemiddleName = cardProfileEdit.findViewById(R.id.txtProfileMiddlename);
        txtProfilenickName = cardProfileEdit.findViewById(R.id.txtProfileNickname);
        txtProfileAboutMe = cardProfileEdit.findViewById(R.id.txtProfileAboutMe);

        //EMAIL
        txtProfileEmail = cardProfileEdit.findViewById(R.id.txtProfileEmail);
        //Passwords
        txtProfileCurrentPassword = cardProfileEdit.findViewById(R.id.txtProfileCurrentPassword);
        txtProfileNewPassword = cardProfileEdit.findViewById(R.id.txtProfileNewPassword);
        txtProfileConfirmNewPassword = cardProfileEdit.findViewById(R.id.txtProfileConfirmNewPassword);
        //Buttons
        btnUpdateProfile = cardProfileEdit.findViewById(R.id.btnUpdateProfile);
        btnUpdateProfileEmail = cardProfileEdit.findViewById(R.id.btnUpdateProfileEmail);
        btnUpdateProfilePassword = cardProfileEdit.findViewById(R.id.btnUpdateProfilePassword);
        //ASSIGNING
        txtProfilefirstName.setText(Frontpage.firstName);
        txtProfilelastName.setText(Frontpage.lastName);
        txtProfilemiddleName.setText(Frontpage.middleName);
        txtProfilenickName.setText(Frontpage.nickName);
        txtProfileAboutMe.setText(Frontpage.aboutMe);
        txtProfileEmail.setText(Frontpage.email);

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


        txtEditProfile.setOnClickListener(view -> {
            cardProfileEdit.setVisibility(View.VISIBLE);
        });

        //EVENTS

        btnUpdateProfile.setOnClickListener(view -> {
            updateProfileDetailsRequest();
        });

        btnUpdateProfilePassword.setOnClickListener(view1 -> {
            functions.hideSoftKeyboard(userTimelineActivity.this);
            updatePasswordRequest();
        });

        btnUpdateProfileEmail.setOnClickListener(view1 -> {
            functions.hideSoftKeyboard(userTimelineActivity.this);
            updateEmailRequest();

        });


        imgCloseEditProfile.setOnClickListener(view -> {
            cardProfileEdit.setVisibility(View.INVISIBLE);
        });
        retrieveIntentNotificationData();
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

                    if (responseCode.equals(data.requestSuccessful)) {

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
                        String bio = jsonObject.get("bio").toString();
                        String userTitle = jsonObject.get("title").toString();
                        String deactivated = jsonObject.get("deactivated").toString();

                        txtUserProfileUsername.setText(username);
                        TextView txtUserProfileAboutMe = findViewById(R.id.txtUserProfileAboutMe);
                        txtUserProfileAboutMe.setText(bio);

                        getSupportActionBar().setTitle(username.concat("'s").concat(" ").concat(getString(R.string.timeline)));
                        txtUserProfileFullname.setText(firstName.concat(" ").concat(lastName));
                        functions.loadProfilePictureDrawableThumb(avatarLink, imgUserProfilePhoto);
                        functions.loadProfilePictureDrawableThumb(avatarLink, imgUserCoverPhoto);
                        imgUserCoverPhoto.setColorFilter(R.color.dark_gray, PorterDuff.Mode.DARKEN);
                        imgUserProfilePhoto.bringToFront();

                    }
                }
            }


            @Override
            public void notifyError(VolleyError error) {
                functions.hideProgress(lottie);

            }
        });
        networkController.PostMethod(data.profile_API, postData);
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

        networkController.PostMethod(data.user_timeline_API, postData);
    }

}