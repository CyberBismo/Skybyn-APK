package social.app.wesocial;

import android.annotation.SuppressLint;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.VolleyError;
import com.like.LikeButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import timber.log.Timber;


public class showFullPost extends AppCompatActivity {
    TextView txtShowTimelinePostUsername;

    TextView txtShowTimelinePostDate,  txtShowTimelinePostLikes, txtShowTimelinePostComments;
    ImageView imgShowTimelinePostProfilePicture;
    TextView txtShowTimelinePostContent;
    Functions functions = new Functions();
    Data data = new Data();
    Button btnSendTimelineComment;
    TextView txtPostComment, txtShowTimelinePostEdit, txtShowTimelinePostDelete;
    LottieAnimationView lottie;
    NetworkController networkController;
    LikeButton btnShowtimePostLike;
    RecyclerView recyclerView;
    TextView txtCommentsCount;
    String postID, postAvatarlink, posterUserID, postUsername, postContent, postLikes, postCommentsCount, userLikedPost, postDate;
    HashMap<String, Object> timelinePostDetails = new HashMap<>();
    ActionBar actionBar;


    private void sendLike() {

        HashMap<String, String> postData = new HashMap<>();
        postData.put("userID", Frontpage.userID);
        postData.put("postID", postID);
        NetworkController networkController = new NetworkController(getApplicationContext(), new NetworkController.IResult() {
            @Override
            public void notifySuccess(String response) throws JSONException {
                Timber.i(response);
                if (functions.isJsonObject(response)) {
                    JSONObject jsonObject = new JSONObject(response);
                    String responseCode = jsonObject.get("responseCode").toString();
                    String message = jsonObject.get("message").toString();
                    String likes = "";

                    if (responseCode.equals("1")) {
                        likes = jsonObject.get("likes").toString();
                        btnShowtimePostLike.setLiked(true);
                        btnShowtimePostLike.setTag("1");
                        userLikedPost = "1";
                    }

                    if (responseCode.equals("2")) {
                        likes = jsonObject.get("likes").toString();
                        btnShowtimePostLike.setLiked(false);
                        btnShowtimePostLike.setTag("0");
                        userLikedPost = "0";
                    }
                    txtShowTimelinePostLikes.setText(likes);
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();


                }
            }

            @Override
            public void notifyError(VolleyError error) {
                Toast.makeText(getApplicationContext(), getString(R.string.network_something_wrong), Toast.LENGTH_SHORT).show();
            }
        });
        networkController.PostMethod(data.like_Api, postData);


    }

    private void getAllViewsbyID() {
        txtShowTimelinePostUsername = findViewById(R.id.txtShowTimelinePostUsername);
        txtCommentsCount = findViewById(R.id.txtCommentsCount);
        txtShowTimelinePostDate = findViewById(R.id.txtShowTimelinePostDate);
        imgShowTimelinePostProfilePicture = findViewById(R.id.imgShowTimelinePostProfilePicture);
        txtShowTimelinePostContent = findViewById(R.id.txtShowTimelinePostContent);
        txtShowTimelinePostLikes = findViewById(R.id.txtShowTimelinePostLikes);
        txtShowTimelinePostComments = findViewById(R.id.txtShowTimelinePostComments);
        txtShowTimelinePostDelete = findViewById(R.id.txtCommentDelete);
        txtShowTimelinePostEdit = findViewById(R.id.txtShowTimelinePostEdit);
        btnSendTimelineComment = findViewById(R.id.btnSendTimelineComment);
        txtPostComment = findViewById(R.id.txtShowPostComment);
        lottie = findViewById(R.id.showTimelinePostProgressView);
        recyclerView = findViewById(R.id.commentsRecyclerview);
        btnShowtimePostLike = findViewById(R.id.btnCommentLike);
    }

