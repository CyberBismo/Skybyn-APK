package social.app.wesocial;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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


public class showFullPost extends AppCompatActivity {
    TextView txtShowTimelinePostUsername;
    TextView txtShowTimelinePostDate, txtShowTimelinePostContent, txtShowTimelinePostLikes, txtShowTimelinePostComments;
    ImageView imgShowTimelinePostProfilePicture;
    Functions functions = new Functions();
    Data data = new Data();
    Button btnSendTimelineComment;
    TextView txtPostComment, txtShowTimelinePostEdit, txtShowTimelinePostDelete;
    LottieAnimationView lottie;
    NetworkController networkController;
    LikeButton btnShowtimePostLike;
    RecyclerView recyclerView;
    TextView txtCommentsCount;
    String postID, postAvatarlink,posterUserID, postUsername, postContent, postLikes, postCommentsCount, userLikedPost, postDate;
    HashMap<String, Object> timelinePostDetails = new HashMap<>();
    ActionBar actionBar;


    private void sendLike() {

        HashMap<String, String> postData = new HashMap<>();
        postData.put("userID", Frontpage.userID);
        postData.put("postID", postID);
        NetworkController networkController = new NetworkController(getApplicationContext(), new NetworkController.IResult() {
            @Override
            public void notifySuccess(String response) throws JSONException {
                Log.i("response", response);
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

    public void getAllViewsbyID() {
        txtShowTimelinePostUsername = findViewById(R.id.txtShowTimelinePostUsername);
        txtCommentsCount = findViewById(R.id.txtCommentsCount);
        txtShowTimelinePostDate = findViewById(R.id.txtShowTimelinePostDate);
        imgShowTimelinePostProfilePicture = findViewById(R.id.imgShowTimelinePostProfilePicture);
        txtShowTimelinePostContent = findViewById(R.id.txtShowTimelinePostContent);
        txtShowTimelinePostLikes = findViewById(R.id.txtShowTimelinePostLikes);
        txtShowTimelinePostComments = findViewById(R.id.txtShowTimelinePostComments);
        txtShowTimelinePostDelete = findViewById(R.id.txtCommentDelete);
        txtShowTimelinePostEdit = findViewById(R.id.txtShowTielinePostEdit);
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
        postUsername = timelinePostDetails.get("username").toString();
        posterUserID = timelinePostDetails.get("userID").toString();
        postID = timelinePostDetails.get("postID").toString();
        postDate = timelinePostDetails.get("date").toString();
        postContent = timelinePostDetails.get("content").toString();
        postLikes = timelinePostDetails.get("likes").toString();
         postAvatarlink = timelinePostDetails.get("avatarLink").toString();
        postCommentsCount = timelinePostDetails.get("comments_count").toString();
        userLikedPost = timelinePostDetails.get("ilike").toString();
        actionBar.setTitle("Comments on:"+" "+postContent);
    }

    private void loadComments() {
        functions.showProgress(lottie);
        HashMap<String, String> postData = new HashMap<>();
        postData.put("userID", Frontpage.userID);
        postData.put("postID", postID);

        NetworkController networkController = new NetworkController(getApplicationContext(), new NetworkController.IResult() {
            @Override
            public void notifySuccess(String response) throws JSONException {
                Log.i("response",response.toString());
                functions.hideProgress(lottie);

                if (functions.isJsonArray(response)) {
                    String commentContent;
                    String commentUsername;
                    String commentDate;
                    String commentUserID;
                    String commentID;
                    String commentILike;
                    String commentAvatarLink;
                    String commentLikes = "";
                    String commentPostID = "";

                    JSONArray jsonArray = new JSONArray(response);
                    ArrayList<CommentDataClass> commentData = new ArrayList();
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


                        commentData.add(new CommentDataClass(commentID,commentUserID,commentUsername,commentAvatarLink,commentDate,commentContent,commentLikes,commentILike,commentPostID));
                    }

                    CommentsAdapter commentsAdapter = new CommentsAdapter(commentData);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setAdapter(commentsAdapter);
                    commentsAdapter.notifyDataSetChanged();
                    txtCommentsCount.setText("Comments("+String.valueOf(commentsAdapter.getItemCount())+")");

                    recyclerView.scrollToPosition(commentsAdapter.getItemCount());


                }

            }

            @Override
            public void notifyError(VolleyError error) {
                functions.hideProgress(lottie);
                Toast.makeText(getApplicationContext(), getString(R.string.network_something_wrong), Toast.LENGTH_SHORT).show();
            }
        });

        networkController.PostMethod(data.comments_Api, postData);
            }

    @Override

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        
        if (item.getItemId() ==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showfullpost);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        getAllViewsbyID();
        getPostIntentContent();
        verifyIfIamPoster();
        loadComments();
        Log.i("Timeline", timelinePostDetails.toString());




        txtPostComment.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus){
                    recyclerView.scrollToPosition(recyclerView.getChildCount());
                }
            }
        });
        functions.loadProfilePictureThumb(postAvatarlink, imgShowTimelinePostProfilePicture);
        txtShowTimelinePostUsername.setText(postUsername);
        txtShowTimelinePostDate.setText(postDate);
        txtShowTimelinePostContent.setText(postContent);
        txtShowTimelinePostLikes.setText(postLikes);
        txtShowTimelinePostComments.setText(postCommentsCount);
        btnShowtimePostLike.setTag(userLikedPost);



        if (userLikedPost.equals("1")) {
            btnShowtimePostLike.setLiked(true);
        } else {
            btnShowtimePostLike.setLiked(false);
        }
        //LIKE BUTTON
        btnShowtimePostLike.setOnClickListener(view -> {

            int postLikes = Integer.valueOf(txtShowTimelinePostLikes.getText().toString());

            //If user has not liked the post before...
            if (userLikedPost.equals("0")) {
                btnShowtimePostLike.setLiked(true);
                txtShowTimelinePostLikes.setText(String.valueOf(postLikes + 1));
            } else {
                btnShowtimePostLike.setLiked(false);
                if (Integer.valueOf(txtShowTimelinePostLikes.getText().toString()) > 0)
                    txtShowTimelinePostLikes.setText(String.valueOf(postLikes - 1));
            }
            sendLike();
        });

        //DELETE POST BUTTON with AlertDialog Confirmation
        txtShowTimelinePostDelete.setOnClickListener(view -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getApplicationContext());
            alertDialogBuilder.setMessage(getString(R.string.deletePOST));
            alertDialogBuilder.setTitle(getString(R.string.deletePostTitle));
            alertDialogBuilder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertDialogBuilder.setPositiveButton(getString(R.string.yes_delete),
                    (dialog, arg1) -> {
                        HashMap<String, String> postData = new HashMap<>();
                        postData.put("userID", Frontpage.userID);
                        postData.put("postID", postID);

                        functions.showProgress(lottie);
                        NetworkController networkController = new NetworkController(getApplicationContext(), new NetworkController.IResult() {

                            @Override
                            public void notifySuccess(String response) throws JSONException {
                                functions.hideProgress(lottie);
                                if (functions.isJsonObject(response)) {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String responseCode = jsonObject.get("responseCode").toString();
                                    String message = jsonObject.get("message").toString();
                                    if (responseCode.equals("1")) {
                                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                        finish();
                                    }

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
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        });


        //Send Comment
        btnSendTimelineComment.setOnClickListener(view -> {
            functions.hideSoftKeyboard(showFullPost.this);
            String content = txtPostComment.getText().toString();
            if (content.length() <= 0) {
                functions.showSnackBarError(getString(R.string.comment_too_short), findViewById(android.R.id.content), getApplicationContext());
                ;
                return; }


            if (content.length() > data.maxCommentPostLength) {
                functions.showSnackBarError(getString(R.string.comment_longer_than).concat(" ".concat(data.maxCommentPostLength.toString())), findViewById(android.R.id.content), getApplicationContext());
                ;
                return;
            }

            functions.showProgress(lottie);
            HashMap<String, String> postData = new HashMap<>();
            postData.put("userID", posterUserID);
            postData.put("postID", postID);
            postData.put("content", content);

            networkController = new NetworkController(this, new NetworkController.IResult() {
                @Override
                public void notifySuccess(String response) throws JSONException {
                    Log.i("Response", response.toString());
                    functions.hideProgress(lottie);
                    if (functions.isJsonObject(response)){
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
                    ;

                }
            });
            networkController.PostMethod(data.send_comment_Api, postData);
            //Toast.makeText(getApplicationContext(),getString(R.string.posting_comment),Toast.LENGTH_SHORT).show();
            btnSendTimelineComment.setEnabled(false);
        });

        //CLOSE ON CREATE
    }


    @Override
    protected void onResume() {
        super.onResume();


    }
}