    void verifyIfIamPoster() {
        //CHECK to see if Delete and Edit option should be visible
        if (posterUserID.equals(Frontpage.userID)) {
            txtShowTimelinePostDelete.setVisibility(View.VISIBLE);
            txtShowTimelinePostEdit.setVisibility(View.VISIBLE);
        } else {
            txtShowTimelinePostDelete.setVisibility(View.INVISIBLE);
            txtShowTimelinePostEdit.setVisibility(View.INVISIBLE);
        }

    }

    public void getPostIntentContent() {
        Intent i = getIntent();
        timelinePostDetails = (HashMap<String, Object>) i.getSerializableExtra("timeLinePostDetails");
        postUsername = Objects.requireNonNull(timelinePostDetails.get("username")).toString();
        posterUserID = Objects.requireNonNull(timelinePostDetails.get("userID")).toString();
        postID = Objects.requireNonNull(timelinePostDetails.get("postID")).toString();
        postDate = Objects.requireNonNull(timelinePostDetails.get("date")).toString();
        postContent = Objects.requireNonNull(timelinePostDetails.get("content")).toString();
        postLikes = Objects.requireNonNull(timelinePostDetails.get("likes")).toString();
        postAvatarlink = Objects.requireNonNull(timelinePostDetails.get("avatarLink")).toString();
        postCommentsCount = Objects.requireNonNull(timelinePostDetails.get("comments_count")).toString();
        userLikedPost = Objects.requireNonNull(timelinePostDetails.get("ilike")).toString();
        actionBar.setTitle("Comments:" + " " + postContent);
    }

    private void loadComments() {
        functions.showProgress(lottie);
        HashMap<String, String> postData = new HashMap<>();
        postData.put("userID", Frontpage.userID);
        postData.put("postID", postID);

        NetworkController networkController = new NetworkController(getApplicationContext(), new NetworkController.IResult() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void notifySuccess(String response) throws JSONException {
                Timber.i(response);
                functions.hideProgress(lottie);

                if (functions.isJsonArray(response)) {
                    String commentContent;
                    String commentUsername;
                    String commentDate;
                    String commentUserID;
                    String commentID;
                    String commentILike;
                    String commentAvatarLink;
                    String commentLikes;
                    String commentPostID;

                    JSONArray jsonArray = new JSONArray(response);
                    ArrayList<CommentDataClass> commentData = new ArrayList<>();
                    JSONObject jsonObject;


                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        commentAvatarLink = (String) jsonObject.get("avatar");
                        commentDate = jsonObject.get("date").toString();
                        commentDate = functions.convertUnixToDateAndTimeNoGMT(Long.valueOf(commentDate));
                        commentUsername = (String) jsonObject.get("username");
                        commentUserID = (String) jsonObject.get("userID");
                        commentID = (String) jsonObject.get("commentID");
                        commentLikes = jsonObject.get("likes").toString();
                        commentContent = (String) jsonObject.get("content");
                        commentILike = jsonObject.get("ilike").toString();
                        commentPostID = jsonObject.get("postID").toString();


                        commentData.add(new CommentDataClass(commentID, commentUserID, commentUsername, commentAvatarLink, commentDate, commentContent, commentLikes, commentILike, commentPostID));
                    }

                    CommentsAdapter commentsAdapter = new CommentsAdapter(commentData,Frontpage.userID);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setAdapter(commentsAdapter);
                    commentsAdapter.notifyDataSetChanged();
                    txtCommentsCount.setText("Comments(" + commentsAdapter.getItemCount() + ")");
                    recyclerView.smoothScrollToPosition(commentsAdapter.getItemCount() -1);


                }

            }

            @Override
            public void notifyError(VolleyError error) {
                functions.hideProgress(lottie);
                Toast.makeText(getApplicationContext(), getString(R.string.network_something_wrong), Toast.LENGTH_SHORT).show();
            }
        });
        networkController.PostMethod(data.list_comments_Api, postData);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

             super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showfullpost);
        actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        getAllViewsbyID();
        getPostIntentContent();
        verifyIfIamPoster();
        loadComments();


        functions.loadProfilePictureDrawableThumb(postAvatarlink, imgShowTimelinePostProfilePicture);
        txtShowTimelinePostUsername.setText(postUsername);
        txtShowTimelinePostUsername.setTag(posterUserID);
        txtShowTimelinePostDate.setText(postDate);
        txtShowTimelinePostContent.setText(postContent);
        txtShowTimelinePostLikes.setText(postLikes);
        txtShowTimelinePostComments.setText(postCommentsCount);
        btnShowtimePostLike.setTag(userLikedPost);





        btnShowtimePostLike.setLiked(userLikedPost.equals("1"));
        //LIKE BUTTON
        btnShowtimePostLike.setOnClickListener(view -> {

            int postLikes = Integer.parseInt(txtShowTimelinePostLikes.getText().toString());

            //If user has not liked the post before...
            if (userLikedPost.equals("0")) {
                btnShowtimePostLike.setLiked(true);
                txtShowTimelinePostLikes.setText(String.valueOf(postLikes + 1));
            } else {
                btnShowtimePostLike.setLiked(false);
                if (Integer.parseInt(txtShowTimelinePostLikes.getText().toString()) > 0)
                    txtShowTimelinePostLikes.setText(String.valueOf(postLikes - 1));
            }
            sendLike();
        });


        txtShowTimelinePostEdit.setOnClickListener(view -> {

                // get alert_dialog.xml view
                LayoutInflater li = LayoutInflater.from(getApplicationContext());
                View editLayout = li.inflate(R.layout.editdialog, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        this,R.style.AlertDialogCustom);

                // set alert_dialog.xml to alertdialog builder
                alertDialogBuilder.setView(editLayout);

                final EditText txtContent = editLayout.findViewById(R.id.etUserInput);
                txtContent.setText(txtShowTimelinePostContent.getText().toString());
                txtContent.setSelection(txtContent.length());

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.save), (dialog, id) -> {
                            //SAVE ACTION
                            HashMap<String, String> postData = new HashMap<>();
                            postData.put("userID", Frontpage.userID);
                            postData.put("postID", postID);
                            postData.put("content", txtContent.getText().toString());


                            NetworkController networkController = new NetworkController(getApplicationContext(), new NetworkController.IResult() {
                                @Override
                                public void notifySuccess(String response) throws JSONException {
                                    Timber.i(response);

                                    if (!functions.isJsonObject(response)) {
                                        Toast.makeText(getApplicationContext(),getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                                    }

                                    if (functions.isJsonObject(response)) {
                                        JSONObject jsonObject = new JSONObject(response);
                                        String responseCode = jsonObject.get("responseCode").toString();
                                        String message = jsonObject.get("message").toString();
                                        String content = jsonObject.get("content").toString();
                                        if (responseCode.equals("1")) {
                                            txtShowTimelinePostContent.setText(content);
                                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                        }

                                        if (responseCode.equals("0")) {
                                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                }

                                @Override
                                public void notifyError(VolleyError error) {
                                    //functions.showSnackBar(holder.itemView.getContext().getString(string.network_something_wrong),holder.itemView.findViewById(android.R.id.content), holder.itemView.getContext());
                                }
                            });

                            Toast.makeText(getApplicationContext(), getString(R.string.updating_post),Toast.LENGTH_SHORT).show();
                            networkController.PostMethod(data.editPost_Api, postData);



                        })
                        .setNegativeButton("Cancel",
                                (dialog, id) -> dialog.dismiss());

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();

        });


        txtShowTimelinePostDelete.setOnClickListener(view -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
            alertDialogBuilder.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.warning));
            alertDialogBuilder.setMessage(getString(R.string.deletePOST));
            alertDialogBuilder.setTitle(getString(R.string.deletePostTitle));
            alertDialogBuilder.setNegativeButton(getString(R.string.no), (dialog, which) -> dialog.dismiss());
            alertDialogBuilder.setPositiveButton(getString(R.string.yes_delete),
                    (dialog, arg1) -> {
                        HashMap<String, String> postData = new HashMap<>();
                        postData.put("userID", Frontpage.userID);
                        postData.put("postID", postID);

                        functions.showProgress(lottie);
                        NetworkController networkController = new NetworkController(getApplicationContext(), new NetworkController.IResult() {

                            @Override
                            public void notifySuccess(String response) throws JSONException {
                                Timber.i(response);
                                functions.hideProgress(lottie);
                                if (functions.isJsonObject(response)) {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String responseCode = jsonObject.get("responseCode").toString();
                                    String message = jsonObject.get("message").toString();
                                    if (responseCode.equals("1")) {
                                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                        finish(); }

                                    if (responseCode.equals("0")) {
                                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                    }

                                }
                            }

                            @Override
                            public void notifyError(VolleyError error) {
                                functions.hideProgress(lottie);
                            }
                        });

                        networkController.PostMethod(data.deletePost_Api, postData);
                    });
            alertDialogBuilder.show();
            //alertDialog.show();
        });


        //Send Comment
        btnSendTimelineComment.setOnClickListener(view -> {
            functions.hideSoftKeyboard(showFullPost.this);
            String content = txtPostComment.getText().toString();
            if (content.length() <= 0) {
                functions.showSnackBarError(getString(R.string.comment_too_short), findViewById(android.R.id.content), getApplicationContext());
                return;
            }


            if (content.length() > data.maxCommentPostLength) {
                functions.showSnackBarError(getString(R.string.comment_longer_than).concat(" ".concat(data.maxCommentPostLength.toString())), findViewById(android.R.id.content), getApplicationContext());
                return;
            }

            functions.showProgress(lottie);
            HashMap<String, String> postData = new HashMap<>();
            postData.put("userID", Frontpage.userID);
            postData.put("postID", postID);
            postData.put("content", content);

            networkController = new NetworkController(this, new NetworkController.IResult() {
                @Override
                public void notifySuccess(String response) throws JSONException {
                    Timber.i(response);
                    functions.hideProgress(lottie);
                    if (functions.isJsonObject(response)) {
                        JSONObject jsonObject = new JSONObject(response);
                        String message = jsonObject.get("message").toString();
                        String responseCode = jsonObject.get("responseCode").toString();

                        if (responseCode.equals("1")) {
                            functions.showSnackBar(message, findViewById(android.R.id.content), getApplicationContext());
                            txtPostComment.setText("");
                            loadComments();
                        }

                        if (responseCode.equals("0")) {
                            functions.showSnackBarError(message, findViewById(android.R.id.content), getApplicationContext());
                        }
                        btnSendTimelineComment.setEnabled(true);

                    }

                }

                @Override
                public void notifyError(VolleyError error) {
                    functions.hideProgress(lottie);
                    btnSendTimelineComment.setEnabled(true);
                    functions.showSnackBarError(getString(R.string.error_commenting), findViewById(android.R.id.content), getApplicationContext());
                }
            });
            networkController.PostMethod(data.add_comment_Api, postData);
            //Toast.makeText(getApplicationContext(),getString(R.string.posting_comment),Toast.LENGTH_SHORT).show();
            btnSendTimelineComment.setEnabled(false);
        });

        //Profile picture and name click
        imgShowTimelinePostProfilePicture.setOnClickListener(view -> {
            functions.loadTimeLineUserProfile(posterUserID,showFullPost.this,getApplicationContext()); });
        txtShowTimelinePostUsername.setOnClickListener(view -> imgShowTimelinePostProfilePicture.callOnClick());


        //END OF ON CREATE
    }





    @Override
    protected void onResume() {
        super.onResume();


    }
}